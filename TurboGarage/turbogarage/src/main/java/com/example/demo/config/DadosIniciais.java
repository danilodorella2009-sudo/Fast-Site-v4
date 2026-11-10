package com.example.demo.config;

import com.example.demo.service.UsuarioService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Roda sozinho quando o servidor liga:
 *  1) adiciona colunas novas em tabelas que já existiam (perfil, email)
 *  2) garante que o email de app.admin.email seja ADMIN (senha em BCrypt)
 */
@Component
public class DadosIniciais implements CommandLineRunner {

    private final UsuarioService usuarioService;
    private final JdbcTemplate jdbc;

    @Value("${app.admin.email}")
    private String email;

    @Value("${app.admin.senha}")
    private String senha;

    public DadosIniciais(UsuarioService usuarioService, JdbcTemplate jdbc) {
        this.usuarioService = usuarioService;
        this.jdbc = jdbc;
    }

    @Override
    public void run(String... args) {
        adicionarColunaSeFaltar("usuarios", "perfil", "VARCHAR(20) NOT NULL DEFAULT 'CLIENTE'");
        adicionarColunaSeFaltar("cadastros", "email", "VARCHAR(150) NULL");

        usuarioService.garantirAdmin(email, senha);
        System.out.println(">>> Administrador: " + email);
    }

    // Se a tabela foi criada por uma versão antiga do projeto, cria a coluna que falta
    private void adicionarColunaSeFaltar(String tabela, String coluna, String tipo) {
        Integer existe = jdbc.queryForObject(
                "SELECT COUNT(*) FROM information_schema.COLUMNS "
                        + "WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = ? AND COLUMN_NAME = ?",
                Integer.class, tabela, coluna);
        if (existe == null || existe == 0) {
            jdbc.execute("ALTER TABLE " + tabela + " ADD COLUMN " + coluna + " " + tipo);
            System.out.println(">>> Coluna criada: " + tabela + "." + coluna);
        }
    }
}
