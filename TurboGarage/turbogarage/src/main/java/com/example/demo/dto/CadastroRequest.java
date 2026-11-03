package com.example.demo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Dados que o formulário de cadastro envia para POST /api/cadastros (em JSON).
 * As anotações fazem a validação dos campos obrigatórios no back-end.
 */
public record CadastroRequest(

        @NotBlank(message = "Nome é obrigatório")
        @Size(max = 150, message = "Nome muito longo")
        String nome,

        @NotBlank(message = "Email é obrigatório")
        @Email(message = "Email inválido")
        @Size(max = 150, message = "Email muito longo")
        String email,

        @NotBlank(message = "Senha é obrigatória")
        @Size(min = 6, max = 72, message = "A senha precisa ter de 6 a 72 caracteres")
        String senha,

        @NotBlank(message = "Endereço é obrigatório")
        @Size(max = 200, message = "Endereço muito longo")
        String endereco,

        @NotBlank(message = "CEP é obrigatório")
        @Pattern(regexp = "\\d{5}-?\\d{3}", message = "CEP inválido (use 00000-000)")
        String cep,

        @NotBlank(message = "Número é obrigatório")
        @Size(max = 10, message = "Número muito longo")
        String numero,

        @NotBlank(message = "Telefone é obrigatório")
        @Pattern(regexp = "\\(?\\d{2}\\)?\\s?\\d{4,5}-?\\d{4}", message = "Telefone inválido (use (00) 90000-0000)")
        String telefone,

        @NotBlank(message = "Observação é obrigatória")
        @Size(max = 2000, message = "Observação muito longa")
        String observacao
) {
}
