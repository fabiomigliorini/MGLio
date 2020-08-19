package br.com.mgpapelaria.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import br.com.mgpapelaria.R;
import br.com.mgpapelaria.api.ApiService;
import br.com.mgpapelaria.api.RetrofitUtil;
import br.com.mgpapelaria.model.Filial;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private ApiService apiService;

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

        this.apiService = RetrofitUtil.createService(this, ApiService.class, token);
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

    @OnClick(R.id.teste_requisicao_button)
    void onTesteRequisicaoButtonClicked(){
        this.apiService.selectFilial().enqueue(new Callback<List<Filial>>() {
            @Override
            public void onResponse(Call<List<Filial>> call, Response<List<Filial>> response) {
                if(response.code() == 200){
                    for(Filial filial : response.body()){
                        Log.i("FILIAL", filial.getLabel());
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Filial>> call, Throwable t) {

            }
        });
    }

    /*@OnClick(R.id.teste_cores_button)
    void onTesteCoresButtonClicked(){
        Intent intent = new Intent(this, TesteCoresActivity.class);
        startActivity(intent);
    }*/

}