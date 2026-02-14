package com.sea.desafio_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuração de Segurança do Spring Security
 * 
 * Configurações:
 * - Permite todos os acessos (temporariamente)
 * - Desabilita CSRF para facilitar testes
 * - BCrypt para encoding de senhas
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Configura a cadeia de filtros de segurança
     * Permite todos os acessos para facilitar desenvolvimento/testes
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable() // Desabilitar CSRF para APIs REST
            .authorizeRequests()
                .antMatchers("/actuator/**").permitAll() // Endpoints do Actuator
                .antMatchers("/api/**").permitAll() // Todos os endpoints da API
                .anyRequest().permitAll() // Qualquer outra requisição
            .and()
            .httpBasic().disable() // Desabilitar autenticação básica
            .formLogin().disable(); // Desabilitar form login

        return http.build();
    }

    /**
     * Bean para encoder de senhas usando BCrypt
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
