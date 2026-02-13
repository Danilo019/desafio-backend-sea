package com.sea.desafio_backend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para requisição de login
 * Contém credenciais do usuário para autenticação
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    
    private String username;
    private String password;
}
