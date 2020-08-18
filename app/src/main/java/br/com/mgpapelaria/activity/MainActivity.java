package br.com.mgpapelaria.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import br.com.mgpapelaria.R;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        SharedPreferences sharedPref = getSharedPreferences("MG_Pref", Context.MODE_PRIVATE);
        String token = sharedPref.getString("token", null);

        if(token == null){
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @OnClick(R.id.venda_aberta_button)
    void onVendaAvulsaClicked(){
        Intent intent = new Intent(this, ListaVendasAbertasActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.valor_avulso_button)
    void onValorAvusoButtonClicked(){
        Intent intent = new Intent(this, PinpadActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.transacoes_button)
    void onVendasButtonClicked(){
        Intent intent = new Intent(this, ListaTransacoesActivity.class);
        startActivity(intent);
    }


    /*@OnClick(R.id.teste_cores_button)
    void onTesteCoresButtonClicked(){
        Intent intent = new Intent(this, TesteCoresActivity.class);
        startActivity(intent);
    }*/

}