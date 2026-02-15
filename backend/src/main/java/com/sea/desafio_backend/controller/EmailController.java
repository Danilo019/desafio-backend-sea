package com.sea.desafio_backend.controller;

import com.sea.desafio_backend.dto.request.EmailRequest;
import com.sea.desafio_backend.dto.response.ApiResponse;
import com.sea.desafio_backend.dto.response.ErrorResponse;
import com.sea.desafio_backend.model.entity.Cliente;
import com.sea.desafio_backend.model.entity.ClienteEmail;
import com.sea.desafio_backend.service.ClienteService;
import com.sea.desafio_backend.service.EmailService;
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
 * Controller REST para gerenciamento de Emails dos Clientes
 * 
 * Endpoints:
 * - POST   /api/clientes/{clienteId}/emails  - Adicionar email
 * - GET    /api/emails/{id}                  - Buscar email por ID
 * - PUT    /api/emails/{id}                  - Atualizar email
 * - DELETE /api/emails/{id}                  - Deletar email (mínimo 1 por cliente)
 * - PUT    /api/emails/{id}/principal        - Marcar como principal
 */
@RestController
@RequestMapping("/api")
@Slf4j
@Tag(name = "Emails", description = "API para gerenciamento de emails dos clientes")
public class EmailController {

    private final EmailService emailService;
    private final ClienteService clienteService;

    public EmailController(EmailService emailService, ClienteService clienteService) {
        this.emailService = emailService;
        this.clienteService = clienteService;
    }

    /**
     * POST /api/clientes/{clienteId}/emails
     * Adiciona novo email ao cliente
     * 
     * @param clienteId ID do cliente
     * @param request Dados do email (enderecoEmail, principal)
     * @return 201 Created com Location header
     */
    @Operation(
        summary = "Adicionar email ao cliente",
        description = "Adiciona um novo endereço de email a um cliente existente"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201",
            description = "Email adicionado com sucesso"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Email já cadastrado ou dados inválidos",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Cliente não encontrado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @PostMapping("/clientes/{clienteId}/emails")
    public ResponseEntity<ClienteEmail> adicionarEmail(
            @Parameter(description = "ID do cliente", required = true, example = "1")
            @PathVariable Long clienteId,
            @Parameter(description = "Dados do email", required = true)
            @Valid @RequestBody EmailRequest request) {
        
        log.info("POST /api/clientes/{}/emails - Adicionando email: {}", 
                clienteId, request.getEnderecoEmail());
        
        // Busca o cliente
        Cliente cliente = clienteService.buscarPorId(clienteId);
        
        // Converte DTO → Entity
        ClienteEmail email = new ClienteEmail();
        email.setEnderecoEmail(request.getEnderecoEmail());
        email.setPrincipal(request.getPrincipal() != null ? request.getPrincipal() : false);
        email.setCliente(cliente);
        
        // Salva via Service
        ClienteEmail emailSalvo = emailService.criarEmail(email);
        
        return ResponseEntity
                .created(URI.create("/api/emails/" + emailSalvo.getId()))
                .body(emailSalvo);
    }

    /**
     * GET /api/emails/{id}
     * Busca email por ID
     * 
     * @param id ID do email
     * @return 200 OK com dados do email
     */
    @Operation(summary = "Buscar email por ID", description = "Retorna os dados completos de um email")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Email encontrado com sucesso",
            content = @Content(schema = @Schema(implementation = ClienteEmail.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Email não encontrado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @GetMapping("/emails/{id}")
    public ResponseEntity<ClienteEmail> buscarPorId(
            @Parameter(description = "ID do email", required = true, example = "1")
            @PathVariable Long id) {
        
        log.info("GET /api/emails/{} - Buscando email", id);
        
        ClienteEmail email = emailService.buscarPorId(id);
        
        return ResponseEntity.ok(email);
    }

    /**
     * PUT /api/emails/{id}
     * Atualiza dados do email
     * 
     * @param id ID do email
     * @param request Novos dados (enderecoEmail, principal)
     * @return 200 OK com email atualizado
     */
    @PutMapping("/emails/{id}")
    public ResponseEntity<ClienteEmail> atualizarEmail(
            @Parameter(description = "ID do email", required = true, example = "1")
            @PathVariable Long id,
            @Valid @RequestBody EmailRequest request) {
        
        log.info("PUT /api/emails/{} - Atualizando para: {}", 
                id, request.getEnderecoEmail());
        
        // Converte DTO → Entity
        ClienteEmail emailAtualizado = new ClienteEmail();
        emailAtualizado.setEnderecoEmail(request.getEnderecoEmail());
        emailAtualizado.setPrincipal(request.getPrincipal() != null ? request.getPrincipal() : false);
        
        ClienteEmail emailSalvo = emailService.atualizarEmail(id, emailAtualizado);
        
        return ResponseEntity.ok(emailSalvo);
    }

    /**
     * DELETE /api/emails/{id}
     * Remove email do cliente
     * REGRA: Cliente deve ter pelo menos 1 email cadastrado
     * 
     * @param id ID do email
     * @return 204 No Content
     */
    @Operation(
        summary = "Remover email",
        description = "Remove um email do cliente. **ATENÇÃO**: Cliente deve ter pelo menos 1 email, não é possível remover o último."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "204",
            description = "Email removido com sucesso"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Erro: tentativa de remover o último email do cliente",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Email não encontrado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @DeleteMapping("/emails/{id}")
    public ResponseEntity<Void> removerEmail(
            @Parameter(description = "ID do email", required = true, example = "1")
            @PathVariable Long id) {
        
        log.info("DELETE /api/emails/{} - Removendo email", id);
        
        emailService.deletarEmail(id);
        
        return ResponseEntity.noContent().build();
    }

    /**
     * PUT /api/emails/{id}/principal
     * Marca email como principal (desmarca outros)
     *
     * @param id ID do email
     * @return 200 OK com mensagem de sucesso
     */
    @PutMapping("/emails/{id}/principal")
    public ResponseEntity<ApiResponse<Void>> marcarComoPrincipal(
            @Parameter(description = "ID do email", required = true, example = "1")
            @PathVariable Long id) {
        
        log.info("PUT /api/emails/{}/principal - Marcando como principal", id);
        
        emailService.definirComoPrincipal(id);
        
        return ResponseEntity.ok(
                ApiResponse.success("Email marcado como principal com sucesso")
        );
    }
}
