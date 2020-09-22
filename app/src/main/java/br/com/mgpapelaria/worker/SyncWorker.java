package br.com.mgpapelaria.worker;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.List;

import br.com.mgpapelaria.dao.PedidoDAO;
import br.com.mgpapelaria.database.AppDatabase;
import br.com.mgpapelaria.model.PedidoWithPagamentos;
import br.com.mgpapelaria.service.SendOrderServie;

public class SyncWorker extends Worker {
    public static String TAG = "SyncWorker";
    private final PedidoDAO pedidoDAO;
    private final SendOrderServie sendOrderServie;

    public SyncWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
        this.pedidoDAO = AppDatabase.build(context).pedidoDAO();
        this.sendOrderServie = new SendOrderServie(context);
    }

    @Override
    public Result doWork() {
        List<PedidoWithPagamentos> pedidosNaoSincronizados = pedidoDAO.getAllNaoSincronizadosWithPagamentos();
        for(PedidoWithPagamentos pedidoWithPagamentos : pedidosNaoSincronizados){
            sendOrderServie.sendSync(pedidoWithPagamentos);
        }

        return Result.success();
    }
}
