package com.sea.desafio_backend.service;

import com.sea.desafio_backend.exception.ResourceNotFoundException;
import com.sea.desafio_backend.model.entity.ClienteEmail;
import com.sea.desafio_backend.repository.EmailRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service para gerenciamento de Emails
 * Responsável por CRUD e validações (email principal)
 */
@Service
@Slf4j
public class EmailService {

    private final EmailRepository emailRepository;

    public EmailService(EmailRepository emailRepository) {
        this.emailRepository = emailRepository;
    }

    // ==================== CRIAR EMAIL ====================

    /**
     * Cria um novo email
     */
    @Transactional
    public ClienteEmail criarEmail(ClienteEmail email) {
        log.info("Criando novo email para cliente ID: {}", email.getCliente().getId());
        
        // Se marcar como principal, desmarca os outros
        if (Boolean.TRUE.equals(email.getPrincipal())) {
            desmarcarEmailsPrincipais(email.getCliente().getId());
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
        
        emailExistente.setEnderecoEmail(emailAtualizado.getEnderecoEmail());
        
        // Se marcar como principal, desmarca os outros
        if (Boolean.TRUE.equals(emailAtualizado.getPrincipal()) && 
            !Boolean.TRUE.equals(emailExistente.getPrincipal())) {
            desmarcarEmailsPrincipais(emailExistente.getCliente().getId());
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
        
        // Valida se não é o último email (cliente precisa ter pelo menos 1)
        long totalEmails = contarEmails(clienteId);
        if (totalEmails <= 1) {
            throw new IllegalArgumentException("Cliente deve ter pelo menos um email cadastrado");
        }
        
        emailRepository.deleteById(id);
        log.info("Email deletado com sucesso. ID: {}", id);
    }

    // ==================== MÉTODOS AUXILIARES ====================

    private void desmarcarEmailsPrincipais(Long clienteId) {
        List<ClienteEmail> emails = listarPorCliente(clienteId);
        emails.forEach(e -> {
            if (Boolean.TRUE.equals(e.getPrincipal())) {
                e.setPrincipal(false);
                emailRepository.save(e);
            }
        });
    }
}
