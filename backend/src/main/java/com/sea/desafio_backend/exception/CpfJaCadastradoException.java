package com.sea.desafio_backend.exception;

/**
 * Exceção lançada quando se tenta cadastrar um CPF que já existe no sistema
 * Status HTTP: 409 CONFLICT
 */
public class CpfJaCadastradoException extends RuntimeException {
    
    public CpfJaCadastradoException(String cpf) {
        super("CPF já cadastrado: " + cpf);
    }
    
    public CpfJaCadastradoException(String cpf, Throwable cause) {
        super("CPF já cadastrado: " + cpf, cause);
    }
}
