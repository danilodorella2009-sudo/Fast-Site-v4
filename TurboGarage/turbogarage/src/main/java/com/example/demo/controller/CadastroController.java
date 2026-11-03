package com.example.demo.controller;

import com.example.demo.dto.CadastroRequest;
import com.example.demo.model.Cadastro;
import com.example.demo.service.CadastroService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * CONTROLLER: cadastros de clientes.
 *   POST   /api/cadastros       -> qualquer pessoa se cadastra (cria também o login)
 *   GET    /api/cadastros       -> lista todos         (SÓ ADMIN)
 *   GET    /api/cadastros/{id}  -> busca um            (SÓ ADMIN)
 *   DELETE /api/cadastros/{id}  -> inativa/soft delete (SÓ ADMIN)
 * A proteção "só admin" fica no config/AdminInterceptor.
 */
@RestController
@RequestMapping("/api/cadastros")
public class CadastroController {

    private final CadastroService cadastroService;

    public CadastroController(CadastroService cadastroService) {
        this.cadastroService = cadastroService;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> criar(@Valid @RequestBody CadastroRequest dados) {
        Cadastro salvo = cadastroService.salvar(dados);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "mensagem", "Cadastro realizado com sucesso!",
                "id", salvo.getId()));
    }

    @GetMapping
    public List<Cadastro> listar() {
        return cadastroService.listar();
    }

    @GetMapping("/{id}")
    public Cadastro buscar(@PathVariable Long id) {
        return cadastroService.buscarPorId(id);
    }

    @DeleteMapping("/{id}")
    public Map<String, String> excluir(@PathVariable Long id) {
        cadastroService.inativar(id);
        return Map.of("mensagem", "Cadastro excluído");
    }
}
