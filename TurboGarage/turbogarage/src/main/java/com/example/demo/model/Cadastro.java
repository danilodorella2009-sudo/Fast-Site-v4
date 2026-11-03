package com.example.demo.model;

/**
 * MODEL 2: cliente cadastrado na oficina (tabela "cadastros").
 * 6 campos obrigatórios + email (login) + data_criacao e ativo (desafio 3).
 * A senha NÃO fica aqui: ela vai criptografada para a tabela "usuarios".
 */
public class Cadastro {

    private Long id;
    private String nome;
    private String email;
    private String endereco;
    private String cep;
    private String numero;
    private String telefone;
    private String observacao;
    // Guardada como texto já formatado (ex.: 27/10/2026 15:13:02)
    // para a resposta JSON não dar erro com tipos de data
    private String dataCriacao;
    private boolean ativo = true;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }

    public String getCep() { return cep; }
    public void setCep(String cep) { this.cep = cep; }

    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public String getObservacao() { return observacao; }
    public void setObservacao(String observacao) { this.observacao = observacao; }

    public String getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(String dataCriacao) { this.dataCriacao = dataCriacao; }

    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }
}
