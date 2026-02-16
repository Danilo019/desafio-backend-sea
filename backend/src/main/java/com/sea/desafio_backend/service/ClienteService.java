package com.sea.desafio_backend.service;

import com.sea.desafio_backend.dto.request.ClienteRequest;
import com.sea.desafio_backend.exception.CpfInvalidoException;
import com.sea.desafio_backend.exception.CpfJaCadastradoException;
import com.sea.desafio_backend.exception.DadosMinimosException;
import com.sea.desafio_backend.exception.ResourceNotFoundException;
import com.sea.desafio_backend.model.entity.Cliente;
import com.sea.desafio_backend.model.entity.ClienteEmail;
import com.sea.desafio_backend.model.entity.Endereco;
import com.sea.desafio_backend.model.entity.Telefone;
import com.sea.desafio_backend.repository.ClienteRepository;
import com.sea.desafio_backend.util.CpfUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

        // Valida e processa CPF
        String cpfProcessado = validarEProcessarCpf(request.getCpf(), null);

        // Converte DTO → Entity
        Cliente cliente = converterRequestParaEntity(request, cpfProcessado);

        // Valida regras de negócio
        validarDadosMinimos(cliente);

        // Salva tudo com cascade
        Cliente clienteSalvo = clienteRepository.save(cliente);
        log.info("Cliente e dependências criados com sucesso. ID: {}", clienteSalvo.getId());

        return clienteSalvo;
    }

    /**
     * Converte ClienteRequest para entidade Cliente
     */
    private Cliente converterRequestParaEntity(ClienteRequest request, String cpfProcessado) {
        Cliente cliente = new Cliente();
        cliente.setNome(request.getNome());
        cliente.setCpf(cpfProcessado);

        // Converte e associa entidades relacionadas
        if (request.getEndereco() != null) {
            cliente.setEndereco(converterEndereco(request, cliente));
        }
        
        if (request.getTelefones() != null && !request.getTelefones().isEmpty()) {
            cliente.setTelefones(converterTelefones(request, cliente));
        }
        
        if (request.getEmails() != null && !request.getEmails().isEmpty()) {
            cliente.setEmails(converterEmails(request, cliente));
        }

        return cliente;
    }

    /**
     * Converte EnderecoRequest para entidade Endereco
     */
    private Endereco converterEndereco(ClienteRequest request, Cliente cliente) {
        Endereco endereco = new Endereco();
        endereco.setCep(enderecoService.removerMascaraCEP(request.getEndereco().getCep()));
        endereco.setLogradouro(request.getEndereco().getLogradouro());
        endereco.setComplemento(request.getEndereco().getComplemento());
        endereco.setBairro(request.getEndereco().getBairro());
        endereco.setCidade(request.getEndereco().getCidade());
        endereco.setUf(request.getEndereco().getUf());
        endereco.setCliente(cliente);
        return endereco;
    }

    /**
     * Converte lista de TelefoneRequest para entidades Telefone
     */
    private List<Telefone> converterTelefones(ClienteRequest request, Cliente cliente) {
        return request.getTelefones().stream()
            .map(t -> {
                Telefone telefone = new Telefone();
                telefone.setNumero(telefoneService.removerMascaraTelefone(t.getNumero()));
                telefone.setTipo(t.getTipo());
                telefone.setPrincipal(t.getPrincipal() != null ? t.getPrincipal() : false);
                telefone.setCliente(cliente);
                return telefone;
            })
            .collect(Collectors.toList());
    }

    /**
     * Converte lista de EmailRequest para entidades ClienteEmail
     */
    private List<ClienteEmail> converterEmails(ClienteRequest request, Cliente cliente) {
        return request.getEmails().stream()
            .map(e -> {
                ClienteEmail email = new ClienteEmail();
                email.setEnderecoEmail(e.getEnderecoEmail());
                email.setPrincipal(e.getPrincipal() != null ? e.getPrincipal() : false);
                email.setCliente(cliente);
                return email;
            })
            .collect(Collectors.toList());
    }

    /**
     * Valida CPF e retorna formatado com máscara
     * @param cpf CPF com ou sem máscara
     * @param clienteId ID do cliente (null se for criação)
     * @return CPF formatado com máscara
     */
    private String validarEProcessarCpf(String cpf, Long clienteId) {
        // Valida formato e dígitos verificadores
        CpfUtil.validarOuLancarExcecao(cpf);
        
        // Valida unicidade
        String cpfComMascara = CpfUtil.aplicarMascara(cpf);
        validarCpfUnico(cpfComMascara, clienteId);
        
        return cpfComMascara;
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

    public Page<Cliente> listarTodosPaginado(Pageable pageable) {
        log.info("Listando clientes paginado - page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());
        return clienteRepository.findAll(pageable);
    }

    // ==================== ATUALIZAR CLIENTE ====================

    @Transactional
    public Cliente atualizarCliente(Long id, Cliente clienteAtualizado) {
        log.info("Atualizando cliente ID: {}", id);

        Cliente clienteExistente = buscarPorId(id);

        // Valida e processa CPF se houver alteração
        String novoCpfMascarado = CpfUtil.aplicarMascara(clienteAtualizado.getCpf());
        if (!clienteExistente.getCpf().equals(novoCpfMascarado)) {
            validarEProcessarCpf(clienteAtualizado.getCpf(), id);
        }

        // Atualiza dados básicos
        clienteExistente.setNome(clienteAtualizado.getNome());
        clienteExistente.setCpf(novoCpfMascarado);

        Cliente clienteSalvo = clienteRepository.save(clienteExistente);
        log.info("Cliente atualizado com sucesso. ID: {}", id);

        return clienteSalvo;
    }

    @Transactional
    public Cliente atualizarClienteCompleto(Long id, ClienteRequest request) {
        log.info("Atualizando cliente completo ID: {}", id);

        Cliente clienteExistente = buscarPorId(id);

        // Valida e atualiza CPF se houver alteração
        String novoCpf = validarEProcessarCpf(request.getCpf(), id);
        clienteExistente.setCpf(novoCpf);
        
        // Atualiza nome
        clienteExistente.setNome(request.getNome());

        // Atualiza endereço
        if (request.getEndereco() != null) {
            if (clienteExistente.getEndereco() != null) {
                // Atualiza endereço existente
                Endereco enderecoExistente = clienteExistente.getEndereco();
                enderecoExistente.setCep(enderecoService.removerMascaraCEP(request.getEndereco().getCep()));
                enderecoExistente.setLogradouro(request.getEndereco().getLogradouro());
                enderecoExistente.setComplemento(request.getEndereco().getComplemento());
                enderecoExistente.setBairro(request.getEndereco().getBairro());
                enderecoExistente.setCidade(request.getEndereco().getCidade());
                enderecoExistente.setUf(request.getEndereco().getUf());
            } else {
                // Cria novo endereço
                clienteExistente.setEndereco(converterEndereco(request, clienteExistente));
            }
        }

        // Atualiza telefones - remove os antigos e adiciona os novos
        if (request.getTelefones() != null) {
            clienteExistente.getTelefones().clear();
            if (!request.getTelefones().isEmpty()) {
                clienteExistente.getTelefones().addAll(converterTelefones(request, clienteExistente));
            }
        }

        // Atualiza emails - remove os antigos e adiciona os novos
        if (request.getEmails() != null) {
            clienteExistente.getEmails().clear();
            if (!request.getEmails().isEmpty()) {
                clienteExistente.getEmails().addAll(converterEmails(request, clienteExistente));
            }
        }

        Cliente clienteSalvo = clienteRepository.save(clienteExistente);
        log.info("Cliente completo atualizado com sucesso. ID: {}", id);

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

    /**
     * Valida se CPF já está cadastrado para outro cliente
     */
    private void validarCpfUnico(String cpf, Long clienteId) {
        Optional<Cliente> clienteExistente = clienteRepository.findByCpf(cpf);

        if (clienteExistente.isPresent()) {
            if (clienteId == null || !clienteExistente.get().getId().equals(clienteId)) {
                throw new CpfJaCadastradoException(cpf);
            }
        }
    }

    /**
     * Valida se cliente possui dados mínimos obrigatórios
     * - Pelo menos 1 telefone
     * - Pelo menos 1 email
     * - Define primeiro como principal se nenhum estiver marcado
     */
    private void validarDadosMinimos(Cliente cliente) {
        if (cliente.getTelefones() == null || cliente.getTelefones().isEmpty()) {
            throw new DadosMinimosException("Cliente deve ter pelo menos um telefone");
        }
        if (cliente.getEmails() == null || cliente.getEmails().isEmpty()) {
            throw new DadosMinimosException("Cliente deve ter pelo menos um email");
        }

        // Define primeiro telefone como principal se nenhum estiver marcado
        boolean temTelefonePrincipal = cliente.getTelefones().stream()
                .anyMatch(t -> Boolean.TRUE.equals(t.getPrincipal()));
        if (!temTelefonePrincipal && !cliente.getTelefones().isEmpty()) {
            cliente.getTelefones().get(0).setPrincipal(true);
        }

        // Define primeiro email como principal se nenhum estiver marcado
        boolean temEmailPrincipal = cliente.getEmails().stream()
                .anyMatch(e -> Boolean.TRUE.equals(e.getPrincipal()));
        if (!temEmailPrincipal && !cliente.getEmails().isEmpty()) {
            cliente.getEmails().get(0).setPrincipal(true);
        }
    }

    // ==================== MÉTODOS AUXILIARES (DEPRECATED - usar CpfUtil) ====================

    /**
     * @deprecated Use {@link CpfUtil#removerMascara(String)}
     */
    @Deprecated
    public String removerMascaraCPF(String cpf) {
        return CpfUtil.removerMascara(cpf);
    }

    /**
     * @deprecated Use {@link CpfUtil#aplicarMascara(String)}
     */
    @Deprecated
    public String aplicarMascaraCPF(String cpf) {
        return CpfUtil.aplicarMascara(cpf);
    }

    /**
     * @deprecated Use {@link CpfUtil#validar(String)}
     */
    @Deprecated
    public boolean validarCPF(String cpf) {
        return CpfUtil.validar(cpf);
    }

    public Cliente buscarPorCpf(String cpf) {
        log.info("Buscando cliente por CPF: {}", cpf);
        String cpfComMascara = CpfUtil.aplicarMascara(cpf);
        return clienteRepository.findByCpf(cpfComMascara)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente com CPF " + cpfComMascara + " não encontrado"));
    }
}