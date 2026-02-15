package com.sea.desafio_backend.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Dados para cadastro de email do cliente")
public class EmailRequest {

    @Schema(
        description = "Endereço de email válido",
        example = "joao.silva@example.com",
        required = true
    )
    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    private String enderecoEmail;

    @Schema(
        description = "Define se é o email principal do cliente",
        example = "true",
        defaultValue = "false"
    )
    private Boolean principal = false;
}
