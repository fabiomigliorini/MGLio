package br.com.mgpapelaria.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import cielo.orders.aidl.ParcelableOrder;
import cielo.orders.aidl.ParcelableTransaction;

import static br.com.mgpapelaria.util.CieloSdkUtil.ACCESS_KEY;
import static br.com.mgpapelaria.util.CieloSdkUtil.SECRET_KEY;

public class LIOCancelationBroadcastReceiver extends BroadcastReceiver {
    String INTENT_ORDER_KEY = "ORDER";
    String INTENT_TRANSACTION_KEY = "TRANSACTION";

    @Override
    public void onReceive(Context context, Intent intent) {
        ParcelableOrder order = intent.getExtras().getParcelable(INTENT_ORDER_KEY);
        ParcelableTransaction transaction = intent.getExtras().getParcelable(INTENT_TRANSACTION_KEY);

        if (ACCESS_KEY.equalsIgnoreCase(order.getAccessKey())
                && SECRET_KEY.equalsIgnoreCase(order.getSecretAccessKey())) {
            //ParcelableTransaction transaction = intent.getExtras().getParcelable(INTENT_TRANSACTION_KEY);
            Log.d("CANCELAMENTO", "A ordem pertence ao meu aplicativo");
        }else {
            Log.d("CANCELAMENTO", "A ordem não pertence ao meu aplicativo. Ignorar");
        }
    }
}