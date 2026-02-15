package com.sea.desafio_backend.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * DTO de requisição para criação/atualização de Endereço
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dados do endereço do cliente")
public class EnderecoRequest {

    @Schema(
        description = "CEP do endereço (com ou sem hífen)",
        example = "01310100",
        required = true,
        pattern = "^\\d{5}-?\\d{3}$"
    )
    @NotBlank(message = "CEP é obrigatório")
    @Pattern(regexp = "^\\d{5}-?\\d{3}$", 
             message = "CEP inválido. Use formato: 01001-000 ou 01001000")
    private String cep;

    @Schema(
        description = "Nome da rua/avenida",
        example = "Avenida Paulista",
        required = true,
        maxLength = 200
    )
    @NotBlank(message = "Logradouro é obrigatório")
    @Size(max = 200, message = "Logradouro deve ter no máximo 200 caracteres")
    private String logradouro;

    @Schema(
        description = "Complemento do endereço (apartamento, bloco, etc)",
        example = "Apto 101",
        maxLength = 100
    )
    @Size(max = 100, message = "Complemento deve ter no máximo 100 caracteres")
    private String complemento;

    @Schema(
        description = "Nome do bairro",
        example = "Bela Vista",
        required = true,
        maxLength = 100
    )
    @NotBlank(message = "Bairro é obrigatório")
    @Size(max = 100, message = "Bairro deve ter no máximo 100 caracteres")
    private String bairro;

    @Schema(
        description = "Nome da cidade",
        example = "São Paulo",
        required = true,
        maxLength = 100
    )
    @NotBlank(message = "Cidade é obrigatória")
    @Size(max = 100, message = "Cidade deve ter no máximo 100 caracteres")
    private String cidade;

    @Schema(
        description = "Unidade Federativa (sigla com 2 letras maiúsculas)",
        example = "SP",
        required = true,
        pattern = "^[A-Z]{2}$"
    )
    @NotBlank(message = "UF é obrigatória")
    @Pattern(regexp = "^[A-Z]{2}$", 
             message = "UF inválida. Use 2 letras maiúsculas (ex: SP, RJ)")
    private String uf;
}
