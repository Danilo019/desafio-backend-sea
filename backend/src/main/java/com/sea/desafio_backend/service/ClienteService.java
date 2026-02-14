package com.sea.desafio_backend.service;

import com.sea.desafio_backend.dto.request.ClienteRequest;
import com.sea.desafio_backend.exception.ResourceNotFoundException;
import com.sea.desafio_backend.model.entity.Cliente;
import com.sea.desafio_backend.model.entity.ClienteEmail;
import com.sea.desafio_backend.model.entity.Endereco;
import com.sea.desafio_backend.model.entity.Telefone;
import com.sea.desafio_backend.repository.ClienteRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service para gerenciamento de Clientes
 * Orquestra validações e persistência em cascata
 */
@Service
@Slf4j
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final EnderecoService enderecoService;
    private final TelefoneService telefoneService;
    private final EmailService emailService;

    public ClienteService(ClienteRepository clienteRepository,EnderecoService enderecoService,TelefoneService telefoneService, EmailService emailService) {
        this.clienteRepository = clienteRepository;
        this.enderecoService = enderecoService;
        this.telefoneService = telefoneService;
        this.emailService = emailService;
    }

    // ==================== CRIAR CLIENTE ====================

    @Transactional
    public Cliente criarCliente(@Valid ClienteRequest request) {

        log.info("Criando novo cliente: {}", request.getNome());

        // Converte DTO → Entity
        Cliente cliente = new Cliente();
        cliente.setNome(request.getNome());
        
        // Remove máscara e valida CPF
        String cpfLimpo = removerMascaraCPF(request.getCpf());
        if (!validarCPF(cpfLimpo)) {
            throw new IllegalArgumentException("CPF inválido");
        }
        validarCpfUnico(cpfLimpo, null);
        cliente.setCpf(aplicarMascaraCPF(cpfLimpo));

        // Converte Endereco
        if (request.getEndereco() != null) {
            Endereco endereco = new Endereco();
            endereco.setCep(enderecoService.removerMascaraCEP(request.getEndereco().getCep()));
            endereco.setLogradouro(request.getEndereco().getLogradouro());
            endereco.setComplemento(request.getEndereco().getComplemento());
            endereco.setBairro(request.getEndereco().getBairro());
            endereco.setCidade(request.getEndereco().getCidade());
            endereco.setUf(request.getEndereco().getUf());
            endereco.setCliente(cliente);
            cliente.setEndereco(endereco);
        }

        // Converte Telefones
        if (request.getTelefones() != null && !request.getTelefones().isEmpty()) {
            List<Telefone> telefones = request.getTelefones().stream()
                    .map(t -> {
                        Telefone telefone = new Telefone();
                        telefone.setNumero(telefoneService.removerMascaraTelefone(t.getNumero()));
                        telefone.setTipo(t.getTipo());
                        telefone.setPrincipal(t.getPrincipal() != null ? t.getPrincipal() : false);
                        telefone.setCliente(cliente);
                        return telefone;
                    })
                    .collect(Collectors.toList());
            cliente.setTelefones(telefones);
        }

        // Converte Emails
        if (request.getEmails() != null && !request.getEmails().isEmpty()) {
            List<ClienteEmail> emails = request.getEmails().stream()
                    .map(e -> {
                        ClienteEmail email = new ClienteEmail();
                        email.setEnderecoEmail(e.getEnderecoEmail());
                        email.setPrincipal(e.getPrincipal() != null ? e.getPrincipal() : false);
                        email.setCliente(cliente);
                        return email;
                    })
                    .collect(Collectors.toList());
            cliente.setEmails(emails);
        }

        // Valida regras de negócio
        validarDadosMinimos(cliente);

        // Salva tudo com cascade
        Cliente clienteSalvo = clienteRepository.save(cliente);

        log.info("Cliente e dependências criados com sucesso. ID: {}", clienteSalvo.getId());

        return clienteSalvo;
    }

    // ==================== BUSCAR CLIENTES ====================

    public Cliente buscarPorId(Long id) {
        log.info("Buscando cliente por ID: {}", id);
        return clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", id));
    }

    public List<Cliente> listarTodos() {
        log.info("Listando todos os clientes");
        return clienteRepository.findAll();
    }

    // ==================== ATUALIZAR CLIENTE ====================

    @Transactional
    public Cliente atualizarCliente(Long id, Cliente clienteAtualizado) {
        log.info("Atualizando cliente ID: {}", id);

        Cliente clienteExistente = buscarPorId(id);

        // Garante a máscara no CPF novo
        String novoCpfMascarado = aplicarMascaraCPF(clienteAtualizado.getCpf());

        // Valida CPF único se houver alteração
        if (!clienteExistente.getCpf().equals(novoCpfMascarado)) {
            if (!validarCPF(novoCpfMascarado)) {
                throw new IllegalArgumentException("Formato de CPF inválido");
            }
            validarCpfUnico(novoCpfMascarado, id);
        }

        // Atualiza dados básicos
        clienteExistente.setNome(clienteAtualizado.getNome());
        clienteExistente.setCpf(novoCpfMascarado);

        Cliente clienteSalvo = clienteRepository.save(clienteExistente);
        log.info("Cliente atualizado com sucesso. ID: {}", id);

        return clienteSalvo;
    }

    // ==================== DELETAR CLIENTE ====================

    @Transactional
    public void deletarCliente(Long id) {
        log.info("Deletando cliente ID: {}", id);
        Cliente cliente = buscarPorId(id);

        // Deleta relacionamentos automaticamente (cascata via JPA orphanRemoval=true)
        clienteRepository.delete(cliente);

        log.info("Cliente deletado com sucesso. ID: {}", id);
    }

    // ==================== VALIDAÇÕES ====================

    private void validarCpfUnico(String cpf, Long clienteId) {
        Optional<Cliente> clienteExistente = clienteRepository.findByCpf(cpf);

        if (clienteExistente.isPresent()) {
            if (clienteId == null || !clienteExistente.get().getId().equals(clienteId)) {
                throw new IllegalArgumentException("CPF já cadastrado: " + cpf);
            }
        }
    }

    private void validarDadosMinimos(Cliente cliente) {
        if (cliente.getTelefones() == null || cliente.getTelefones().isEmpty()) {
            throw new IllegalArgumentException("Cliente deve ter pelo menos um telefone");
        }
        if (cliente.getEmails() == null || cliente.getEmails().isEmpty()) {
            throw new IllegalArgumentException("Cliente deve ter pelo menos um email");
        }

        boolean temTelefonePrincipal = cliente.getTelefones().stream()
                .anyMatch(t -> Boolean.TRUE.equals(t.getPrincipal()));
        if (!temTelefonePrincipal && !cliente.getTelefones().isEmpty()) {
            cliente.getTelefones().get(0).setPrincipal(true);
        }

        boolean temEmailPrincipal = cliente.getEmails().stream()
                .anyMatch(e -> Boolean.TRUE.equals(e.getPrincipal()));
        if (!temEmailPrincipal && !cliente.getEmails().isEmpty()) {
            cliente.getEmails().get(0).setPrincipal(true);
        }
    }

    // ==================== MÉTODOS AUXILIARES ====================

    public String removerMascaraCPF(String cpf) {
        if (cpf == null) return "";
        return cpf.replaceAll("[^0-9]", "");
    }

    public String aplicarMascaraCPF(String cpf) {
        String cpfLimpo = removerMascaraCPF(cpf);
        if (cpfLimpo.length() == 11) {
            // Usando replaceFirst para melhor performance
            return cpfLimpo.replaceFirst("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4");
        }
        return cpf;
    }

    public boolean validarCPF(String cpf) {
        String cpfLimpo = removerMascaraCPF(cpf);
        if (cpfLimpo.length() != 11) return false;
        if (cpfLimpo.matches("(\\d)\\1{10}")) return false; // Impede 11111111111
        return true;
    }

    public Cliente buscarPorCpf(String cpf) {
        log.info("Buscando cliente por CPF: {}", cpf);
        String cpfComMascara = aplicarMascaraCPF(cpf);
        return clienteRepository.findByCpf(cpfComMascara)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente com CPF " + cpfComMascara + " não encontrado"));
    }
}