package com.sea.desafio_backend.exception;

/**
 * Exceção lançada quando o CPF informado é inválido (formato ou dígitos verificadores)
 * Status HTTP: 400 BAD_REQUEST
 */
public class CpfInvalidoException extends RuntimeException {
    
    public CpfInvalidoException(String mensagem) {
        super(mensagem);
    }
    
    public CpfInvalidoException(String cpf, String motivo) {
        super("CPF inválido (" + cpf + "): " + motivo);
    }
}
