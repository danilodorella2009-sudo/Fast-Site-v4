package com.example.demo.model;

import java.math.BigDecimal;

/**
 * MODEL 4 (Parte 4): ordem de serviço (tabela "ordens_servico").
 * Relacionamento: muitas ordens -> um veículo (veiculo_id = chave estrangeira).
 */
public class OrdemServico {

    // Status possíveis de uma ordem
    public static final String ABERTA = "ABERTA";
    public static final String EM_ANDAMENTO = "EM_ANDAMENTO";
    public static final String CONCLUIDA = "CONCLUIDA";
    public static final String CANCELADA = "CANCELADA";

    private Long id;
    private Long veiculoId;
    private String defeito;
    private BigDecimal valor;
    private String status;
    private String dataAbertura;  // texto já formatado (dd/MM/yyyy HH:mm:ss)

    // Vêm do JOIN com "veiculos" e "cadastros" (só leitura)
    private String veiculoPlaca;
    private String veiculoModelo;
    private String clienteNome;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getVeiculoId() { return veiculoId; }
    public void setVeiculoId(Long veiculoId) { this.veiculoId = veiculoId; }

    public String getDefeito() { return defeito; }
    public void setDefeito(String defeito) { this.defeito = defeito; }

    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getDataAbertura() { return dataAbertura; }
    public void setDataAbertura(String dataAbertura) { this.dataAbertura = dataAbertura; }

    public String getVeiculoPlaca() { return veiculoPlaca; }
    public void setVeiculoPlaca(String veiculoPlaca) { this.veiculoPlaca = veiculoPlaca; }

    public String getVeiculoModelo() { return veiculoModelo; }
    public void setVeiculoModelo(String veiculoModelo) { this.veiculoModelo = veiculoModelo; }

    public String getClienteNome() { return clienteNome; }
    public void setClienteNome(String clienteNome) { this.clienteNome = clienteNome; }
}
