package com.sea.desafio_backend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

/**
 * DTO de requisição para criação/atuawlização de Email
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailRequest {

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    private String enderecoEmail;

    private Boolean principal = false;
}
