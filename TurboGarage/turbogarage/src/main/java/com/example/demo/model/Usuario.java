package com.example.demo.model;

/**
 * MODEL 1: conta de login (tabela "usuarios").
 * perfil = "ADMIN" (vê os cadastros salvos) ou "CLIENTE" (pessoa que se cadastrou).
 */
public class Usuario {

    public static final String ADMIN = "ADMIN";
    public static final String CLIENTE = "CLIENTE";

    private Long id;
    private String email;
    private String senhaHash; // HASH BCrypt da senha, nunca a senha pura
    private String perfil;

    public Usuario() {
    }

    public Usuario(String email, String senhaHash, String perfil) {
        this.email = email;
        this.senhaHash = senhaHash;
        this.perfil = perfil;
    }

    public boolean isAdmin() {
        return ADMIN.equals(perfil);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSenhaHash() { return senhaHash; }
    public void setSenhaHash(String senhaHash) { this.senhaHash = senhaHash; }

    public String getPerfil() { return perfil; }
    public void setPerfil(String perfil) { this.perfil = perfil; }
}
