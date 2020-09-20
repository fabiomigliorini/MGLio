package br.com.mgpapelaria.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesHelper {
    public static final String NAME = "MG_Pref";
    public static final String TOKEN = "token";
    public static final String USER = "user";
    public static final String USER_ID = "userId";
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
