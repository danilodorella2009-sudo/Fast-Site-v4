package com.example.demo.service;

import com.example.demo.model.Usuario;
import com.example.demo.repository.CadastroRepository;
import com.example.demo.repository.UsuarioRepository;
import com.example.demo.util.SenhaUtil;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * SERVICE: regras de negócio do login.
 */
@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final CadastroRepository cadastroRepository;

    // O Spring entrega os Repositories prontos (injeção de dependência)
    public UsuarioService(UsuarioRepository usuarioRepository, CadastroRepository cadastroRepository) {
        this.usuarioRepository = usuarioRepository;
        this.cadastroRepository = cadastroRepository;
    }

    /**
     * Confere email e senha.
     * @return o usuário, se a senha bater com o hash salvo; vazio caso contrário
     */
    public Optional<Usuario> autenticar(String email, String senha) {
        if (email == null || senha == null) {
            return Optional.empty();
        }
        return usuarioRepository.findByEmail(email.trim().toLowerCase())
                .filter(u -> SenhaUtil.conferir(senha, u.getSenhaHash()));
    }

    // Nome para mostrar na tela: o nome do cadastro, ou "Administrador"
    public String nomeParaExibir(Usuario u) {
        if (u.isAdmin()) {
            return "Administrador";
        }
        return cadastroRepository.findAtivoByEmail(u.getEmail())
                .map(c -> c.getNome())
                .orElse(u.getEmail());
    }

    /**
     * Garante que o email do administrador exista com perfil ADMIN.
     * Se já existir (ex.: a pessoa se cadastrou antes), só promove para ADMIN.
     * Se não existir, cria com a senha CRIPTOGRAFADA (BCrypt).
     */
    public void garantirAdmin(String email, String senha) {
        String emailLimpo = email.trim().toLowerCase();
        if (usuarioRepository.existsByEmail(emailLimpo)) {
            usuarioRepository.tornarAdmin(emailLimpo);
        } else {
            usuarioRepository.save(new Usuario(emailLimpo, SenhaUtil.criptografar(senha), Usuario.ADMIN));
        }
        // Se você trocar o email do admin no application.properties, o antigo perde o acesso
        usuarioRepository.rebaixarOutrosAdmins(emailLimpo);
    }
}
