package com.example.demo.repository;

import com.example.demo.model.Veiculo;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

/**
 * REPOSITORY: tabela "veiculos" (Spring JDBC / JdbcTemplate).
 * O LEFT JOIN traz junto o nome do cliente dono do veículo.
 */
@Repository
public class VeiculoRepository {

    private static final String SELECT_BASE =
            "SELECT v.id, v.placa, v.modelo, v.ano, v.cadastro_id, c.nome AS cliente_nome "
                    + "FROM veiculos v LEFT JOIN cadastros c ON c.id = v.cadastro_id ";

    private final JdbcTemplate jdbc;

    public VeiculoRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private final RowMapper<Veiculo> mapper = (rs, linha) -> {
        Veiculo v = new Veiculo();
        v.setId(rs.getLong("id"));
        v.setPlaca(rs.getString("placa"));
        v.setModelo(rs.getString("modelo"));
        v.setAno(rs.getInt("ano"));
        v.setClienteId(rs.getLong("cadastro_id"));
        v.setClienteNome(rs.getString("cliente_nome"));
        return v;
    };

    // READ: todos
    public List<Veiculo> findAll() {
        return jdbc.query(SELECT_BASE + "ORDER BY v.id DESC", mapper);
    }

    // READ: um
    public Optional<Veiculo> findById(Long id) {
        return jdbc.query(SELECT_BASE + "WHERE v.id = ?", mapper, id).stream().findFirst();
    }

    // Placa repetida? (idIgnorado: na edição, não conta o próprio veículo)
    public boolean existsPlaca(String placa, Long idIgnorado) {
        Integer total = jdbc.queryForObject(
                "SELECT COUNT(*) FROM veiculos WHERE placa = ? AND id <> ?",
                Integer.class, placa, idIgnorado == null ? 0L : idIgnorado);
        return total != null && total > 0;
    }

    // Quantas ordens de serviço usam este veículo
    public int contarOrdens(Long veiculoId) {
        Integer total = jdbc.queryForObject(
                "SELECT COUNT(*) FROM ordens_servico WHERE veiculo_id = ?", Integer.class, veiculoId);
        return total == null ? 0 : total;
    }

    // CREATE
    public Long insert(Veiculo v) {
        KeyHolder chave = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO veiculos (placa, modelo, ano, cadastro_id) VALUES (?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, v.getPlaca());
            ps.setString(2, v.getModelo());
            ps.setInt(3, v.getAno());
            ps.setLong(4, v.getClienteId());
            return ps;
        }, chave);
        return chave.getKey().longValue();
    }

    // UPDATE
    public void update(Long id, Veiculo v) {
        jdbc.update("UPDATE veiculos SET placa = ?, modelo = ?, ano = ?, cadastro_id = ? WHERE id = ?",
                v.getPlaca(), v.getModelo(), v.getAno(), v.getClienteId(), id);
    }

    // DELETE (remoção definitiva)
    public void delete(Long id) {
        jdbc.update("DELETE FROM veiculos WHERE id = ?", id);
    }
}
