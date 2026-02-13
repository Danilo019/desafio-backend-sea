package com.sea.desafio_backend.service;

import com.sea.desafio_backend.dto.response.ViaCepResponse;
import com.sea.desafio_backend.exception.ResourceNotFoundException;
import com.sea.desafio_backend.model.entity.Endereco;
import com.sea.desafio_backend.repository.EnderecoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service para gerenciamento de Endereços
 * Responsável por CRUD e integração com ViaCEP
 */
@Service
@Slf4j
public class EnderecoService {

    private final EnderecoRepository enderecoRepository;
    private final ViaCepService viaCepService;

    public EnderecoService(EnderecoRepository enderecoRepository, ViaCepService viaCepService) {
        this.enderecoRepository = enderecoRepository;
        this.viaCepService = viaCepService;
    }

    // ==================== CRIAR ENDEREÇO ====================

    /**
     * Cria um novo endereço
     * Remove máscara do CEP antes de salvar
     */
    @Transactional
    public Endereco criarEndereco(Endereco endereco) {
        log.info("Criando novo endereço para cliente ID: {}", endereco.getCliente().getId());
        
        // Remove máscara do CEP antes de salvar
        String cepSemMascara = removerMascaraCEP(endereco.getCep());
        endereco.setCep(cepSemMascara);
        
        Endereco enderecoSalvo = enderecoRepository.save(endereco);
        log.info("Endereço criado com sucesso. ID: {}", enderecoSalvo.getId());
        
        return enderecoSalvo;
    }

    /**
     * Cria endereço preenchendo automaticamente com dados do ViaCEP
     */
    @Transactional
    public Endereco criarEnderecoComViaCep(String cep, Endereco endereco) {
        log.info("Criando endereço com busca ViaCEP: {}", cep);
        
        // Busca dados do CEP na API ViaCEP
        ViaCepResponse dadosViaCep = viaCepService.buscarEnderecoPorCep(cep);
        
        // Preenche dados do endereço
        String cepSemMascara = removerMascaraCEP(dadosViaCep.getCep());
        endereco.setCep(cepSemMascara);
        endereco.setLogradouro(dadosViaCep.getLogradouro());
        endereco.setBairro(dadosViaCep.getBairro());
        endereco.setCidade(dadosViaCep.getLocalidade());
        endereco.setUf(dadosViaCep.getUf());
        
        // Complemento vem do usuário (não da API)
        // Cliente pode ser setado externamente
        
        Endereco enderecoSalvo = enderecoRepository.save(endereco);
        log.info("Endereço criado com sucesso usando ViaCEP. ID: {}", enderecoSalvo.getId());
        
        return enderecoSalvo;
    }

    // ==================== BUSCAR ENDEREÇOS ====================

    public Endereco buscarPorId(Long id) {
        log.info("Buscando endereço por ID: {}", id);
        return enderecoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Endereco", id));
    }

    public Endereco buscarPorCliente(Long clienteId) {
        log.info("Buscando endereço do cliente ID: {}", clienteId);
        return enderecoRepository.findByClienteId(clienteId)
                .orElseThrow(() -> new ResourceNotFoundException("Endereco", "clienteId", clienteId.toString()));
    }

    public List<Endereco> listarTodos() {
        log.info("Listando todos os endereços");
        return enderecoRepository.findAll();
    }

    // ==================== ATUALIZAR ENDEREÇO ====================

    @Transactional
    public Endereco atualizarEndereco(Long id, Endereco enderecoAtualizado) {
        log.info("Atualizando endereço ID: {}", id);
        
        Endereco enderecoExistente = buscarPorId(id);
        
        // Remove máscara do CEP
        String cepSemMascara = removerMascaraCEP(enderecoAtualizado.getCep());
        
        enderecoExistente.setCep(cepSemMascara);
        enderecoExistente.setLogradouro(enderecoAtualizado.getLogradouro());
        enderecoExistente.setComplemento(enderecoAtualizado.getComplemento());
        enderecoExistente.setBairro(enderecoAtualizado.getBairro());
        enderecoExistente.setCidade(enderecoAtualizado.getCidade());
        enderecoExistente.setUf(enderecoAtualizado.getUf());
        
        Endereco enderecoSalvo = enderecoRepository.save(enderecoExistente);
        log.info("Endereço atualizado com sucesso. ID: {}", id);
        
        return enderecoSalvo;
    }

    /**
     * Atualiza endereço buscando novos dados no ViaCEP
     */
    @Transactional
    public Endereco atualizarComViaCep(Long id, String novoCep) {
        log.info("Atualizando endereço ID: {} com novo CEP: {}", id, novoCep);
        
        Endereco enderecoExistente = buscarPorId(id);
        
        // Busca novos dados no ViaCEP
        ViaCepResponse dadosViaCep = viaCepService.buscarEnderecoPorCep(novoCep);
        
        // Atualiza dados
        String cepSemMascara = removerMascaraCEP(dadosViaCep.getCep());
        enderecoExistente.setCep(cepSemMascara);
        enderecoExistente.setLogradouro(dadosViaCep.getLogradouro());
        enderecoExistente.setBairro(dadosViaCep.getBairro());
        enderecoExistente.setCidade(dadosViaCep.getLocalidade());
        enderecoExistente.setUf(dadosViaCep.getUf());
        // Mantém o complemento existente
        
        Endereco enderecoSalvo = enderecoRepository.save(enderecoExistente);
        log.info("Endereço atualizado com ViaCEP. ID: {}", id);
        
        return enderecoSalvo;
    }

    // ==================== DELETAR ENDEREÇO ====================

    @Transactional
    public void deletarEndereco(Long id) {
        log.info("Deletando endereço ID: {}", id);
        
        buscarPorId(id); // Valida se existe
        
        enderecoRepository.deleteById(id);
        log.info("Endereço deletado com sucesso. ID: {}", id);
    }

    // ==================== MÉTODOS AUXILIARES ====================

    /**
     * Remove máscara do CEP (mantém apenas números)
     * Exemplo: 12345-678 → 12345678
     */
    public String removerMascaraCEP(String cep) {
        if (cep == null) {
            return "";
        }
        return cep.replaceAll("[^0-9]", "");
    }

    /**
     * Aplica máscara no CEP
     * Exemplo: 12345678 → 12345-678
     */
    public String aplicarMascaraCEP(String cep) {
        String cepLimpo = removerMascaraCEP(cep);
        
        if (cepLimpo.length() == 8) {
            return cepLimpo.replaceAll("(\\d{5})(\\d{3})", "$1-$2");
        }
        
        return cep;
    }

    /**
     * Busca dados de um CEP na API ViaCEP
     */
    public ViaCepResponse consultarCep(String cep) {
        log.info("Consultando CEP: {}", cep);
        return viaCepService.buscarEnderecoPorCep(cep);
    }
}
