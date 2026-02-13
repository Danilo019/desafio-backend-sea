package com.sea.desafio_backend.service;

import com.sea.desafio_backend.exception.ResourceNotFoundException;
import com.sea.desafio_backend.model.entity.Cliente;
import com.sea.desafio_backend.model.entity.ClienteEmail;
import com.sea.desafio_backend.model.entity.Endereco;
import com.sea.desafio_backend.model.entity.Telefone;
import com.sea.desafio_backend.repository.ClienteRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Service para gerenciamento de Clientes
 * Orquestra todos os outros services (Endereco, Telefone, Email)
 * Responsável por validações de CPF, nome e integridade dos dados
 */
@Service
@Slf4j
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final EnderecoService enderecoService;
    private final TelefoneService telefoneService;
    private final EmailService emailService;

    public ClienteService(ClienteRepository clienteRepository, 
                         EnderecoService enderecoService,
                         TelefoneService telefoneService, 
                         EmailService emailService) {
        this.clienteRepository = clienteRepository;
        this.enderecoService = enderecoService;
        this.telefoneService = telefoneService;
        this.emailService = emailService;
    }

    // ==================== CRIAR CLIENTE ====================

    /**
     * Cria um novo cliente completo (com endereço, telefones e emails)
     */
    @Transactional
    public Cliente criarCliente(Cliente cliente) {
        log.info("Criando novo cliente: {}", cliente.getNome());
        
        // Valida CPF único
        validarCpfUnico(cliente.getCpf(), null);
        
        // Valida dados mínimos
        validarDadosMinimos(cliente);
        
        // Salva cliente (sem relacionamentos primeiro)
        Cliente clienteSalvo = clienteRepository.save(cliente);
        log.info("Cliente criado com sucesso. ID: {}", clienteSalvo.getId());
        
        // Salva endereço (relacionamento 1:1)
        if (cliente.getEndereco() != null) {
            Endereco endereco = cliente.getEndereco();
            endereco.setCliente(clienteSalvo);
            Endereco enderecoSalvo = enderecoService.criarEndereco(endereco);
            clienteSalvo.setEndereco(enderecoSalvo);
        }
        
        // Salva telefones (relacionamento 1:N)
        if (cliente.getTelefones() != null && !cliente.getTelefones().isEmpty()) {
            List<Telefone> telefonesSalvos = new ArrayList<>();
            for (Telefone telefone : cliente.getTelefones()) {
                telefone.setCliente(clienteSalvo);
                Telefone telefoneSalvo = telefoneService.criarTelefone(telefone);
                telefonesSalvos.add(telefoneSalvo);
            }
            clienteSalvo.setTelefones(telefonesSalvos);
        }
        
        // Salva emails (relacionamento 1:N)
        if (cliente.getEmails() != null && !cliente.getEmails().isEmpty()) {
            List<ClienteEmail> emailsSalvos = new ArrayList<>();
            for (ClienteEmail email : cliente.getEmails()) {
                email.setCliente(clienteSalvo);
                ClienteEmail emailSalvo = emailService.criarEmail(email);
                emailsSalvos.add(emailSalvo);
            }
            clienteSalvo.setEmails(emailsSalvos);
        }
        
        return clienteSalvo;
    }

    // ==================== BUSCAR CLIENTES ====================

    public Cliente buscarPorId(Long id) {
        log.info("Buscando cliente por ID: {}", id);
        return clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", id));
    }

    public Cliente buscarPorCpf(String cpf) {
        log.info("Buscando cliente por CPF: {}", cpf);
        return clienteRepository.findByCpf(cpf)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", "cpf", cpf));
    }

    public List<Cliente> listarTodos() {
        log.info("Listando todos os clientes");
        return clienteRepository.findAll();
    }

    public List<Cliente> listarTodosComEndereco() {
        log.info("Listando todos os clientes com endereço");
        return clienteRepository.findAllWithEndereco();
    }

    /**
     * Busca cliente completo (com todos os relacionamentos)
     */
    public Cliente buscarClienteCompleto(Long id) {
        log.info("Buscando cliente completo com todos os relacionamentos. ID: {}", id);
        
        Cliente cliente = buscarPorId(id);
        
        // Carrega endereço
        try {
            Endereco endereco = enderecoService.buscarPorCliente(id);
            cliente.setEndereco(endereco);
        } catch (ResourceNotFoundException e) {
            log.warn("Cliente sem endereço cadastrado. ID: {}", id);
        }
        
        // Carrega telefones
        List<Telefone> telefones = telefoneService.listarPorCliente(id);
        cliente.setTelefones(telefones);
        
        // Carrega emails
        List<ClienteEmail> emails = emailService.listarPorCliente(id);
        cliente.setEmails(emails);
        
        return cliente;
    }

    // ==================== ATUALIZAR CLIENTE ====================

    @Transactional
    public Cliente atualizarCliente(Long id, Cliente clienteAtualizado) {
        log.info("Atualizando cliente ID: {}", id);
        
        Cliente clienteExistente = buscarPorId(id);
        
        // Valida CPF único (se mudou)
        if (!clienteExistente.getCpf().equals(clienteAtualizado.getCpf())) {
            validarCpfUnico(clienteAtualizado.getCpf(), id);
        }
        
        // Atualiza dados básicos
        clienteExistente.setNome(clienteAtualizado.getNome());
        clienteExistente.setCpf(clienteAtualizado.getCpf());
        
        Cliente clienteSalvo = clienteRepository.save(clienteExistente);
        log.info("Cliente atualizado com sucesso. ID: {}", id);
        
        return clienteSalvo;
    }

    /**
     * Atualiza endereço do cliente
     */
    @Transactional
    public Cliente atualizarEndereco(Long clienteId, Endereco novoEndereco) {
        log.info("Atualizando endereço do cliente ID: {}", clienteId);
        
        Cliente cliente = buscarPorId(clienteId);
        
        try {
            // Busca endereço existente
            Endereco enderecoExistente = enderecoService.buscarPorCliente(clienteId);
            
            // Atualiza endereço existente
            enderecoService.atualizarEndereco(enderecoExistente.getId(), novoEndereco);
            
        } catch (ResourceNotFoundException e) {
            // Se não existe, cria novo
            novoEndereco.setCliente(cliente);
            enderecoService.criarEndereco(novoEndereco);
        }
        
        return buscarClienteCompleto(clienteId);
    }

    /**
     * Adiciona telefone ao cliente
     */
    @Transactional
    public Cliente adicionarTelefone(Long clienteId, Telefone telefone) {
        log.info("Adicionando telefone ao cliente ID: {}", clienteId);
        
        Cliente cliente = buscarPorId(clienteId);
        telefone.setCliente(cliente);
        
        telefoneService.criarTelefone(telefone);
        
        return buscarClienteCompleto(clienteId);
    }

    /**
     * Adiciona email ao cliente
     */
    @Transactional
    public Cliente adicionarEmail(Long clienteId, ClienteEmail email) {
        log.info("Adicionando email ao cliente ID: {}", clienteId);
        
        Cliente cliente = buscarPorId(clienteId);
        email.setCliente(cliente);
        
        emailService.criarEmail(email);
        
        return buscarClienteCompleto(clienteId);
    }

    // ==================== DELETAR CLIENTE ====================

    @Transactional
    public void deletarCliente(Long id) {
        log.info("Deletando cliente ID: {}", id);
        
        Cliente cliente = buscarPorId(id);
        
        // Deleta relacionamentos (cascata automático via JPA)
        clienteRepository.deleteById(id);
        
        log.info("Cliente deletado com sucesso. ID: {}", id);
    }

    // ==================== VALIDAÇÕES ====================

    private void validarCpfUnico(String cpf, Long clienteId) {
        boolean existe = clienteRepository.existsByCpf(cpf);
        
        if (existe) {
            // Se é atualização, verifica se o CPF é do próprio cliente
            if (clienteId != null) {
                Cliente clienteExistente = clienteRepository.findByCpf(cpf).orElse(null);
                if (clienteExistente != null && !clienteExistente.getId().equals(clienteId)) {
                    throw new IllegalArgumentException("CPF já cadastrado: " + cpf);
                }
            } else {
                throw new IllegalArgumentException("CPF já cadastrado: " + cpf);
            }
        }
    }

    private void validarDadosMinimos(Cliente cliente) {
        // Valida telefones
        if (cliente.getTelefones() == null || cliente.getTelefones().isEmpty()) {
            throw new IllegalArgumentException("Cliente deve ter pelo menos um telefone");
        }
        
        // Valida emails
        if (cliente.getEmails() == null || cliente.getEmails().isEmpty()) {
            throw new IllegalArgumentException("Cliente deve ter pelo menos um email");
        }
        
        // Valida telefone principal
        boolean temTelefonePrincipal = cliente.getTelefones().stream()
                .anyMatch(t -> Boolean.TRUE.equals(t.getPrincipal()));
        
        if (!temTelefonePrincipal && !cliente.getTelefones().isEmpty()) {
            // Se não tem principal, marca o primeiro como principal
            cliente.getTelefones().get(0).setPrincipal(true);
        }
        
        // Valida email principal
        boolean temEmailPrincipal = cliente.getEmails().stream()
                .anyMatch(e -> Boolean.TRUE.equals(e.getPrincipal()));
        
        if (!temEmailPrincipal && !cliente.getEmails().isEmpty()) {
            // Se não tem principal, marca o primeiro como principal
            cliente.getEmails().get(0).setPrincipal(true);
        }
    }

    // ==================== MÉTODOS AUXILIARES ====================

    /**
     * Remove máscara do CPF
     * Exemplo: 123.456.789-00 → 12345678900
     */
    public String removerMascaraCPF(String cpf) {
        if (cpf == null) {
            return "";
        }
        return cpf.replaceAll("[^0-9]", "");
    }

    /**
     * Aplica máscara no CPF
     * Exemplo: 12345678900 → 123.456.789-00
     */
    public String aplicarMascaraCPF(String cpf) {
        String cpfLimpo = removerMascaraCPF(cpf);
        
        if (cpfLimpo.length() == 11) {
            return cpfLimpo.replaceAll("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4");
        }
        
        return cpf;
    }

    /**
     * Verifica se CPF é válido (algoritmo simplificado)
     */
    public boolean validarCPF(String cpf) {
        String cpfLimpo = removerMascaraCPF(cpf);
        
        // Valida tamanho
        if (cpfLimpo.length() != 11) {
            return false;
        }
        
        // Valida se não é sequência conhecida (111.111.111-11, etc)
        if (cpfLimpo.matches("(\\d)\\1{10}")) {
            return false;
        }
        
        // Aqui poderia implementar validação completa dos dígitos verificadores
        // Por simplicidade, apenas valida formato e sequências
        
        return true;
    }
}
