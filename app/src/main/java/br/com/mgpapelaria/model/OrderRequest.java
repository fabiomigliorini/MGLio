package br.com.mgpapelaria.model;

import com.google.gson.Gson;

import java.io.Serializable;

public class OrderRequest implements Serializable {
    private final String order;
    private final String pagamentos;

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
