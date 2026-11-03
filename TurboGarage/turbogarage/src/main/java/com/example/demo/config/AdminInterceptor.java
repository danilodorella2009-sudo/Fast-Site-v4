package com.example.demo.config;

import com.example.demo.controller.AuthController;
import com.example.demo.model.Usuario;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Protege as rotas da área administrativa: só o ADMIN pode usar.
 *   /api/cadastros/** (exceto POST /api/cadastros, que é o cadastro público)
 *   /api/veiculos/**  e  /api/ordens/**  (Parte 4)
 *     (exceto POST /api/veiculos: qualquer pessoa LOGADA pode cadastrar o próprio veículo)
 *   - sem login         -> 401
 *   - logado, não admin -> 403
 */
public class AdminInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        boolean ehCadastroNovo = "POST".equals(request.getMethod())
                && request.getRequestURI().equals("/api/cadastros");
        if (ehCadastroNovo) {
            return true;
        }

        HttpSession session = request.getSession(false);
        Object email = session != null ? session.getAttribute(AuthController.SESSAO_EMAIL) : null;
        Object perfil = session != null ? session.getAttribute(AuthController.SESSAO_PERFIL) : null;

        if (email == null) {
            return negar(response, HttpServletResponse.SC_UNAUTHORIZED, "Faça login para continuar");
        }

        // Cliente logado pode CADASTRAR veículo (só o próprio; o Service garante isso).
        // Listar, editar e excluir continuam só para o admin.
        boolean ehNovoVeiculo = "POST".equals(request.getMethod())
                && request.getRequestURI().equals("/api/veiculos");
        if (ehNovoVeiculo) {
            return true;
        }
        if (!Usuario.ADMIN.equals(perfil)) {
            return negar(response, HttpServletResponse.SC_FORBIDDEN, "Acesso permitido só para o administrador");
        }
        return true;
    }

    private boolean negar(HttpServletResponse response, int status, String mensagem) throws Exception {
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"mensagem\":\"" + mensagem + "\"}");
        return false;
    }
}
