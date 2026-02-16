package com.sea.desafio_backend.controller;

import com.sea.desafio_backend.dto.request.ClienteRequest;
import com.sea.desafio_backend.dto.response.ApiResponse;
import com.sea.desafio_backend.dto.response.ClienteResponse;
import com.sea.desafio_backend.dto.response.ErrorResponse;
import com.sea.desafio_backend.model.entity.Cliente;
import com.sea.desafio_backend.service.ClienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller REST para gerenciamento de Clientes
 * 
 * Endpoints:
 * - POST   /api/clientes              - Criar cliente completo
 * - GET    /api/clientes              - Listar todos
 * - GET    /api/clientes/{id}         - Buscar por ID
 * - GET    /api/clientes/cpf/{cpf}    - Buscar por CPF
 * - PUT    /api/clientes/{id}         - Atualizar dados básicos
 * - DELETE /api/clientes/{id}         - Deletar cliente
 */
@RestController
@RequestMapping("/api/clientes")
@Slf4j
@Tag(name = "Clientes", description = "API para gestão completa de clientes, incluindo cadastro, consulta, atualização e remoção")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    /**
     * POST /api/clientes
     * Cria um novo cliente completo (com endereço, telefones e emails)
     */
    @Operation(
        summary = "Criar novo cliente",
        description = "Cria um cliente completo com endereço, telefones e emails associados. " +
                      "Valida CPF, endereço via ViaCEP e dados obrigatórios."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201",
            description = "Cliente criado com sucesso",
            content = @Content(schema = @Schema(implementation = ClienteResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Dados inválidos (CPF duplicado, validações)",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @PostMapping
    public ResponseEntity<ClienteResponse> criarCliente(
            @Parameter(description = "Dados do cliente a ser criado", required = true)
            @Valid @RequestBody ClienteRequest request) {
        log.info("POST /api/clientes - Criando cliente: {}", request.getNome());
        
        Cliente cliente = clienteService.criarCliente(request);
        ClienteResponse response = ClienteResponse.fromEntity(cliente);
        
        return ResponseEntity
                .created(URI.create("/api/clientes/" + cliente.getId()))
                .body(response);
    }

    /**
     * GET /api/clientes
     * Lista todos os clientes com paginação
     */
    @Operation(
        summary = "Listar todos os clientes",
        description = "Retorna lista paginada de clientes cadastrados com seus endereços, telefones e emails"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Lista de clientes retornada com sucesso",
            content = @Content(schema = @Schema(implementation = ClienteResponse.class))
        )
    })
    @GetMapping
    public ResponseEntity<Page<ClienteResponse>> listarTodos(
            @Parameter(description = "Número da página (iniciando em 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamanho da página", example = "10")
            @RequestParam(defaultValue = "10") int size) {
        log.info("GET /api/clientes?page={}&size={} - Listando clientes", page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Cliente> clientesPage = clienteService.listarTodosPaginado(pageable);
        Page<ClienteResponse> responses = clientesPage.map(ClienteResponse::fromEntity);
        
        return ResponseEntity.ok(responses);
    }

    /**
     * GET /api/clientes/{id}
     * Busca cliente por ID
     */
    @Operation(
        summary = "Buscar cliente por ID",
        description = "Retorna os dados completos de um cliente específico pelo seu identificador"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Cliente encontrado",
            content = @Content(schema = @Schema(implementation = ClienteResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Cliente não encontrado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponse> buscarPorId(
            @Parameter(description = "ID do cliente", required = true, example = "1")
            @PathVariable Long id) {
        log.info("GET /api/clientes/{} - Buscando cliente", id);
        
        Cliente cliente = clienteService.buscarPorId(id);
        ClienteResponse response = ClienteResponse.fromEntity(cliente);
        
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/clientes/cpf/{cpf}
     * Busca cliente por CPF
     */
    @Operation(
        summary = "Buscar cliente por CPF",
        description = "Retorna os dados de um cliente através do seu CPF (com ou sem máscara)"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Cliente encontrado",
            content = @Content(schema = @Schema(implementation = ClienteResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Cliente não encontrado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @GetMapping("/cpf/{cpf}")
    public ResponseEntity<ClienteResponse> buscarPorCpf(
            @Parameter(description = "CPF do cliente (com ou sem máscara)", required = true, example = "12345678901")
            @PathVariable String cpf) {
        log.info("GET /api/clientes/cpf/{} - Buscando cliente", cpf);
        
        Cliente cliente = clienteService.buscarPorCpf(cpf);
        ClienteResponse response = ClienteResponse.fromEntity(cliente);
        
        return ResponseEntity.ok(response);
    }

    /**
     * PUT /api/clientes/{id}
     * Atualiza um cliente completo
     */
    @Operation(
        summary = "Atualizar cliente",
        description = "Atualiza todos os dados de um cliente incluindo endereço, telefones e emails"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Cliente atualizado com sucesso",
            content = @Content(schema = @Schema(implementation = ClienteResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Dados inválidos",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Cliente não encontrado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @PutMapping("/{id}")
    public ResponseEntity<ClienteResponse> atualizarCliente(
            @Parameter(description = "ID do cliente a ser atualizado", required = true, example = "1")
            @PathVariable Long id,
            @Parameter(description = "Novos dados do cliente", required = true)
            @Valid @RequestBody ClienteRequest request) {
        log.info("PUT /api/clientes/{} - Atualizando cliente: {}", id, request.getNome());
        
        Cliente cliente = clienteService.atualizarClienteCompleto(id, request);
        ClienteResponse response = ClienteResponse.fromEntity(cliente);
        
        return ResponseEntity.ok(response);
    }

    /**
     * DELETE /api/clientes/{id}
     * Deleta um cliente
     */
    @Operation(
        summary = "Deletar cliente",
        description = "Remove um cliente e todos os seus dados associados (endereço, telefones, emails)"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "204",
            description = "Cliente deletado com sucesso"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Cliente não encontrado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(
            @Parameter(description = "ID do cliente a ser deletado", required = true, example = "1")
            @PathVariable Long id) {
        log.info("DELETE /api/clientes/{} - Deletando cliente", id);
        
        clienteService.deletarCliente(id);
        
        return ResponseEntity.noContent().build();
    }
}
