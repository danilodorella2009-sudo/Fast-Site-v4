package com.example.demo.repository;

import com.example.demo.model.Usuario;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * REPOSITORY: conversa com a tabela "usuarios" usando Spring JDBC (JdbcTemplate).
 * Os "?" são preenchidos pelo Spring, o que evita SQL Injection.
 */
@Repository
public class UsuarioRepository {

    private final JdbcTemplate jdbc;

    public UsuarioRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public Optional<Usuario> findByEmail(String email) {
        List<Usuario> lista = jdbc.query(
                "SELECT id, email, senha_hash, perfil FROM usuarios WHERE email = ?",
                (rs, linha) -> {
                    Usuario u = new Usuario();
                    u.setId(rs.getLong("id"));
                    u.setEmail(rs.getString("email"));
                    u.setSenhaHash(rs.getString("senha_hash"));
                    u.setPerfil(rs.getString("perfil"));
                    return u;
                },
                email);
        return lista.stream().findFirst();
    }

    public boolean existsByEmail(String email) {
        Integer total = jdbc.queryForObject(
                "SELECT COUNT(*) FROM usuarios WHERE email = ?", Integer.class, email);
        return total != null && total > 0;
    }

    public void save(Usuario usuario) {
        jdbc.update("INSERT INTO usuarios (email, senha_hash, perfil) VALUES (?, ?, ?)",
                usuario.getEmail(), usuario.getSenhaHash(), usuario.getPerfil());
    }

    public void tornarAdmin(String email) {
        jdbc.update("UPDATE usuarios SET perfil = 'ADMIN' WHERE email = ?", email);
    }

    // Só pode existir UM admin: quem não for o email informado volta a ser CLIENTE
    public void rebaixarOutrosAdmins(String emailAdmin) {
        jdbc.update("UPDATE usuarios SET perfil = 'CLIENTE' WHERE perfil = 'ADMIN' AND email <> ?", emailAdmin);
    }

    // Remove o login de um CLIENTE (o admin nunca é apagado por aqui)
    public void excluirCliente(String email) {
        jdbc.update("DELETE FROM usuarios WHERE email = ? AND perfil <> 'ADMIN'", email);
    }
}
