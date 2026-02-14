package com.sea.desafio_backend.service;

import com.sea.desafio_backend.exception.ResourceNotFoundException;
import com.sea.desafio_backend.model.entity.ClienteEmail;
import com.sea.desafio_backend.repository.EmailRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service para gerenciamento de Emails
 * Responsável por CRUD, validações (email principal, duplicidade) e performance
 */
@Service
@Slf4j
public class EmailService {

    private final EmailRepository emailRepository;

    public EmailService(EmailRepository emailRepository) {
        this.emailRepository = emailRepository;
    }

    // ==================== CRIAR EMAIL ====================

    @Transactional
    public ClienteEmail criarEmail(ClienteEmail email) {
        Long clienteId = email.getCliente().getId();
        log.info("Criando novo email para cliente ID: {}", clienteId);

        // 1. Validação de duplicidade
        validarEmailDuplicado(clienteId, email.getEnderecoEmail(), null);

        // 2. Regra de Negócio: Primeiro email DEVE ser o principal
        long totalEmails = contarEmails(clienteId);
        if (totalEmails == 0) {
            email.setPrincipal(true);
        } else if (Boolean.TRUE.equals(email.getPrincipal())) {
            // Se já existem e quer ser o principal, desmarca os outros
            desmarcarEmailsPrincipais(clienteId);
        } else {
            email.setPrincipal(false); // Null safety
        }

        ClienteEmail emailSalvo = emailRepository.save(email);
        log.info("Email criado com sucesso. ID: {}", emailSalvo.getId());

        return emailSalvo;
    }

    // ==================== BUSCAR EMAILS ====================

    public ClienteEmail buscarPorId(Long id) {
        log.info("Buscando email por ID: {}", id);
        return emailRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Email", id));
    }

    public List<ClienteEmail> listarPorCliente(Long clienteId) {
        log.info("Listando emails do cliente ID: {}", clienteId);
        return emailRepository.findByClienteId(clienteId);
    }

    public ClienteEmail buscarEmailPrincipal(Long clienteId) {
        log.info("Buscando email principal do cliente ID: {}", clienteId);
        return emailRepository.findByClienteIdAndPrincipal(clienteId, true)
                .orElse(null);
    }

    public long contarEmails(Long clienteId) {
        return emailRepository.countByClienteId(clienteId);
    }

    // ==================== ATUALIZAR EMAIL ====================

    @Transactional
    public ClienteEmail atualizarEmail(Long id, ClienteEmail emailAtualizado) {
        log.info("Atualizando email ID: {}", id);

        ClienteEmail emailExistente = buscarPorId(id);
        Long clienteId = emailExistente.getCliente().getId();

        // Valida duplicidade se o endereço mudou
        if (!emailExistente.getEnderecoEmail().equalsIgnoreCase(emailAtualizado.getEnderecoEmail())) {
            validarEmailDuplicado(clienteId, emailAtualizado.getEnderecoEmail(), id);
        }

        emailExistente.setEnderecoEmail(emailAtualizado.getEnderecoEmail());

        // Se marcar como principal, desmarca os outros
        if (Boolean.TRUE.equals(emailAtualizado.getPrincipal()) &&
                !Boolean.TRUE.equals(emailExistente.getPrincipal())) {
            desmarcarEmailsPrincipais(clienteId);
        }

        emailExistente.setPrincipal(emailAtualizado.getPrincipal());

        ClienteEmail emailSalvo = emailRepository.save(emailExistente);
        log.info("Email atualizado com sucesso. ID: {}", id);

        return emailSalvo;
    }

    @Transactional
    public void definirComoPrincipal(Long id) {
        log.info("Definindo email ID: {} como principal", id);

        ClienteEmail email = buscarPorId(id);
        desmarcarEmailsPrincipais(email.getCliente().getId());

        email.setPrincipal(true);
        emailRepository.save(email);

        log.info("Email marcado como principal");
    }

    // ==================== DELETAR EMAIL ====================

    @Transactional
    public void deletarEmail(Long id) {
        log.info("Deletando email ID: {}", id);

        ClienteEmail email = buscarPorId(id);
        Long clienteId = email.getCliente().getId();
        boolean eraPrincipal = Boolean.TRUE.equals(email.getPrincipal());

        // 1. Valida se não é o último email
        long totalEmails = contarEmails(clienteId);
        if (totalEmails <= 1) {
            throw new IllegalArgumentException("Cliente deve ter pelo menos um email cadastrado");
        }

        // 2. Deleta o email
        emailRepository.delete(email);
        emailRepository.flush(); // Força exclusão no banco agora para a próxima query vir limpa

        // 3. Se deletou o principal, elege novo automaticamente
        if (eraPrincipal) {
            log.info("Email principal deletado. Elegendo um novo principal automaticamente.");
            List<ClienteEmail> restantes = listarPorCliente(clienteId);
            if (!restantes.isEmpty()) {
                ClienteEmail novoPrincipal = restantes.get(0);
                novoPrincipal.setPrincipal(true);
                emailRepository.save(novoPrincipal);
                log.info("Novo email principal eleito: ID {}", novoPrincipal.getId());
            }
        }

        log.info("Email deletado com sucesso. ID: {}", id);
    }

    // ==================== MÉTODOS AUXILIARES ====================

    /**
     * Valida duplicidade de forma performática (1 query apenas).
     * Requer método no Repository: Optional<ClienteEmail> findByClienteIdAndEnderecoEmail(Long clienteId, String enderecoEmail)
     */
    private void validarEmailDuplicado(Long clienteId, String enderecoEmail, Long emailIdIgnorar) {
        Optional<ClienteEmail> emailExistenteOpt = emailRepository.findByClienteIdAndEnderecoEmail(clienteId, enderecoEmail);

        if (emailExistenteOpt.isPresent()) {
            ClienteEmail emailExistente = emailExistenteOpt.get();
            // Se for criação (ignorar null) ou se o ID encontrado for diferente do ID atual
            if (emailIdIgnorar == null || !emailExistente.getId().equals(emailIdIgnorar)) {
                throw new IllegalArgumentException("Este e-mail já está cadastrado para este cliente.");
            }
        }
    }

    /**
     * Requer método no Repository:
     * @Modifying
     * @Query("UPDATE ClienteEmail e SET e.principal = false WHERE e.cliente.id = :clienteId")
     * void desmarcarTodosPrincipaisPorCliente(@Param("clienteId") Long clienteId);
     */
    private void desmarcarEmailsPrincipais(Long clienteId) {
        log.debug("Desmarcando emails principais do cliente ID: {}", clienteId);
        emailRepository.desmarcarTodosPrincipaisPorCliente(clienteId);
    }
}