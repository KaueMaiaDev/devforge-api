package br.com.devforge.config;

import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Libera todas as rotas (desafios, solucoes, etc)
                .allowedOrigins("*") // Libera para qualquer site (localhost etc)
                .allowedMethods("GET", "POST", "PUT", "OPTIONS"); // Libera esses métodos
    }
}
