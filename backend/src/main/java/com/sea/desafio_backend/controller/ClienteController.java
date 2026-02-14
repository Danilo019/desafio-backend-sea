package com.sea.desafio_backend.controller;

import com.sea.desafio_backend.dto.request.ClienteRequest;
import com.sea.desafio_backend.dto.response.ClienteResponse;
import com.sea.desafio_backend.model.entity.Cliente;
import com.sea.desafio_backend.service.ClienteService;
import lombok.extern.slf4j.Slf4j;
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
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    /**
     * POST /api/clientes
     * Cria um novo cliente completo (com endereço, telefones e emails)
     */
    @PostMapping
    public ResponseEntity<ClienteResponse> criarCliente(@Valid @RequestBody ClienteRequest request) {
        log.info("POST /api/clientes - Criando cliente: {}", request.getNome());
        
        Cliente cliente = clienteService.criarCliente(request);
        ClienteResponse response = ClienteResponse.fromEntity(cliente);
        
        return ResponseEntity
                .created(URI.create("/api/clientes/" + cliente.getId()))
                .body(response);
    }

    /**
     * GET /api/clientes
     * Lista todos os clientes
     */
    @GetMapping
    public ResponseEntity<List<ClienteResponse>> listarTodos() {
        log.info("GET /api/clientes - Listando todos os clientes");
        
        List<Cliente> clientes = clienteService.listarTodos();
        List<ClienteResponse> responses = clientes.stream()
                .map(ClienteResponse::fromEntity)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(responses);
    }

    /**
     * GET /api/clientes/{id}
     * Busca cliente por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponse> buscarPorId(@PathVariable Long id) {
        log.info("GET /api/clientes/{} - Buscando cliente", id);
        
        Cliente cliente = clienteService.buscarPorId(id);
        ClienteResponse response = ClienteResponse.fromEntity(cliente);
        
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/clientes/cpf/{cpf}
     * Busca cliente por CPF
     */
    @GetMapping("/cpf/{cpf}")
    public ResponseEntity<ClienteResponse> buscarPorCpf(@PathVariable String cpf) {
        log.info("GET /api/clientes/cpf/{} - Buscando cliente", cpf);
        
        Cliente cliente = clienteService.buscarPorCpf(cpf);
        ClienteResponse response = ClienteResponse.fromEntity(cliente);
        
        return ResponseEntity.ok(response);
    }

    /**
     * DELETE /api/clientes/{id}
     * Deleta um cliente
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        log.info("DELETE /api/clientes/{} - Deletando cliente", id);
        
        clienteService.deletarCliente(id);
        
        return ResponseEntity.noContent().build();
    }
}
