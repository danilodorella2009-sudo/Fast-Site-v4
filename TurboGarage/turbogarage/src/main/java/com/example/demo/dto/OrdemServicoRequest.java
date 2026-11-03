package com.example.demo.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * Dados que a tela de Ordens de Serviço envia em POST e PUT /api/ordens (JSON).
 */
public record OrdemServicoRequest(

        @NotNull(message = "Escolha o veículo")
        Long veiculoId,

        @NotBlank(message = "Descreva o defeito")
        @Size(max = 500, message = "Defeito muito longo (máx. 500 caracteres)")
        String defeito,

        @NotNull(message = "Valor é obrigatório")
        @DecimalMin(value = "0.00", message = "O valor não pode ser negativo")
        @DecimalMax(value = "99999999.99", message = "Valor muito alto")
        BigDecimal valor,

        @NotBlank(message = "Status é obrigatório")
        @Pattern(regexp = "ABERTA|EM_ANDAMENTO|CONCLUIDA|CANCELADA", message = "Status inválido")
        String status
) {
}
