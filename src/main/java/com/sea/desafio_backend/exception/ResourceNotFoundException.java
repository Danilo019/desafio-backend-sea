package com.sea.desafio_backend.exception;

/**
 * Exception lançada quando um recurso não é encontrado no banco de dados
 * Genérica para uso em qualquer entidade (Usuario, Cliente, Endereco, etc)
 */
public class ResourceNotFoundException extends RuntimeException {
    
    /**
     * Construtor com mensagem customizada
     * @param mensagem Mensagem de erro
     */
    public ResourceNotFoundException(String mensagem) {
        super(mensagem);
    }
    
    /**
     * Construtor padrão para entidade não encontrada por ID
     * @param entidade Nome da entidade (ex: "Usuario", "Cliente")
     * @param id ID que não foi encontrado
     */
    public ResourceNotFoundException(String entidade, Long id) {
        super(String.format("%s não encontrado(a) com ID: %d", entidade, id));
    }
    
    /**
     * Construtor para busca por campo específico
     * @param entidade Nome da entidade
     * @param campo Nome do campo buscado
     * @param valor Valor buscado
     */
    public ResourceNotFoundException(String entidade, String campo, String valor) {
        super(String.format("%s não encontrado(a) com %s: %s", entidade, campo, valor));
    }
}
