package br.com.mgpapelaria.model;

public class LoginRequest {
    private final String usuario;
    private final String senha;

    public LoginRequest(String usuario, String senha) {
        this.usuario = usuario;
        this.senha = senha;
    }

    public String getUsuario() {
        return usuario;
    }

    public String getSenha() {
        return senha;
    }
}
