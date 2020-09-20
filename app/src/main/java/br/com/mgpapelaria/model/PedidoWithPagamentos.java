package br.com.mgpapelaria.model;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.io.Serializable;
import java.util.List;

public class PedidoWithPagamentos implements Serializable {
    @Embedded
    public Pedido pedido;

    @Relation(parentColumn = "id", entityColumn = "pedidoId")
    public List<Pagamento> pagamentos;
}
