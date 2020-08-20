package br.com.mgpapelaria.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.Date;

import cielo.orders.domain.Order;

@Entity
public class Pedido implements Serializable {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public int userId;
    public String orderId;
    public String nome;
    public Date data;
    public long valor;
    public String status;
    public boolean sincronizado;
    public Order order;
}
