package com.sea.desafio_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * Configuração do RestTemplate para chamadas HTTP
 * Utilizado para consumir APIs externas como ViaCEP
 */
@Configuration
public class RestTemplateConfig {

    /**
     * Bean RestTemplate com timeout configurado
     * @return RestTemplate configurado e pronto para uso
     */
    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        
        // Configuração de timeout para evitar travamentos
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000); // 5 segundos para conectar
        factory.setReadTimeout(5000);    // 5 segundos para ler resposta
        
        restTemplate.setRequestFactory(factory);
        
        return restTemplate;
    }
}
