package com.example.demo.controller;

import com.example.demo.dto.LoginRequest;
import com.example.demo.model.Usuario;
import com.example.demo.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

/**
 * CONTROLLER: login, "quem está logado" e logout.
 * Endereços: POST /api/login, GET /api/usuario, POST /api/logout
 */
@RestController
@RequestMapping("/api")
public class AuthController {

    // Nomes das informações guardadas na sessão
    public static final String SESSAO_EMAIL = "usuarioLogado";
    public static final String SESSAO_PERFIL = "perfil";
    public static final String SESSAO_NOME = "nome";

    private final UsuarioService usuarioService;

    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest dados, HttpSession session) {
        Optional<Usuario> resultado = usuarioService.autenticar(dados.email(), dados.senha());

        if (resultado.isEmpty()) {
            // Mensagem genérica: não revela se o erro foi no email ou na senha
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("mensagem", "Email ou senha inválidos"));
        }

        // Login certo: guarda na SESSÃO quem é a pessoa e se ela é ADMIN
        Usuario u = resultado.get();
        String nome = usuarioService.nomeParaExibir(u);
        session.setAttribute(SESSAO_EMAIL, u.getEmail());
        session.setAttribute(SESSAO_PERFIL, u.getPerfil());
        session.setAttribute(SESSAO_NOME, nome);
        return ResponseEntity.ok(Map.of("mensagem", "Login realizado!", "admin", u.isAdmin()));
    }

    @GetMapping("/usuario")
    public ResponseEntity<Map<String, Object>> usuarioLogado(HttpSession session) {
        Object email = session.getAttribute(SESSAO_EMAIL);
        if (email == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("mensagem", "Não logado"));
        }
        Object nome = session.getAttribute(SESSAO_NOME);
        return ResponseEntity.ok(Map.of(
                "email", email.toString(),
                "nome", nome != null ? nome.toString() : email.toString(),
                "admin", Usuario.ADMIN.equals(session.getAttribute(SESSAO_PERFIL))));
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok(Map.of("mensagem", "Saiu"));
    }
}
