package br.com.mgpapelaria.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesHelper {
    public static final String NAME = "MG_Pref";
    public static final String TOKEN = "token";
    public static final String USER = "user";
    public static final String USER_ID = "userId";
    public static final String BASE_URL_DEFAULT = "baseUrlDefault";
    public static final String BASE_URL_PRODUCAO = "baseUrlProducao";
    public static final String BASE_URL_DESENVOLVIMENTO = "baseUrlDesenvolvimento";
    public static final String BASE_URL_LIST_VENDAS_ABERTAS = "baseUrlListVendasAbertas";
    public static final String BASE_URL_UPDATE_ORDER = "baseUrlUpdateOrder";
    private static SharedPreferences sharedPreferences;

    public static SharedPreferences getSharedPreferences(Context context){
        if(sharedPreferences != null){
            return sharedPreferences;
        }
        sharedPreferences = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        return sharedPreferences;
    }

    public static void clear(Context context){
        getSharedPreferences(context).edit().clear().apply();
    }

    public static String getToken(Context context){
        return getSharedPreferences(context).getString(TOKEN, null);
    }

    public static String getBaseUrlDefault(Context context){
        return getSharedPreferences(context).getString(BASE_URL_DEFAULT, "http://api.mgspa.mgpapelaria.com.br/api/v1/");
    }

    public static String getBaseUrlListVendasAbertas(Context context){
        return getSharedPreferences(context).getString(BASE_URL_LIST_VENDAS_ABERTAS, "http://api.mgspa.mgpapelaria.com.br/api/v1/lio/vendas-abertas");
    }

    public static void setBaseUrlListVendasAbertas(Context context, String value){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(BASE_URL_LIST_VENDAS_ABERTAS, value);
        editor.commit();
    }

    public static String getBaseUrlUpdateOrder(Context context){
        return getSharedPreferences(context).getString(BASE_URL_UPDATE_ORDER, "http://api.mgspa.mgpapelaria.com.br/api/v1/lio/order");
    }

    public static void setBaseUrlUpdateOrder(Context context, String value){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(BASE_URL_UPDATE_ORDER, value);
        editor.commit();
    }

    public static void setBaseUrlDefault(Context context, String value){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(BASE_URL_DEFAULT, value);
        editor.commit();
    }

    public static String getBaseUrl(Context context, boolean producao){
        if(producao){
            return getSharedPreferences(context).getString(BASE_URL_PRODUCAO, "http://api.mgspa.mgpapelaria.com.br/api/v1/");
        }else{
            return getSharedPreferences(context).getString(BASE_URL_DESENVOLVIMENTO, "http://192.168.1.198:91/api/v1/");
        }
    }

    public static void setBaseUrlProducao(Context context, String value){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(BASE_URL_PRODUCAO, value);
        editor.commit();
    }

    public static void setBaseUrlDesenvolvimento(Context context, String value){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(BASE_URL_DESENVOLVIMENTO, value);
        editor.commit();
    }

    public static void setToken(Context context, String value){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(TOKEN, value);
        editor.apply();
    }

    public static String getUser(Context context){
        return getSharedPreferences(context).getString(USER, null);
    }

    public static Integer getUserId(Context context){
        return getSharedPreferences(context).getInt(USER_ID, -1);
    }

    public static void setUser(Context context, String user, Integer userId){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(USER, user);
        editor.putInt(USER_ID, userId);
        editor.apply();
    }
}
