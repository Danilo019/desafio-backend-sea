package com.sea.desafio_backend.controller;

import com.sea.desafio_backend.dto.response.ViaCepResponse;
import com.sea.desafio_backend.service.ViaCepService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller temporário para testar integração ViaCEP
 * ⚠️ PODE SER DELETADO APÓS TESTES ⚠️
 */
@RestController
@RequestMapping("/api/test")
public class TestViaCepController {

    private final ViaCepService viaCepService;

    public TestViaCepController(ViaCepService viaCepService) {
        this.viaCepService = viaCepService;
    }

    /**
     * Endpoint para buscar CEP
     * GET http://localhost:8080/api/test/cep/{cep}
     */
    @GetMapping("/cep/{cep}")
    public ResponseEntity<?> buscarCep(@PathVariable String cep) {
        try {
            ViaCepResponse response = viaCepService.buscarEnderecoPorCep(cep);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Erro de validação: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao buscar CEP: " + e.getMessage());
        }
    }

    /**
     * Endpoint para status da aplicação
     * GET http://localhost:8080/api/test/health
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("ViaCEP Service está funcionando! ✅");
    }
}
