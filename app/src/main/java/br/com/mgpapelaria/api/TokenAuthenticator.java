package br.com.mgpapelaria.api;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.IOException;

import br.com.mgpapelaria.activity.LoginActivity;
import okhttp3.Authenticator;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

public class TokenAuthenticator implements Authenticator {
    //private ApiService apiService;
    private Context context;
    private SharedPreferences sharedPref;
    private String token;

    public TokenAuthenticator(Context context, String token) {
        this.context = context;
        this.sharedPref = context.getSharedPreferences("MG_Pref", Context.MODE_PRIVATE);
        this.token = token;
        //String token = this.sharedPref.getString("token", null);
        //this.apiService = RetrofitUtil.createService(context, ApiService.class, token);
    }

    @Override
    public Request authenticate(Route route, @NonNull Response response) throws IOException {
        ApiService apiService = RetrofitUtil.createService(this.context, ApiService.class, this.token);
        retrofit2.Response<String> refreshResponse = apiService.refreshToken().execute();
        if(refreshResponse.code() == 200){
            String newToken = refreshResponse.body();
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("token", newToken);
            editor.apply();
            this.token = newToken;

            return response.request().newBuilder().header("Authorization", "Bearer " + newToken).build();
        }else{
            Log.e("TokenAuthenticator", "Ir para tela de login");
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("token", null);
            editor.apply();
            this.token = null;

            Intent intent = new Intent(context, LoginActivity.class);
            context.startActivity(intent);

            return null;
        }
    }
}
