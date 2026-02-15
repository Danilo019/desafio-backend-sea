package com.sea.desafio_backend.controller;

import com.sea.desafio_backend.dto.request.TelefoneRequest;
import com.sea.desafio_backend.dto.response.ApiResponse;
import com.sea.desafio_backend.dto.response.ErrorResponse;
import com.sea.desafio_backend.model.entity.Cliente;
import com.sea.desafio_backend.model.entity.Telefone;
import com.sea.desafio_backend.service.ClienteService;
import com.sea.desafio_backend.service.TelefoneService;
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
import java.net.URI;

/**
 * Controller REST para gerenciamento de Telefones dos Clientes
 * 
 * Endpoints:
 * - POST   /api/clientes/{clienteId}/telefones  - Adicionar telefone
 * - GET    /api/telefones/{id}                  - Buscar telefone por ID
 * - PUT    /api/telefones/{id}                  - Atualizar telefone
 * - DELETE /api/telefones/{id}                  - Deletar telefone (mínimo 1 por cliente)
 * - PUT    /api/telefones/{id}/principal        - Marcar como principal
 */
@RestController
@RequestMapping("/api")
@Slf4j
@Tag(name = "Telefones", description = "API para gerenciamento de telefones dos clientes")
public class TelefoneController {

    private final TelefoneService telefoneService;
    private final ClienteService clienteService;

    public TelefoneController(TelefoneService telefoneService, ClienteService clienteService) {
        this.telefoneService = telefoneService;
        this.clienteService = clienteService;
    }

    /**
     * POST /api/clientes/{clienteId}/telefones
     * Adiciona novo telefone ao cliente
     * 
     * @param clienteId ID do cliente
     * @param request Dados do telefone (numero, tipo, principal)
     * @return 201 Created com Location header
     */
    @PostMapping("/clientes/{clienteId}/telefones")
    public ResponseEntity<Telefone> adicionarTelefone(
            @Parameter(description = "ID do cliente", required = true, example = "1")
            @PathVariable Long clienteId,
            @Valid @RequestBody TelefoneRequest request) {
        
        log.info("POST /api/clientes/{}/telefones - Adicionando telefone: {}", 
                clienteId, request.getNumero());
        
        // Busca o cliente
        Cliente cliente = clienteService.buscarPorId(clienteId);
        
        // Converte DTO → Entity
        Telefone telefone = new Telefone();
        telefone.setNumero(request.getNumero());
        telefone.setTipo(request.getTipo());
        telefone.setPrincipal(request.getPrincipal() != null ? request.getPrincipal() : false);
        telefone.setCliente(cliente);
        
        // Salva via Service
        Telefone telefoneSalvo = telefoneService.criarTelefone(telefone);
        
        return ResponseEntity
                .created(URI.create("/api/telefones/" + telefoneSalvo.getId()))
                .body(telefoneSalvo);
    }

    /**
     * GET /api/telefones/{id}
     * Busca telefone por ID
     * 
     * @param id ID do telefone
     * @return 200 OK com dados do telefone
     */
    @Operation(summary = "Buscar telefone por ID", description = "Retorna os dados completos de um telefone")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Telefone encontrado com sucesso",
            content = @Content(schema = @Schema(implementation = Telefone.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Telefone não encontrado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @GetMapping("/telefones/{id}")
    public ResponseEntity<Telefone> buscarPorId(
            @Parameter(description = "ID do telefone", required = true, example = "1")
            @PathVariable Long id) {
        
        log.info("GET /api/telefones/{} - Buscando telefone", id);
        
        Telefone telefone = telefoneService.buscarPorId(id);
        
        return ResponseEntity.ok(telefone);
    }

    /**
     * PUT /api/telefones/{id}
     * Atualiza dados do telefone
     * 
     * @param id ID do telefone
     * @param request Novos dados (numero, tipo, principal)
     * @return 200 OK com telefone atualizado
     */
    @PutMapping("/telefones/{id}")
    public ResponseEntity<Telefone> atualizarTelefone(
            @Parameter(description = "ID do telefone", required = true, example = "1")
            @PathVariable Long id,
            @Valid @RequestBody TelefoneRequest request) {
        
        log.info("PUT /api/telefones/{} - Atualizando para: {}", 
                id, request.getNumero());
        
        // Converte DTO → Entity
        Telefone telefoneAtualizado = new Telefone();
        telefoneAtualizado.setNumero(request.getNumero());
        telefoneAtualizado.setTipo(request.getTipo());
        telefoneAtualizado.setPrincipal(request.getPrincipal() != null ? request.getPrincipal() : false);
        
        Telefone telefoneSalvo = telefoneService.atualizarTelefone(id, telefoneAtualizado);
        
        return ResponseEntity.ok(telefoneSalvo);
    }

    /**
     * DELETE /api/telefones/{id}
     * Remove telefone do cliente
     * REGRA: Cliente deve ter pelo menos 1 telefone cadastrado
     * 
     * @param id ID do telefone
     * @return 204 No Content
     */
    @Operation(
        summary = "Remover telefone",
        description = "Remove um telefone do cliente. **ATENÇÃO**: Cliente deve ter pelo menos 1 telefone, não é possível remover o último."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "204",
            description = "Telefone removido com sucesso"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Erro: tentativa de remover o último telefone do cliente",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Telefone não encontrado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @DeleteMapping("/telefones/{id}")
    public ResponseEntity<Void> removerTelefone(
            @Parameter(description = "ID do telefone", required = true, example = "1")
            @PathVariable Long id) {
        
        log.info("DELETE /api/telefones/{} - Removendo telefone", id);
        
        telefoneService.deletarTelefone(id);
        
        return ResponseEntity.noContent().build();
    }

    /**
     * PUT /api/telefones/{id}/principal
     * Marca telefone como principal (desmarca outros)
     *
     * @param id ID do telefone
     * @return 200 OK com mensagem de sucesso
     */
    @PutMapping("/telefones/{id}/principal")
    public ResponseEntity<ApiResponse<Void>> marcarComoPrincipal(
            @Parameter(description = "ID do telefone", required = true, example = "1")
            @PathVariable Long id) {
        
        log.info("PUT /api/telefones/{}/principal - Marcando como principal", id);
        
        telefoneService.definirComoPrincipal(id);
        
        return ResponseEntity.ok(
                ApiResponse.success("Telefone marcado como principal com sucesso")
        );
    }
}
