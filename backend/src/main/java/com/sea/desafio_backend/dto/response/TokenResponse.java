package com.sea.desafio_backend.dto.response;

/**
 * DTO para resposta de login com token JWT
 */
public class TokenResponse {

    private String token;
    private String type = "Bearer";

    // Construtores
    public TokenResponse() {
    }

    public TokenResponse(String token) {
        this.token = token;
    }

    // Getters e Setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
