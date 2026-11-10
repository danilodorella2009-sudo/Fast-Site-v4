package com.example.demo.model;

/**
 * MODEL 3 (Parte 4): veículo de um cliente (tabela "veiculos").
 * Relacionamento: muitos veículos -> um cliente (cadastro_id = chave estrangeira).
 */
public class Veiculo {

    private Long id;
    private String placa;
    private String modelo;
    private Integer ano;
    private Long clienteId;     // coluna cadastro_id
    private String clienteNome; // vem do JOIN com "cadastros" (só leitura)

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPlaca() { return placa; }
    public void setPlaca(String placa) { this.placa = placa; }

    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }

    public Integer getAno() { return ano; }
    public void setAno(Integer ano) { this.ano = ano; }

    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }

    public String getClienteNome() { return clienteNome; }
    public void setClienteNome(String clienteNome) { this.clienteNome = clienteNome; }
}
