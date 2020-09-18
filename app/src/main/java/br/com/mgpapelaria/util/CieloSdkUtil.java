package br.com.mgpapelaria.util;

import com.google.firebase.crashlytics.FirebaseCrashlytics;

import cielo.orders.domain.Credentials;
import cielo.sdk.order.ServiceBindListener;

public class CieloSdkUtil {
    public static final String ACCESS_KEY = "3bBCIdoFCNMUCJHFPZIQtuVAFQzb16O11O3twEnzz9MT5Huhng/ rRKDEcIfdA7AMcGSzStRAyHSCx44yEHsRVmLTeYMQfBEFFpcgm";
    public static final String SECRET_KEY = "iIm9ujCG8IkvWOaTSFT3diNSEhNkjr0ttRf7hDnwEDMoO3u3S0";

    public static Credentials getCredentials(){
        return new Credentials(ACCESS_KEY, SECRET_KEY);
    }

    public interface SdkListener{
        void onServiceBound();
    }

    public static class BindListener implements ServiceBindListener{
        private SdkListener listener;

        public BindListener(SdkListener listener) {
            this.listener = listener;
        }

        @Override
        public void onServiceBound() {
            if(listener != null){
                listener.onServiceBound();
            }
        }

        @Override
        public void onServiceBoundError(Throwable throwable) {
            FirebaseCrashlytics.getInstance().recordException(throwable);
        }

        @Override
        public void onServiceUnbound() {

        }
    }
}
