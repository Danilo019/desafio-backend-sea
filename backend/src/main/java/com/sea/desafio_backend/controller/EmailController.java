package com.sea.desafio_backend.controller;

import com.sea.desafio_backend.dto.request.EmailRequest;
import com.sea.desafio_backend.dto.response.ApiResponse;
import com.sea.desafio_backend.model.entity.Cliente;
import com.sea.desafio_backend.model.entity.ClienteEmail;
import com.sea.desafio_backend.service.ClienteService;
import com.sea.desafio_backend.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;

/**
 * Controller REST para gerenciamento de Emails dos Clientes
 */
@RestController
@RequestMapping("/api")
@Slf4j
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
    @PostMapping("/clientes/{clienteId}/emails")
    public ResponseEntity<ClienteEmail> adicionarEmail(
            @PathVariable Long clienteId,
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
     * PUT /api/emails/{id}
     * Atualiza dados do email
     * 
     * @param id ID do email
     * @param request Novos dados (enderecoEmail, principal)
     * @return 200 OK com email atualizado
     */
    @PutMapping("/emails/{id}")
    public ResponseEntity<ClienteEmail> atualizarEmail(
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
     * 
     * @param id ID do email
     * @return 204 No Content
     */
    @DeleteMapping("/emails/{id}")
    public ResponseEntity<Void> removerEmail(@PathVariable Long id) {
        
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
    public ResponseEntity<ApiResponse<Void>> marcarComoPrincipal(@PathVariable Long id) {
        
        log.info("PUT /api/emails/{}/principal - Marcando como principal", id);
        
        emailService.definirComoPrincipal(id);
        
        return ResponseEntity.ok(
                ApiResponse.success("Email marcado como principal com sucesso")
        );
    }
}
