package br.com.mgpapelaria.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import br.com.mgpapelaria.dao.PedidoDAO;
import br.com.mgpapelaria.model.Pedido;

@Database(entities = {Pedido.class}, version = 1)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {
    public static String DATABASE_NAME = "mg_cielo_lio.db";
    public abstract PedidoDAO pedidoDAO();

    public static AppDatabase build(Context context){
        return Room
                .databaseBuilder(context, AppDatabase.class, DATABASE_NAME)
                .build();
    }
}
