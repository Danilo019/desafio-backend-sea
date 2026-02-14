package com.sea.desafio_backend.dto.request;

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
public class ClienteRequest {

    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 3, max = 100, message = "Nome deve ter entre 3 e 100 caracteres")
    @Pattern(regexp = "^[\\p{L}\\p{N}\\s]+$", 
             message = "Nome deve conter apenas letras, números e espaços")
    private String nome;

    @NotBlank(message = "CPF é obrigatório")
    @Pattern(regexp = "^\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}$", 
             message = "CPF inválido. Use formato: 123.456.789-00")
    private String cpf;

    @Valid
    private EnderecoRequest endereco;

    @NotEmpty(message = "Cliente deve ter pelo menos um telefone")
    @Valid
    private List<TelefoneRequest> telefones;

    @NotEmpty(message = "Cliente deve ter pelo menos um email")
    @Valid
    private List<EmailRequest> emails;
}
