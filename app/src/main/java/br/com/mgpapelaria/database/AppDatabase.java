package br.com.mgpapelaria.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import br.com.mgpapelaria.dao.PedidoDAO;
import br.com.mgpapelaria.model.Pedido;

@Database(entities = {Pedido.class}, version = 1)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract PedidoDAO pedidoDAO();
}
