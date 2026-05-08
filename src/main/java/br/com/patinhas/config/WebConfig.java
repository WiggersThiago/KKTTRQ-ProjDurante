package br.com.patinhas.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configurações adicionais de Web MVC.
 *
 * Permite expansão futura (interceptors, formatters, conversores etc.)
 * sem alterar a configuração padrão do Spring Boot.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        // Espaço reservado para conversores futuros (datas, enums, etc.).
    }
}
