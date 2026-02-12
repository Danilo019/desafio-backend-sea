package com.sea.desafio_backend.model.enums;

/**
 * Enum para tipos de usuário do sistema
 * - ADMIN: Acesso total ao sistema
 * - PADRAO: Apenas visualização dos dados
 */
public enum TipoUsuario {
    ADMIN("Administrador"),
    PADRAO("Usuário Padrão");

    private final String descricao;

    TipoUsuario(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
