package com.sea.desafio_backend.controller;

import com.sea.desafio_backend.dto.request.EnderecoRequest;
import com.sea.desafio_backend.dto.response.ErrorResponse;
import com.sea.desafio_backend.dto.response.ViaCepResponse;
import com.sea.desafio_backend.model.entity.Endereco;
import com.sea.desafio_backend.service.EnderecoService;
import com.sea.desafio_backend.service.ViaCepService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * Controller REST para gerenciamento de Endereços
 * 
 * Endpoints:
 * - GET    /api/cep/{cep}         - Consultar CEP (ViaCEP)
 * - GET    /api/enderecos/{id}    - Buscar endereço por ID
 * - PUT    /api/enderecos/{id}    - Atualizar endereço
 */
@RestController
@RequestMapping("/api")
@Slf4j
@Tag(name = "Endereços", description = "API para consulta de CEP via ViaCEP e gerenciamento de endereços")
public class EnderecoController {

    private final EnderecoService enderecoService;
    private final ViaCepService viaCepService;

    public EnderecoController(EnderecoService enderecoService, ViaCepService viaCepService) {
        this.enderecoService = enderecoService;
        this.viaCepService = viaCepService;
    }

    /**
     * GET /api/cep/{cep}
     * Consulta CEP na API ViaCEP
     * 
     * @param cep CEP a ser consultado (com ou sem máscara)
     * @return 200 OK com dados do endereço
     * 
     * Exemplo: GET /api/cep/01001000 ou GET /api/cep/01001-000
     */
    @GetMapping("/cep/{cep}")
    public ResponseEntity<ViaCepResponse> consultarCep(@PathVariable String cep) {
        
        log.info("GET /api/cep/{} - Consultando CEP no ViaCEP", cep);
        
        ViaCepResponse endereco = viaCepService.buscarEnderecoPorCep(cep);
        
        return ResponseEntity.ok(endereco);
    }

    /**
     * GET /api/enderecos/{id}
     * Busca endereço por ID
     * 
     * @param id ID do endereço
     * @return 200 OK com dados do endereço
     */
    @GetMapping("/enderecos/{id}")
    public ResponseEntity<Endereco> buscarPorId(
            @Parameter(description = "ID do endereço", required = true, example = "1")
            @PathVariable Long id) {
        
        log.info("GET /api/enderecos/{} - Buscando endereço", id);
        
        Endereco endereco = enderecoService.buscarPorId(id);
        
        return ResponseEntity.ok(endereco);
    }

    /**
     * PUT /api/enderecos/{id}
     * Atualiza dados do endereço
     * 
     * @param id ID do endereço
     * @param request Novos dados do endereço
     * @return 200 OK com endereço atualizado
     */
    @PutMapping("/enderecos/{id}")
    public ResponseEntity<Endereco> atualizarEndereco(
            @Parameter(description = "ID do endereço", required = true, example = "1")
            @PathVariable Long id,
            @Valid @RequestBody EnderecoRequest request) {
        
        log.info("PUT /api/enderecos/{} - Atualizando endereço", id);
        
        // Converte DTO → Entity
        Endereco enderecoAtualizado = new Endereco();
        enderecoAtualizado.setCep(request.getCep());
        enderecoAtualizado.setLogradouro(request.getLogradouro());
        enderecoAtualizado.setComplemento(request.getComplemento());
        enderecoAtualizado.setBairro(request.getBairro());
        enderecoAtualizado.setCidade(request.getCidade());
        enderecoAtualizado.setUf(request.getUf());
        
        Endereco enderecoSalvo = enderecoService.atualizarEndereco(id, enderecoAtualizado);
        
        return ResponseEntity.ok(enderecoSalvo);
    }
}
