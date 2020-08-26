package br.com.mgpapelaria.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import br.com.mgpapelaria.dao.PagamentoDAO;
import br.com.mgpapelaria.dao.PedidoDAO;
import br.com.mgpapelaria.model.Pagamento;
import br.com.mgpapelaria.model.Pedido;

@Database(entities = {
        Pedido.class,
        Pagamento.class
}, version = 2)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {
    public static String DATABASE_NAME = "mg_cielo_lio.db";
    public abstract PedidoDAO pedidoDAO();
    public abstract PagamentoDAO pagamentoDAO();

    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE `Pagamento` (`id` INTEGER NOT NULL, "
                    + "`pedidoId` INTEGER NOT NULL, `paymentId` TEXT NOT NULL, "
                    + "`userId` INTEGER NOT NULL, `userName` TEXT NOT NULL,  PRIMARY KEY(`id`))");
        }
    };

    public static AppDatabase build(Context context){
        return Room
                .databaseBuilder(context, AppDatabase.class, DATABASE_NAME)
                .addMigrations(MIGRATION_1_2)
                .build();
    }
}
