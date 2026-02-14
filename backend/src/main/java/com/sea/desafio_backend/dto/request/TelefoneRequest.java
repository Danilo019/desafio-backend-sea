package com.sea.desafio_backend.dto.request;

import com.sea.desafio_backend.model.enums.TipoTelefone;
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
public class TelefoneRequest {

    @NotBlank(message = "Número do telefone é obrigatório")
    @Pattern(regexp = "^\\(?\\d{2}\\)?\\s?\\d{4,5}-?\\d{4}$", 
             message = "Número de telefone inválido. Use formato: (11) 98765-4321 ou 11987654321")
    private String numero;

    @NotNull(message = "Tipo do telefone é obrigatório")
    private TipoTelefone tipo;

    private Boolean principal = false;
}
