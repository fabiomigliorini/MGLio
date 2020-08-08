package br.com.mgpapelaria.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class VendaRegistrada implements Serializable {
    private int id;
    private String descricao;
    private long valor;
    private Date dataCriacao;

    public VendaRegistrada() {
    }

    public VendaRegistrada(int id, String descricao, long valor, Date dataCriacao) {
        this.id = id;
        this.descricao = descricao;
        this.valor = valor;
        this.dataCriacao = dataCriacao;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public long getValor() {
        return valor;
    }

    public void setValor(long valor) {
        this.valor = valor;
    }

    public Date getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(Date dataCriacao) {
        this.dataCriacao = dataCriacao;
    }
}
