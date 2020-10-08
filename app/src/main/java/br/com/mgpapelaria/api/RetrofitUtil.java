package br.com.mgpapelaria.api;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import br.com.mgpapelaria.util.SharedPreferencesHelper;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitUtil {
    private static final OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
    private static final Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .create();

    private static Retrofit.Builder getBuilder(Context context){
        final String baseUrl = SharedPreferencesHelper.getBaseUrlDefault(context);
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create(gson));
    }

    public static <S> S createService(Context context, Class<S> serviceClass) {
        return createService(context, serviceClass, null);
    }

    public static <S> S createService(Context context, Class<S> serviceClass, final String authToken) {
        Retrofit.Builder builder = getBuilder(context);
        Retrofit retrofit = builder.build();

        if (!TextUtils.isEmpty(authToken)) {
            AuthenticationInterceptor interceptor =
                    new AuthenticationInterceptor(authToken);

            if (!httpClient.interceptors().contains(interceptor)) {
                httpClient.addInterceptor(interceptor);
                httpClient.authenticator(new TokenAuthenticator(context, authToken));

                builder.client(httpClient.build());
                retrofit = builder.build();
            }
        }

        return retrofit.create(serviceClass);
    }
}
