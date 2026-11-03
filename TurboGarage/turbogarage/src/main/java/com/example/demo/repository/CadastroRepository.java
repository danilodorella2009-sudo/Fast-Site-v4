package com.example.demo.repository;

import com.example.demo.model.Cadastro;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;

/**
 * REPOSITORY: conversa com a tabela "cadastros" usando Spring JDBC (JdbcTemplate).
 */
@Repository
public class CadastroRepository {

    private final JdbcTemplate jdbc;

    public CadastroRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private static String formatar(Timestamp data) {
        return data == null ? "" : new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(data);
    }

    // Converte uma linha do banco (ResultSet) em um objeto Cadastro
    private final RowMapper<Cadastro> mapper = (rs, linha) -> {
        Cadastro c = new Cadastro();
        c.setId(rs.getLong("id"));
        c.setNome(rs.getString("nome"));
        c.setEmail(rs.getString("email"));
        c.setEndereco(rs.getString("endereco"));
        c.setCep(rs.getString("cep"));
        c.setNumero(rs.getString("numero"));
        c.setTelefone(rs.getString("telefone"));
        c.setObservacao(rs.getString("observacao"));
        c.setDataCriacao(formatar(rs.getTimestamp("data_criacao")));
        c.setAtivo(rs.getBoolean("ativo"));
        return c;
    };

    // INSERT e devolve o cadastro já com o id gerado pelo banco
    public Cadastro insert(Cadastro c) {
        String sql = "INSERT INTO cadastros (nome, email, endereco, cep, numero, telefone, observacao, data_criacao, ativo) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, 1)";
        // Desafio 3: data de criação automática (hora atual, sem milissegundos)
        Timestamp agora = new Timestamp(System.currentTimeMillis() / 1000 * 1000);
        KeyHolder chave = new GeneratedKeyHolder();

        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, c.getNome());
            ps.setString(2, c.getEmail());
            ps.setString(3, c.getEndereco());
            ps.setString(4, c.getCep());
            ps.setString(5, c.getNumero());
            ps.setString(6, c.getTelefone());
            ps.setString(7, c.getObservacao());
            ps.setTimestamp(8, agora);
            return ps;
        }, chave);

        c.setId(chave.getKey().longValue());
        c.setDataCriacao(formatar(agora));
        c.setAtivo(true);
        return c;
    }

    public List<Cadastro> findAtivos() {
        return jdbc.query("SELECT * FROM cadastros WHERE ativo = 1 ORDER BY id DESC", mapper);
    }

    public Optional<Cadastro> findAtivoById(Long id) {
        return jdbc.query("SELECT * FROM cadastros WHERE id = ? AND ativo = 1", mapper, id)
                .stream().findFirst();
    }

    public Optional<Cadastro> findAtivoByEmail(String email) {
        return jdbc.query("SELECT * FROM cadastros WHERE email = ? AND ativo = 1", mapper, email)
                .stream().findFirst();
    }

    // Soft delete: não apaga a linha, só marca como inativa
    public void inativar(Long id) {
        jdbc.update("UPDATE cadastros SET ativo = 0 WHERE id = ?", id);
    }
}
