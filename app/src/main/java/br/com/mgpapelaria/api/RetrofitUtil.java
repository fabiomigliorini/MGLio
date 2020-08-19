package br.com.mgpapelaria.api;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitUtil {
    public static final String API_BASE_URL = "http://api.mgspa.mgpapelaria.com.br/api/v1/";
    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
    private static Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .create();

    private static Retrofit.Builder builder =
            new Retrofit.Builder()
                    .baseUrl(API_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson));

    private static Retrofit retrofit = builder.build();


    public static <S> S createService(Context context, Class<S> serviceClass) {
        return createService(context, serviceClass, null);
    }

    public static <S> S createService(Context context,
            Class<S> serviceClass, final String authToken) {
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
