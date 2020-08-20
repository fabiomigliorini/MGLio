package br.com.mgpapelaria.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import br.com.mgpapelaria.R;
import br.com.mgpapelaria.api.ApiService;
import br.com.mgpapelaria.api.RetrofitUtil;
import br.com.mgpapelaria.model.Filial;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cielo.sdk.info.InfoManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private ApiService apiService;
    private SharedPreferences sharedPref;

    @BindView(R.id.usuario_text_view)
    TextView usuarioTextView;
    @BindView(R.id.numero_logico_text_view)
    TextView numeroLogicoTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        sharedPref = getSharedPreferences("MG_Pref", Context.MODE_PRIVATE);
        String token = sharedPref.getString("token", null);

        if(token == null){
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        this.apiService = RetrofitUtil.createService(this, ApiService.class, token);

        String usuario = sharedPref.getString("user", null);
        String numeroLogico = new InfoManager().getSettings(this).getLogicNumber();
        this.usuarioTextView.setText(usuario);
        this.numeroLogicoTextView.setText(numeroLogico);
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

    @OnClick(R.id.sair_button)
    void onSairButtonClicked(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Realmente deseja sair?");
        builder.setPositiveButton("Sim", (dialog, which) -> {
            dialog.dismiss();
            logout();
        });
        builder.setNegativeButton("Cancelar", (dialog, which) -> {
            dialog.dismiss();
        });
        builder.create().show();
    }

    private void logout(){
        this.apiService.logout().enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                ProgressDialog mDialog = new ProgressDialog(MainActivity.this);
                mDialog.setMessage("Aguarde...");
                mDialog.setCancelable(false);
                mDialog.show();

                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("token", null);
                editor.apply();

                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });
    }

    /*@OnClick(R.id.teste_requisicao_button)
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

    @OnClick(R.id.teste_cores_button)
    void onTesteCoresButtonClicked(){
        Intent intent = new Intent(this, TesteCoresActivity.class);
        startActivity(intent);
    }*/

}