package com.sea.desafio_backend.controller;

import com.sea.desafio_backend.dto.request.LoginRequest;
import com.sea.desafio_backend.dto.response.TokenResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Base64;

/**
 * Controller para autenticação de usuários
 * 
 * NOTA: Esta é uma implementação simplificada para demonstração.
 * Em produção, use Spring Security com JWT real.
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticação", description = "Endpoints para autenticação de usuários")
public class AuthController {

    /**
     * Endpoint de login
     * 
     * Credenciais válidas:
     * - Admin: admin@sea.com / 123qwe!@#
     * - User: user@sea.com / 123qwe123
     */
    @PostMapping("/login")
    @Operation(summary = "Login de usuário", description = "Autentica um usuário e retorna um token JWT")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        
        // Validar credenciais (mock)
        if (!isValidCredentials(request.getEmail(), request.getSenha())) {
            return ResponseEntity.status(401).body("Credenciais inválidas");
        }

        // Determinar role baseado no email
        String role = request.getEmail().equals("admin@sea.com") ? "ROLE_ADMIN" : "ROLE_USER";

        // Gerar token mock (em produção, use biblioteca JWT real)
        String payload = request.getEmail() + ":" + role + ":" + System.currentTimeMillis();
        String token = Base64.getEncoder().encodeToString(payload.getBytes());

        TokenResponse response = new TokenResponse(token);
        return ResponseEntity.ok(response);
    }

    /**
     * Valida credenciais (implementação mock)
     */
    private boolean isValidCredentials(String email, String senha) {
        // Admin
        if ("admin@sea.com".equals(email) && "123qwe!@#".equals(senha)) {
            return true;
        }
        // User
        if ("user@sea.com".equals(email) && "123qwe123".equals(senha)) {
            return true;
        }
        return false;
    }

    /**
     * Endpoint de teste para verificar autenticação
     */
    @GetMapping("/me")
    @Operation(summary = "Usuário atual", description = "Retorna informações do usuário autenticado")
    public ResponseEntity<?> getCurrentUser(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("Token não fornecido");
        }

        try {
            String token = authHeader.substring(7);
            String decoded = new String(Base64.getDecoder().decode(token));
            String[] parts = decoded.split(":");
            
            return ResponseEntity.ok(new Object() {
                public String email = parts[0];
                public String role = parts[1];
            });
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Token inválido");
        }
    }
}
