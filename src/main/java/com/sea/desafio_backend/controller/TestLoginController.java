package com.sea.desafio_backend.controller;

import com.sea.desafio_backend.dto.request.LoginRequest;
import com.sea.desafio_backend.dto.response.LoginResponse;
import com.sea.desafio_backend.model.entity.Usuario;
import com.sea.desafio_backend.service.UsuarioService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller de TESTE para Login e Usuários
 * ⚠️ TEMPORÁRIO - Apenas para testes de desenvolvimento
 * 
 * Endpoints disponíveis:
 * - POST /api/test/login - Testar login
 * - GET  /api/test/usuarios - Listar todos usuários
 * - GET  /api/test/usuarios/{username} - Buscar por username
 */
@RestController
@RequestMapping("/api/test")
@Slf4j
public class TestLoginController {

    private final UsuarioService usuarioService;

    public TestLoginController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    /**
     * Endpoint para testar login
     * POST http://localhost:8080/api/test/login
     * 
     * Body JSON:
     * {
     *   "username": "admin",
     *   "password": "123qwe!@#"
     * }
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        log.info("Tentativa de login para username: {}", request.getUsername());
        
        try {
            // Valida credenciais
            boolean credenciaisValidas = usuarioService.validarCredenciais(
                request.getUsername(), 
                request.getPassword()
            );
            
            if (credenciaisValidas) {
                // Busca usuário completo
                Usuario usuario = usuarioService.buscarPorUsername(request.getUsername());
                
                // Retorna resposta de sucesso (SEM A SENHA!)
                LoginResponse response = LoginResponse.sucesso(
                    usuario.getId(),
                    usuario.getUsername(),
                    usuario.getTipo(),
                    usuario.getAtivo()
                );
                
                log.info("✅ Login bem-sucedido para: {}", request.getUsername());
                return ResponseEntity.ok(response);
                
            } else {
                log.warn("❌ Credenciais inválidas para: {}", request.getUsername());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(LoginResponse.erro("Usuário ou senha inválidos"));
            }
            
        } catch (Exception e) {
            log.error("Erro ao processar login", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(LoginResponse.erro("Erro ao processar login: " + e.getMessage()));
        }
    }

    /**
     * Lista todos os usuários (para debug)
     * GET http://localhost:8080/api/test/usuarios
     */
    @GetMapping("/usuarios")
    public ResponseEntity<List<Usuario>> listarUsuarios() {
        log.info("Listando todos os usuários");
        List<Usuario> usuarios = usuarioService.listarTodos();
        
        // Remove as senhas antes de retornar (segurança)
        usuarios.forEach(u -> u.setPassword("***OCULTO***"));
        
        return ResponseEntity.ok(usuarios);
    }

    /**
     * Busca usuário por username (para debug)
     * GET http://localhost:8080/api/test/usuarios/{username}
     */
    @GetMapping("/usuarios/{username}")
    public ResponseEntity<?> buscarPorUsername(@PathVariable String username) {
        log.info("Buscando usuário: {}", username);
        
        try {
            Usuario usuario = usuarioService.buscarPorUsername(username);
            usuario.setPassword("***OCULTO***"); // Remove senha
            return ResponseEntity.ok(usuario);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Usuário não encontrado: " + username);
        }
    }

    /**
     * Endpoint de health check
     * GET http://localhost:8080/api/test/auth-health
     */
    @GetMapping("/auth-health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Sistema de Autenticação funcionando! ✅");
    }
}
