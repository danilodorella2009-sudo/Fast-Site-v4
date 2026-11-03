package com.example.demo.controller;

import com.example.demo.dto.OrdemServicoRequest;
import com.example.demo.model.OrdemServico;
import com.example.demo.service.OrdemServicoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * CONTROLLER (Parte 4): CRUD de ordens de serviço. Só o ADMIN acessa (AdminInterceptor).
 *   GET    /api/ordens       -> lista            (200)
 *   GET    /api/ordens/{id}  -> uma              (200 ou 404)
 *   POST   /api/ordens       -> cria             (201)
 *   PUT    /api/ordens/{id}  -> atualiza         (200)
 *   DELETE /api/ordens/{id}  -> exclui           (200)
 */
@RestController
@RequestMapping("/api/ordens")
public class OrdemServicoController {

    private final OrdemServicoService service;

    public OrdemServicoController(OrdemServicoService service) {
        this.service = service;
    }

    @GetMapping
    public List<OrdemServico> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public OrdemServico buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> criar(@Valid @RequestBody OrdemServicoRequest dados) {
        OrdemServico o = service.criar(dados);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("mensagem", "Registro salvo com sucesso!", "registro", o));
    }

    @PutMapping("/{id}")
    public Map<String, Object> atualizar(@PathVariable Long id, @Valid @RequestBody OrdemServicoRequest dados) {
        OrdemServico o = service.atualizar(id, dados);
        return Map.of("mensagem", "Registro atualizado", "registro", o);
    }

    @DeleteMapping("/{id}")
    public Map<String, String> excluir(@PathVariable Long id) {
        service.excluir(id);
        return Map.of("mensagem", "Registro excluído");
    }
}
