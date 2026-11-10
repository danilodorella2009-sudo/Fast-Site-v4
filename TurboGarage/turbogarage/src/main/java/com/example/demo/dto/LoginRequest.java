package com.example.demo.dto;

/**
 * Dados que a tela de login envia para POST /api/login (em JSON).
 */
public record LoginRequest(String email, String senha) {
}
