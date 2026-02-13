package com.sea.desafio_backend.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para resposta da API ViaCEP
 * Mapeia o JSON retornado pela API https://viacep.com.br/
 * 
 * Exemplo de resposta:
 * {
 *   "cep": "01001-000",
 *   "logradouro": "Praça da Sé",
 *   "complemento": "lado ímpar",
 *   "bairro": "Sé",
 *   "localidade": "São Paulo",
 *   "uf": "SP"
 * }
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true) // Ignora campos extras (ibge, gia, ddd, siafi)
public class ViaCepResponse {

    @JsonProperty("cep")
    private String cep;

    @JsonProperty("logradouro")
    private String logradouro;

    @JsonProperty("complemento")
    private String complemento;

    @JsonProperty("bairro")
    private String bairro;

    @JsonProperty("localidade") // A API retorna o nome da cidade como "localidade"
    private String localidade;

    @JsonProperty("uf")
    private String uf;

    @JsonProperty("erro") // Campo "erro": true quando CEP não existe
    private Boolean erro;
}
