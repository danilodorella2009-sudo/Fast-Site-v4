package com.example.demo.controller;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * TRATAMENTO GLOBAL DE EXCEÇÕES (desafio 3 da Parte 4).
 * Todo erro vira um JSON simples { "mensagem": "..." } para a tela exibir.
 */
@RestControllerAdvice
public class ErroHandler {

    // 400: campo obrigatório vazio ou inválido
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> validacao(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getDefaultMessage())
                .distinct()
                .collect(Collectors.joining("; "));
        return ResponseEntity.badRequest().body(Map.of("mensagem", msg));
    }

    // 400: JSON mal formado (ex.: texto no lugar de número)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> jsonInvalido(HttpMessageNotReadableException e) {
        return ResponseEntity.badRequest().body(Map.of("mensagem", "Dados enviados em formato inválido"));
    }

    // 400: id inválido na URL (ex.: /api/veiculos/abc)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, String>> idInvalido(MethodArgumentTypeMismatchException e) {
        return ResponseEntity.badRequest().body(Map.of("mensagem", "Valor inválido: " + e.getValue()));
    }

    // 409: valor repetido em coluna UNIQUE (email, placa)
    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<Map<String, String>> duplicado(DuplicateKeyException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("mensagem", "Registro duplicado: este valor já está cadastrado"));
    }

    // 409: chave estrangeira (ex.: excluir algo que outro registro usa)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> integridade(DataIntegrityViolationException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("mensagem", "Operação não permitida: existem registros ligados a este"));
    }

    // 404 / 409 / 400 lançados pelos Services
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, String>> status(ResponseStatusException e) {
        String msg = e.getReason() != null ? e.getReason() : "Erro";
        return ResponseEntity.status(e.getStatusCode()).body(Map.of("mensagem", msg));
    }

    // Qualquer outro erro
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> outro(Exception e) {
        // Erros "normais" do Spring (404 de arquivo inexistente, 405 método errado...)
        if (e instanceof ErrorResponse er) {
            return ResponseEntity.status(er.getStatusCode())
                    .body(Map.of("mensagem", "Erro " + er.getStatusCode().value()));
        }
        // Erro inesperado: mostra no console E manda o motivo para a tela
        e.printStackTrace();
        Throwable causa = e;
        while (causa.getCause() != null) {
            causa = causa.getCause();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("mensagem", "Erro no servidor: " + causa.getClass().getSimpleName()
                        + " - " + causa.getMessage()));
    }
}
