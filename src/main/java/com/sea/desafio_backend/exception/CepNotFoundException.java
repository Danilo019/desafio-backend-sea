package com.sea.desafio_backend.exception;

/**
 * Exception lançada quando CEP não é encontrado na API ViaCEP
 * ou quando ocorre erro na comunicação com a API
 */
public class CepNotFoundException extends RuntimeException {
    
    /**
     * Construtor com mensagem padrão
     * @param cep CEP que não foi encontrado
     */
    public CepNotFoundException(String cep) {
        super("CEP não encontrado: " + cep);
    }
    
    /**
     * Construtor com causa raiz (para erros de rede)
     * @param cep CEP que causou o erro
     * @param cause Causa do erro (timeout, conexão, etc)
     */
    public CepNotFoundException(String cep, Throwable cause) {
        super("Erro ao buscar CEP: " + cep, cause);
    }
}
