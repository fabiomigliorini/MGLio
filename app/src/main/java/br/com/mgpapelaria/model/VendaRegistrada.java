package br.com.mgpapelaria.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class VendaRegistrada implements Serializable {
    @SerializedName("codnegocio")
    private Long codNegocio;
    private Date lancamento;
    private String fantasia;
    @SerializedName("valortotal")
    private BigDecimal valorTotal;
    @SerializedName("valorpago")
    private BigDecimal valorPago;
    @SerializedName("valorsaldo")
    private BigDecimal valorSaldo;

    public VendaRegistrada() {
    }

    public Long getCodNegocio() {
        return codNegocio;
    }

    public void setCodNegocio(Long codNegocio) {
        this.codNegocio = codNegocio;
    }

    public Date getLancamento() {
        return lancamento;
    }

    public void setLancamento(Date lancamento) {
        this.lancamento = lancamento;
    }

    public String getFantasia() {
        return fantasia;
    }

    public void setFantasia(String fantasia) {
        this.fantasia = fantasia;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public BigDecimal getValorPago() {
        return valorPago;
    }

    public void setValorPago(BigDecimal valorPago) {
        this.valorPago = valorPago;
    }

    public BigDecimal getValorSaldo() {
        return valorSaldo;
    }

    public void setValorSaldo(BigDecimal valorSaldo) {
        this.valorSaldo = valorSaldo;
    }
}
