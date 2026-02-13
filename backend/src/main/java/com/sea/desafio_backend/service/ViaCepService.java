package com.sea.desafio_backend.service;

import com.sea.desafio_backend.dto.response.ViaCepResponse;
import com.sea.desafio_backend.exception.CepNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * Service para integração com API ViaCEP
 * Busca endereços completos a partir do CEP
 * API: https://viacep.com.br/
 */
@Service
@Slf4j // Lombok: Gera logger automático
public class ViaCepService {

    private static final String VIACEP_URL = "https://viacep.com.br/ws/{cep}/json/";
    
    private final RestTemplate restTemplate;

    /**
     * Constructor Injection (melhor prática do Spring)
     * @param restTemplate Bean configurado em RestTemplateConfig
     */
    public ViaCepService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Busca endereço completo por CEP na API ViaCEP
     * 
     * @param cep CEP a ser consultado (aceita com ou sem máscara: "01001-000" ou "01001000")
     * @return ViaCepResponse com dados completos do endereço
     * @throws CepNotFoundException se CEP for inválido ou não encontrado
     * @throws CepNotFoundException se houver erro de comunicação com API
     */
    public ViaCepResponse buscarEnderecoPorCep(String cep) {
        log.info("Buscando CEP na API ViaCEP: {}", cep);
        
        // Remove máscara do CEP (transforma "01001-000" em "01001000")
        String cepLimpo = removerMascara(cep);
        
        // Valida formato do CEP (deve ter exatamente 8 dígitos)
        validarFormatoCep(cepLimpo);

        try {
            // Faz requisição GET para API ViaCEP
            // {cep} na URL é substituído pelo valor de cepLimpo
            ViaCepResponse response = restTemplate.getForObject(
                VIACEP_URL, 
                ViaCepResponse.class, 
                cepLimpo
            );

            // Verifica se API retornou erro (CEP não encontrado)
            // ViaCEP retorna {"erro": true} quando CEP não existe
            if (response == null || Boolean.TRUE.equals(response.getErro())) {
                log.warn("CEP não encontrado na base do ViaCEP: {}", cep);
                throw new CepNotFoundException(cep);
            }

            log.info("CEP encontrado com sucesso: {} - {}/{}", cep, response.getLocalidade(), response.getUf());
            return response;

        } catch (RestClientException e) {
            // Captura erros de rede, timeout, etc
            log.error("Erro ao comunicar com API ViaCEP para o CEP: {}", cep, e);
            throw new CepNotFoundException(cep, e);
        }
    }

    /**
     * Valida se CEP tem formato válido (8 dígitos numéricos)
     * 
     * @param cep CEP a validar (sem máscara)
     * @return true se válido, false caso contrário
     */
    public boolean validarFormatoCep(String cep) {
        if (cep == null || cep.trim().isEmpty()) {
            log.warn("CEP nulo ou vazio");
            throw new IllegalArgumentException("CEP não pode ser nulo ou vazio");
        }
        
        String cepLimpo = removerMascara(cep);
        
        if (cepLimpo.length() != 8) {
            log.warn("CEP com formato inválido (deve ter 8 dígitos): {}", cep);
            throw new IllegalArgumentException("CEP deve ter 8 dígitos. Recebido: " + cep);
        }
        
        return true;
    }

    /**
     * Remove máscara do CEP (mantém apenas números)
     * Exemplo: "01001-000" vira "01001000"
     * 
     * @param cep CEP com ou sem máscara
     * @return CEP apenas com números
     */
    private String removerMascara(String cep) {
        if (cep == null) {
            return "";
        }
        return cep.replaceAll("[^0-9]", "");
    }
}
