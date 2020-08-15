package br.com.mgpapelaria.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitUtil {
    public static Retrofit build(){
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .create();

        return new Retrofit.Builder()
                .baseUrl("http://api.mgspa.mgpapelaria.com.br/api/v1/lio/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }
}
