package br.com.mgpapelaria.model;

import com.google.gson.Gson;

import java.io.Serializable;

import cielo.orders.domain.Order;

public class OrderRequest implements Serializable {
    private String order;

    public OrderRequest(Order order) {
        this.order = new Gson().toJson(order);
    }

    public String getOrder() {
        return order;
    }
}
