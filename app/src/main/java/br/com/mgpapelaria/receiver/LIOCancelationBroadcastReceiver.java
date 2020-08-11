package br.com.mgpapelaria.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import cielo.orders.aidl.ParcelableOrder;
import cielo.orders.aidl.ParcelableTransaction;

public class LIOCancelationBroadcastReceiver extends BroadcastReceiver {

    String MY_CLIENT_ID = "3bBCIdoFCNMUCJHFPZIQtuVAFQzb16O11O3twEnzz9MT5Huhng/ rRKDEcIfdA7AMcGSzStRAyHSCx44yEHsRVmLTeYMQfBEFFpcgm";
    String MY_ACCESS_KEY = "iIm9ujCG8IkvWOaTSFT3diNSEhNkjr0ttRf7hDnwEDMoO3u3S0";

    String INTENT_ORDER_KEY = "ORDER";
    String INTENT_TRANSACTION_KEY = "TRANSACTION";

    @Override
    public void onReceive(Context context, Intent intent) {
        ParcelableOrder order = intent.getExtras().getParcelable(INTENT_ORDER_KEY);
        ParcelableTransaction transaction = intent.getExtras().getParcelable(INTENT_TRANSACTION_KEY);

        if (MY_ACCESS_KEY.equalsIgnoreCase(order.getAccessKey())
                && MY_CLIENT_ID.equalsIgnoreCase(order.getSecretAccessKey())) {
            //ParcelableTransaction transaction = intent.getExtras().getParcelable(INTENT_TRANSACTION_KEY);
            Log.d("CANCELAMENTO", "A ordem pertence ao meu aplicativo");
        }else {
            Log.d("CANCELAMENTO", "A ordem não pertence ao meu aplicativo. Ignorar");
        }
    }
}