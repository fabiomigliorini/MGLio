package br.com.mgpapelaria.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import br.com.mgpapelaria.model.Pedido;
import cielo.orders.domain.Order;

@Dao
public interface PedidoDAO {
    @Query("SELECT * FROM pedido WHERE userId = :userId ORDER BY id DESC")
    List<Pedido> getAllByUserId(int userId);

    @Insert
    void insertPedido(Pedido pedido);

    @Query("UPDATE Pedido SET sincronizado=:valor WHERE orderId = :orderId")
    void updatePedidoSincronizado(String orderId, boolean valor);

    @Query("UPDATE Pedido SET status = 'CANCELED', `order` = :order WHERE orderId = :orderId")
    void cancelaOrder(String orderId, Order order);
}
