package com.sea.desafio_backend.model.enums;

/**
 * Enum para tipos de telefone
 * Define os formatos de máscara para cada tipo
 */
public enum TipoTelefone {
    RESIDENCIAL("Residencial", "(XX) XXXX-XXXX"),
    COMERCIAL("Comercial", "(XX) XXXX-XXXX"),
    CELULAR("Celular", "(XX) XXXXX-XXXX");

    private final String descricao;
    private final String mascara;

    TipoTelefone(String descricao, String mascara) {
        this.descricao = descricao;
        this.mascara = mascara;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getMascara() {
        return mascara;
    }
}
