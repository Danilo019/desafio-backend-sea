package com.sea.desafio_backend.exception;

/**
 * Exceção lançada quando os dados mínimos obrigatórios não são fornecidos
 * Exemplo: Cliente sem telefone ou sem email
 * Status HTTP: 400 BAD_REQUEST
 */
public class DadosMinimosException extends RuntimeException {
    
    public DadosMinimosException(String mensagem) {
        super(mensagem);
    }
}
