package com.example.demo.repository;

import com.example.demo.model.OrdemServico;
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
 * REPOSITORY: tabela "ordens_servico" (Spring JDBC / JdbcTemplate).
 * Os JOINs trazem a placa/modelo do veículo e o nome do cliente.
 */
@Repository
public class OrdemServicoRepository {

    private static final String SELECT_BASE =
            "SELECT o.id, o.veiculo_id, o.defeito, o.valor, o.status, o.data_abertura, "
                    + "v.placa, v.modelo, c.nome AS cliente_nome "
                    + "FROM ordens_servico o "
                    + "JOIN veiculos v ON v.id = o.veiculo_id "
                    + "LEFT JOIN cadastros c ON c.id = v.cadastro_id ";

    private final JdbcTemplate jdbc;

    public OrdemServicoRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private final RowMapper<OrdemServico> mapper = (rs, linha) -> {
        OrdemServico o = new OrdemServico();
        o.setId(rs.getLong("id"));
        o.setVeiculoId(rs.getLong("veiculo_id"));
        o.setDefeito(rs.getString("defeito"));
        o.setValor(rs.getBigDecimal("valor"));
        o.setStatus(rs.getString("status"));
        Timestamp data = rs.getTimestamp("data_abertura");
        o.setDataAbertura(data == null ? "" : new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(data));
        o.setVeiculoPlaca(rs.getString("placa"));
        o.setVeiculoModelo(rs.getString("modelo"));
        o.setClienteNome(rs.getString("cliente_nome"));
        return o;
    };

    // READ: todas
    public List<OrdemServico> findAll() {
        return jdbc.query(SELECT_BASE + "ORDER BY o.id DESC", mapper);
    }

    // READ: uma
    public Optional<OrdemServico> findById(Long id) {
        return jdbc.query(SELECT_BASE + "WHERE o.id = ?", mapper, id).stream().findFirst();
    }

    // CREATE (a data de abertura é a hora atual)
    public Long insert(OrdemServico o) {
        Timestamp agora = new Timestamp(System.currentTimeMillis() / 1000 * 1000);
        KeyHolder chave = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO ordens_servico (veiculo_id, defeito, valor, status, data_abertura) "
                            + "VALUES (?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, o.getVeiculoId());
            ps.setString(2, o.getDefeito());
            ps.setBigDecimal(3, o.getValor());
            ps.setString(4, o.getStatus());
            ps.setTimestamp(5, agora);
            return ps;
        }, chave);
        return chave.getKey().longValue();
    }

    // UPDATE
    public void update(Long id, OrdemServico o) {
        jdbc.update("UPDATE ordens_servico SET veiculo_id = ?, defeito = ?, valor = ?, status = ? WHERE id = ?",
                o.getVeiculoId(), o.getDefeito(), o.getValor(), o.getStatus(), id);
    }

    // DELETE (remoção definitiva)
    public void delete(Long id) {
        jdbc.update("DELETE FROM ordens_servico WHERE id = ?", id);
    }
}
