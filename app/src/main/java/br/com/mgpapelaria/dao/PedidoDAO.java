package br.com.mgpapelaria.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import br.com.mgpapelaria.model.Pedido;
import br.com.mgpapelaria.model.PedidoWithPagamentos;
import cielo.orders.domain.Order;

@Dao
public interface PedidoDAO {
    @Query("SELECT id, userId, nome, data, valor, status, sincronizado FROM pedido ORDER BY id DESC")
    List<Pedido> getAll();

    @Query("SELECT * FROM Pedido WHERE id = :id")
    PedidoWithPagamentos getWithPagamentosById(int id);

    @Query("SELECT * FROM Pedido WHERE sincronizado=0 ORDER BY id ASC")
    List<PedidoWithPagamentos> getAllNaoSincronizadosWithPagamentos();

    @Query("SELECT id FROM Pedido WHERE orderId = :orderId")
    int getPedidoIdByOrderId(String orderId);

    @Insert
    long insertPedido(Pedido pedido);

    @Query("UPDATE Pedido SET sincronizado=:valor WHERE orderId = :orderId")
    void updatePedidoSincronizado(String orderId, boolean valor);

    @Query("UPDATE Pedido SET status = 'CANCELED', `order` = :order WHERE orderId = :orderId")
    void cancelaOrder(String orderId, Order order);
}
