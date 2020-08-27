package br.com.mgpapelaria.model;

import com.google.gson.Gson;

import java.io.Serializable;

import cielo.orders.domain.Order;

public class OrderRequest implements Serializable {
    private String order;
    private String pagamentos;

    public OrderRequest(PedidoWithPagamentos pedido) {
        this.order = new Gson().toJson(pedido.pedido.order);
        this.pagamentos = new Gson().toJson(pedido.pagamentos);
    }

    public String getOrder() {
        return order;
    }

    public String getPagamentos() {
        return pagamentos;
    }
}
