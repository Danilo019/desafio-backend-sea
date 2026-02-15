package com.sea.desafio_backend.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * DTO de requisição para criação/atualização de Cliente
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dados para criação de um novo cliente completo")
public class ClienteRequest {

    @Schema(
        description = "Nome completo do cliente",
        example = "João da Silva",
        required = true,
        minLength = 3,
        maxLength = 100
    )
    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 3, max = 100, message = "Nome deve ter entre 3 e 100 caracteres")
    @Pattern(regexp = "^[\\p{L}\\p{N}\\s]+$", 
             message = "Nome deve conter apenas letras, números e espaços")
    private String nome;

    @Schema(
        description = "CPF do cliente no formato XXX.XXX.XXX-XX",
        example = "123.456.789-00",
        required = true,
        pattern = "\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}"
    )
    @NotBlank(message = "CPF é obrigatório")
    @Pattern(regexp = "^\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}$", 
             message = "CPF inválido. Use formato: 123.456.789-00")
    private String cpf;

    @Schema(description = "Endereço do cliente", required = true)
    @Valid
    private EnderecoRequest endereco;

    @Schema(description = "Lista de telefones do cliente (mínimo 1)", required = true)
    @NotEmpty(message = "Cliente deve ter pelo menos um telefone")
    @Valid
    private List<TelefoneRequest> telefones;

    @Schema(description = "Lista de emails do cliente (mínimo 1)", required = true)
    @NotEmpty(message = "Cliente deve ter pelo menos um email")
    @Valid
    private List<EmailRequest> emails;
}
