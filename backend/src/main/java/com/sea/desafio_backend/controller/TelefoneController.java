package com.sea.desafio_backend.controller;

import com.sea.desafio_backend.dto.request.TelefoneRequest;
import com.sea.desafio_backend.dto.response.ApiResponse;
import com.sea.desafio_backend.model.entity.Cliente;
import com.sea.desafio_backend.model.entity.Telefone;
import com.sea.desafio_backend.service.ClienteService;
import com.sea.desafio_backend.service.TelefoneService;
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
 * - PUT    /api/telefones/{id}                  - Atualizar telefone
 * - DELETE /api/telefones/{id}                  - Deletar telefone
 * - PUT    /api/telefones/{id}/principal        - Marcar como principal
 */
@RestController
@RequestMapping("/api")
@Slf4j
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
     * PUT /api/telefones/{id}
     * Atualiza dados do telefone
     * 
     * @param id ID do telefone
     * @param request Novos dados (numero, tipo, principal)
     * @return 200 OK com telefone atualizado
     */
    @PutMapping("/telefones/{id}")
    public ResponseEntity<Telefone> atualizarTelefone(
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
     * 
     * @param id ID do telefone
     * @return 204 No Content
     */
    @DeleteMapping("/telefones/{id}")
    public ResponseEntity<Void> removerTelefone(@PathVariable Long id) {
        
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
    public ResponseEntity<ApiResponse<Void>> marcarComoPrincipal(@PathVariable Long id) {
        
        log.info("PUT /api/telefones/{}/principal - Marcando como principal", id);
        
        telefoneService.definirComoPrincipal(id);
        
        return ResponseEntity.ok(
                ApiResponse.success("Telefone marcado como principal com sucesso")
        );
    }
}
