package com.sea.desafio_backend.dto.response;

import com.sea.desafio_backend.model.enums.TipoUsuario;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para resposta de login bem-sucedido
 * Contém informações do usuário autenticado (SEM A SENHA!)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    
    private Long id;
    private String username;
    private TipoUsuario tipo;
    private Boolean ativo;
    private String mensagem;
    
    /**
     * Factory method para criar resposta de sucesso
     */
    public static LoginResponse sucesso(Long id, String username, TipoUsuario tipo, Boolean ativo) {
        return new LoginResponse(
            id, 
            username, 
            tipo, 
            ativo, 
            "Login realizado com sucesso!"
        );
    }
    
    /**
     * Factory method para criar resposta de erro
     */
    public static LoginResponse erro(String mensagem) {
        return new LoginResponse(null, null, null, null, mensagem);
    }
}
