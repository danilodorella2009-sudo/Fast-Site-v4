package com.example.demo.controller;

import com.example.demo.dto.VeiculoRequest;
import com.example.demo.model.Usuario;
import com.example.demo.model.Veiculo;
import com.example.demo.service.VeiculoService;
import jakarta.servlet.http.HttpSession;
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
 * CONTROLLER (Parte 4): CRUD de veículos (proteção no AdminInterceptor).
 *   O CLIENTE logado só pode usar o POST (cadastrar o próprio veículo).
 *   Todo o resto é só do ADMIN.
 *   GET    /api/veiculos       -> lista            (200)
 *   GET    /api/veiculos/{id}  -> um               (200 ou 404)
 *   POST   /api/veiculos       -> cria             (201)
 *   PUT    /api/veiculos/{id}  -> atualiza         (200)
 *   DELETE /api/veiculos/{id}  -> exclui           (200)
 */
@RestController
@RequestMapping("/api/veiculos")
public class VeiculoController {

    private final VeiculoService service;

    public VeiculoController(VeiculoService service) {
        this.service = service;
    }

    @GetMapping
    public List<Veiculo> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public Veiculo buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> criar(@Valid @RequestBody VeiculoRequest dados, HttpSession session) {
        String email = (String) session.getAttribute(AuthController.SESSAO_EMAIL);
        boolean admin = Usuario.ADMIN.equals(session.getAttribute(AuthController.SESSAO_PERFIL));
        Veiculo v = service.criar(dados, email, admin);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("mensagem", "Registro salvo com sucesso!", "registro", v));
    }

    @PutMapping("/{id}")
    public Map<String, Object> atualizar(@PathVariable Long id, @Valid @RequestBody VeiculoRequest dados) {
        Veiculo v = service.atualizar(id, dados);
        return Map.of("mensagem", "Registro atualizado", "registro", v);
    }

    @DeleteMapping("/{id}")
    public Map<String, String> excluir(@PathVariable Long id) {
        service.excluir(id);
        return Map.of("mensagem", "Registro excluído");
    }
}
