package br.com.mgpapelaria.util;

import android.widget.Toast;

import br.com.mgpapelaria.BaseApplication;
import cielo.orders.domain.Credentials;
import cielo.sdk.order.OrderManager;
import cielo.sdk.order.ServiceBindListener;

public class OrderManagerSingleton {
    private static OrderManager instance;
    private static boolean orderManagerServiceBinded = false;

    private OrderManagerSingleton(){

    }

    public static OrderManager getInstance(){
        if(instance == null){
            Credentials credentials = new Credentials( "3bBCIdoFCNMUCJHFPZIQtuVAFQzb16O11O3twEnzz9MT5Huhng/ rRKDEcIfdA7AMcGSzStRAyHSCx44yEHsRVmLTeYMQfBEFFpcgm", "iIm9ujCG8IkvWOaTSFT3diNSEhNkjr0ttRf7hDnwEDMoO3u3S0");
            instance = new OrderManager(credentials, BaseApplication.getContext());
            instance.bind(BaseApplication.getContext(), new ServiceBindListener() {

                @Override
                public void onServiceBoundError(Throwable throwable) {
                    orderManagerServiceBinded = false;

                    Toast.makeText(BaseApplication.getContext(),
                            String.format("Erro fazendo bind do serviço de ordem -> %s",
                                    throwable.getMessage()), Toast.LENGTH_LONG).show();
                }

                @Override
                public void onServiceBound() {
                    orderManagerServiceBinded = true;
                    //orderManager.createDraftOrder("REFERENCIA DA ORDEM");
                }

                @Override
                public void onServiceUnbound() {
                    orderManagerServiceBinded = false;
                }
            });
        }
        return instance;
    }
}
