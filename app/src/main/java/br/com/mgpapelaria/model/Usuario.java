package br.com.mgpapelaria.model;

import com.google.gson.annotations.SerializedName;

public class Usuario {
    @SerializedName("codusuario")
    private Integer id;
    private String usuario;
    @SerializedName("codfilial")
    private Integer filialId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public Integer getFilialId() {
        return filialId;
    }

    public void setFilialId(Integer filialId) {
        this.filialId = filialId;
    }
}
