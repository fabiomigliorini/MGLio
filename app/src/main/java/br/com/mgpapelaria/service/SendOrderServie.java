package br.com.mgpapelaria.service;

import android.content.Context;
import android.os.AsyncTask;

import java.io.IOException;

import br.com.mgpapelaria.api.ApiService;
import br.com.mgpapelaria.api.RetrofitUtil;
import br.com.mgpapelaria.dao.PedidoDAO;
import br.com.mgpapelaria.database.AppDatabase;
import br.com.mgpapelaria.model.OrderRequest;
import br.com.mgpapelaria.model.PedidoWithPagamentos;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SendOrderServie {
    private ApiService apiService;
    private PedidoDAO pedidoDAO;

    public SendOrderServie(Context context) {
        this.pedidoDAO = AppDatabase.build(context).pedidoDAO();
        this.apiService = RetrofitUtil.createService(context, ApiService.class);
    }

    public interface SendOrderListner{
        void onFinish(boolean result);
    }

    public void sendAsync(PedidoWithPagamentos pedidoWithPagamentos){
        this.sendAsync(pedidoWithPagamentos, null);
    }

    public boolean sendSync(PedidoWithPagamentos pedidoWithPagamentos) {
        try {
            Response<Void> response = this.apiService.updateOrder(new OrderRequest(pedidoWithPagamentos)).execute();
            if(response.code() == 200){
                pedidoDAO.updatePedidoSincronizado(pedidoWithPagamentos.pedido.order.getId(), true);
                /*AsyncTask.execute(() -> {
                    pedidoDAO.updatePedidoSincronizado(pedidoWithPagamentos.pedido.order.getId(), true);
                });*/
                return true;
            }
            return false;
        } catch (IOException e) {
            return false;
        }
    }

    public void sendAsync(PedidoWithPagamentos pedidoWithPagamentos, SendOrderListner listner){
        this.apiService.updateOrder(new OrderRequest(pedidoWithPagamentos)).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                boolean result = false;
                if(response.code() == 200){
                    result = true;
                    if(listner != null){
                        listner.onFinish(true);
                    }
                }
                boolean finalResult = result;
                AsyncTask.execute(() -> {
                    pedidoDAO.updatePedidoSincronizado(pedidoWithPagamentos.pedido.order.getId(), finalResult);
                });
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                if(listner != null){
                    listner.onFinish(false);
                }
                AsyncTask.execute(() -> {
                    pedidoDAO.updatePedidoSincronizado(pedidoWithPagamentos.pedido.order.getId(), false);
                });
            }

        });
    }
}