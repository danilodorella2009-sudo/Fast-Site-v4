package com.example.demo.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Dados que a tela de Veículos envia em POST e PUT /api/veiculos (JSON).
 */
public record VeiculoRequest(

        // Aceita placa antiga (ABC-1234) e Mercosul (ABC1D23)
        @NotBlank(message = "Placa é obrigatória")
        @Pattern(regexp = "(?i)[A-Z]{3}-?[0-9][A-Z0-9][0-9]{2}", message = "Placa inválida (ex.: ABC-1234 ou ABC1D23)")
        String placa,

        @NotBlank(message = "Modelo é obrigatório")
        @Size(max = 100, message = "Modelo muito longo")
        String modelo,

        @NotNull(message = "Ano é obrigatório")
        @Min(value = 1950, message = "Ano deve ser a partir de 1950")
        @Max(value = 2100, message = "Ano inválido")
        Integer ano,

        // Obrigatório só para o ADMIN. Para o CLIENTE é ignorado:
        // o veículo é ligado ao cadastro de quem está logado (ver VeiculoService)
        Long clienteId
) {
}
