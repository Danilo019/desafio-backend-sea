package com.sea.desafio_backend.exception;

/**
 * Exceção lançada quando se tenta cadastrar um email que já existe no sistema
 * Status HTTP: 409 CONFLICT
 */
public class EmailJaCadastradoException extends RuntimeException {
    
    public EmailJaCadastradoException(String email) {
        super("Email já cadastrado: " + email);
    }
    
    public EmailJaCadastradoException(String email, Throwable cause) {
        super("Email já cadastrado: " + email, cause);
    }
}
