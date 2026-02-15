package com.sea.desafio_backend.dto.request;

import com.sea.desafio_backend.model.enums.TipoTelefone;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * DTO de requisição para criação/atualização de Telefone
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dados para cadastro de telefone do cliente")
public class TelefoneRequest {

    @Schema(
        description = "Número do telefone com DDD",
        example = "11987654321",
        required = true,
        pattern = "^\\(?\\d{2}\\)?\\s?\\d{4,5}-?\\d{4}$"
    )
    @NotBlank(message = "Número do telefone é obrigatório")
    @Pattern(regexp = "^\\(?\\d{2}\\)?\\s?\\d{4,5}-?\\d{4}$", 
             message = "Número de telefone inválido. Use formato: (11) 98765-4321 ou 11987654321")
    private String numero;

    @Schema(
        description = "Tipo do telefone",
        example = "CELULAR",
        required = true,
        allowableValues = {"CELULAR", "FIXO", "COMERCIAL"}
    )
    @NotNull(message = "Tipo do telefone é obrigatório")
    private TipoTelefone tipo;

    @Schema(
        description = "Define se é o telefone principal do cliente",
        example = "true",
        defaultValue = "false"
    )
    private Boolean principal = false;
}
