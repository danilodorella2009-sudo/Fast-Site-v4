package com.example.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Liga o AdminInterceptor nas rotas da área administrativa.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AdminInterceptor())
                .addPathPatterns(
                        "/api/cadastros", "/api/cadastros/**",
                        "/api/veiculos", "/api/veiculos/**",   // Parte 4
                        "/api/ordens", "/api/ordens/**");      // Parte 4
    }
}
