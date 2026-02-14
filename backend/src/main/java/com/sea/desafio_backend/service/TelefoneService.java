package com.sea.desafio_backend.service;

import com.sea.desafio_backend.exception.ResourceNotFoundException;
import com.sea.desafio_backend.model.entity.Telefone;
import com.sea.desafio_backend.model.enums.TipoTelefone;
import com.sea.desafio_backend.repository.TelefoneRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service para gerenciamento de Telefones
 * Responsável por CRUD e validações (telefone principal, máscaras)
 */
@Service
@Slf4j
public class TelefoneService {

    private final TelefoneRepository telefoneRepository;

    public TelefoneService(TelefoneRepository telefoneRepository) {
        this.telefoneRepository = telefoneRepository;
    }

    // ==================== CRIAR TELEFONE ====================

    /**
     * Cria um novo telefone
     * Remove máscara do número antes de salvar
     * ✅ MELHORIA: Primeiro telefone automaticamente é principal
     * ✅ MELHORIA: Valida duplicidade de número
     *
     * @param telefone Telefone a ser criado
     * @return Telefone salvo
     */
    @Transactional
    public Telefone criarTelefone(Telefone telefone) {
        Long clienteId = telefone.getCliente().getId();
        log.info("Criando novo telefone para cliente ID: {}", clienteId);

        // Remove máscara do telefone antes de salvar
        String numeroSemMascara = removerMascaraTelefone(telefone.getNumero());
        telefone.setNumero(numeroSemMascara);

        // ✅ MELHORIA: Valida duplicidade
        validarTelefoneDuplicado(clienteId, numeroSemMascara, null);

        // ✅ MELHORIA: Regra de Negócio - Primeiro telefone DEVE ser o principal
        long totalTelefones = contarTelefones(clienteId);
        if (totalTelefones == 0) {
            telefone.setPrincipal(true);
        } else if (Boolean.TRUE.equals(telefone.getPrincipal())) {
            // Se já existem e quer ser o principal, desmarca os outros
            desmarcarTelefonesPrincipais(clienteId);
        } else {
            telefone.setPrincipal(false); // Null safety
        }

        Telefone telefoneSalvo = telefoneRepository.save(telefone);
        log.info("Telefone criado com sucesso. ID: {}", telefoneSalvo.getId());

        return telefoneSalvo;
    }

    // ==================== BUSCAR TELEFONES ====================

    /**
     * Busca telefone por ID
     *
     * @param id ID do telefone
     * @return Telefone encontrado
     */
    public Telefone buscarPorId(Long id) {
        log.info("Buscando telefone por ID: {}", id);
        return telefoneRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Telefone", id));
    }
    /**
     * Lista todos os telefones de um cliente
     *
     * @param clienteId ID do cliente
     * @return Lista de telefones
     */
    public List<Telefone> listarPorCliente(Long clienteId) {
        log.info("Listando telefones do cliente ID: {}", clienteId);
        return telefoneRepository.findByClienteId(clienteId);
    }

    /**
     * Busca telefone principal do cliente
     *
     * @param clienteId ID do cliente
     * @return Telefone principal (se existir)
     */
    public Telefone buscarTelefonePrincipal(Long clienteId) {
        log.info("Buscando telefone principal do cliente ID: {}", clienteId);
        return telefoneRepository.findByClienteIdAndPrincipal(clienteId, true)
                .orElse(null);
    }

    /**
     * Conta quantos telefones o cliente possui
     *
     * @param clienteId ID do cliente
     * @return Número de telefones
     */
    public long contarTelefones(Long clienteId) {
        return telefoneRepository.countByClienteId(clienteId);
    }

    // ==================== ATUALIZAR TELEFONE ====================

    /**
     * Atualiza dados do telefone
     * ✅ MELHORIA: Valida duplicidade ao mudar número
     *
     * @param id ID do telefone
     * @param telefoneAtualizado Dados atualizados
     * @return Telefone atualizado
     */
    @Transactional
    public Telefone atualizarTelefone(Long id, Telefone telefoneAtualizado) {
        log.info("Atualizando telefone ID: {}", id);

        Telefone telefoneExistente = buscarPorId(id);
        Long clienteId = telefoneExistente.getCliente().getId();

        // Remove máscara antes de salvar
        String numeroSemMascara = removerMascaraTelefone(telefoneAtualizado.getNumero());
        
        // ✅ MELHORIA: Valida duplicidade se o número mudou
        if (!telefoneExistente.getNumero().equals(numeroSemMascara)) {
            validarTelefoneDuplicado(clienteId, numeroSemMascara, id);
        }
        
        telefoneExistente.setNumero(numeroSemMascara);
        telefoneExistente.setTipo(telefoneAtualizado.getTipo());

        // Se marcar como principal, desmarca os outros
        if (Boolean.TRUE.equals(telefoneAtualizado.getPrincipal()) &&
                !Boolean.TRUE.equals(telefoneExistente.getPrincipal())) {
            desmarcarTelefonesPrincipais(clienteId);
        }

        telefoneExistente.setPrincipal(telefoneAtualizado.getPrincipal());

        Telefone telefoneSalvo = telefoneRepository.save(telefoneExistente);
        log.info("Telefone atualizado com sucesso. ID: {}", id);

        return telefoneSalvo;
    }

    /**
     * Define telefone como principal (desmarca os outros)
     *
     * @param id ID do telefone
     */
    @Transactional
    public void definirComoPrincipal(Long id) {
        log.info("Definindo telefone ID: {} como principal", id);

        Telefone telefone = buscarPorId(id);

        // Desmarca todos os outros telefones do cliente
        desmarcarTelefonesPrincipais(telefone.getCliente().getId());

        // Marca este como principal
        telefone.setPrincipal(true);
        telefoneRepository.save(telefone);

        log.info("Telefone marcado como principal");
    }

    // ==================== DELETAR TELEFONE ====================

    /**
     * Deleta telefone
     * Valida se não é o último telefone do cliente
     * ✅ MELHORIA: Elege automaticamente novo principal se deletar o principal
     *
     * @param id ID do telefone
     * @throws IllegalArgumentException se for o último telefone
     */
    @Transactional
    public void deletarTelefone(Long id) {
        log.info("Deletando telefone ID: {}", id);

        Telefone telefone = buscarPorId(id);
        Long clienteId = telefone.getCliente().getId();
        boolean eraPrincipal = Boolean.TRUE.equals(telefone.getPrincipal());

        // 1. Valida se não é o último telefone (cliente precisa ter pelo menos 1)
        long totalTelefones = contarTelefones(clienteId);
        if (totalTelefones <= 1) {
            throw new IllegalArgumentException("Cliente deve ter pelo menos um telefone cadastrado");
        }

        // 2. Deleta o telefone
        telefoneRepository.delete(telefone);
        telefoneRepository.flush(); // ✅ Força exclusão no banco agora

        // 3. ✅ MELHORIA: Se deletou o principal, elege novo automaticamente
        if (eraPrincipal) {
            log.info("Telefone principal deletado. Elegendo um novo principal automaticamente.");
            List<Telefone> restantes = listarPorCliente(clienteId);
            if (!restantes.isEmpty()) {
                Telefone novoPrincipal = restantes.get(0);
                novoPrincipal.setPrincipal(true);
                telefoneRepository.save(novoPrincipal);
                log.info("Novo telefone principal eleito: ID {}", novoPrincipal.getId());
            }
        }

        log.info("Telefone deletado com sucesso. ID: {}", id);
    }

    // ==================== MÉTODOS AUXILIARES ====================

    /**
     * Remove máscara do telefone (mantém apenas números)
     * Exemplo: (11) 98765-4321 → 11987654321
     *
     * @param telefone Telefone com ou sem máscara
     * @return Telefone apenas com números
     */
    public String removerMascaraTelefone(String telefone) {
        if (telefone == null) {
            return "";
        }
        return telefone.replaceAll("[^0-9]", "");
    }

    /**
     * Aplica máscara no telefone conforme o tipo
     *
     * @param numero Número sem máscara (apenas dígitos)
     * @param tipo Tipo do telefone (RESIDENCIAL, COMERCIAL, CELULAR)
     * @return Telefone formatado com máscara
     */
    public String aplicarMascaraTelefone(String numero, TipoTelefone tipo) {
        String numeroLimpo = removerMascaraTelefone(numero);

        if (tipo == TipoTelefone.CELULAR && numeroLimpo.length() == 11) {
            // (11) 98765-4321
            return numeroLimpo.replaceAll("(\\d{2})(\\d{5})(\\d{4})", "($1) $2-$3");
        } else if (numeroLimpo.length() == 10) {
            // (11) 3456-7890
            return numeroLimpo.replaceAll("(\\d{2})(\\d{4})(\\d{4})", "($1) $2-$3");
        }

        return numero; // Retorna original se não couber no padrão
    }

    /**
     * ✅ PERFORMANCE: Desmarca todos os telefones principais em 1 UPDATE
     * Evita N+1 Problem (buscar todos + loop de saves)
     * Requer método no Repository com @Modifying
     *
     * @param clienteId ID do cliente
     */
    private void desmarcarTelefonesPrincipais(Long clienteId) {
        log.debug("Desmarcando telefones principais do cliente ID: {}", clienteId);
        telefoneRepository.desmarcarTodosPrincipaisPorCliente(clienteId);
    }

    /**
     * ✅ PERFORMANCE: Valida duplicidade de forma otimizada (1 query)
     * Requer método no Repository: Optional<Telefone> findByClienteIdAndNumero(Long clienteId, String numero)
     *
     * @param clienteId ID do cliente
     * @param numero Número do telefone (sem máscara)
     * @param telefoneIdIgnorar ID do telefone a ignorar (útil em updates)
     */
    private void validarTelefoneDuplicado(Long clienteId, String numero, Long telefoneIdIgnorar) {
        Optional<Telefone> telefoneExistenteOpt = telefoneRepository.findByClienteIdAndNumero(clienteId, numero);

        if (telefoneExistenteOpt.isPresent()) {
            Telefone telefoneExistente = telefoneExistenteOpt.get();
            // Se for criação (ignorar null) ou se o ID encontrado for diferente do ID atual
            if (telefoneIdIgnorar == null || !telefoneExistente.getId().equals(telefoneIdIgnorar)) {
                throw new IllegalArgumentException("Este número de telefone já está cadastrado para este cliente.");
            }
        }
    }
}