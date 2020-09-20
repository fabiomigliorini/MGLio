package br.com.mgpapelaria.api;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.IOException;

import br.com.mgpapelaria.activity.LoginActivity;
import br.com.mgpapelaria.util.SharedPreferencesHelper;
import okhttp3.Authenticator;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.Route;

public class TokenAuthenticator implements Authenticator {
    //private ApiService apiService;
    private Context context;
    private String token;

    public TokenAuthenticator(Context context, String token) {
        this.context = context;
        this.token = token;
        //String token = this.sharedPref.getString("token", null);
        //this.apiService = RetrofitUtil.createService(context, ApiService.class, token);
    }

    @Override
    public Request authenticate(Route route, @NonNull Response response) throws IOException {
        ApiService apiService = RetrofitUtil.createService(this.context, ApiService.class, this.token);
        retrofit2.Response<ResponseBody> refreshResponse = apiService.refreshToken().execute();
        if(refreshResponse.code() == 200){
            String newToken = refreshResponse.body().string();
            SharedPreferencesHelper.setToken(this.context, newToken);
            this.token = newToken;

            return response.request().newBuilder().header("Authorization", "Bearer " + newToken).build();
        }else{
            Log.e("TokenAuthenticator", "Ir para tela de login");
            SharedPreferencesHelper.setToken(this.context, null);
            this.token = null;

            Intent intent = new Intent(context, LoginActivity.class);
            context.startActivity(intent);

            return null;
        }
    }
}
