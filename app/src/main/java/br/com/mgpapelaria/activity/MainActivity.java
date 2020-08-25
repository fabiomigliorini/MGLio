package br.com.mgpapelaria.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

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
import cielo.orders.domain.Credentials;
import cielo.orders.domain.product.PrimaryProduct;
import cielo.orders.domain.product.SecondaryProduct;
import cielo.sdk.info.InfoManager;
import cielo.sdk.order.OrderManager;
import cielo.sdk.order.ServiceBindListener;
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
    private OrderManager orderManager = null;
    private static boolean orderManagerServiceBinded = false;
    /*@BindView(R.id.payments_types_text_view)
    TextView paymetsTypesTextView;*/

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

        //this.configSDK();
    }

    /*protected void configSDK() {
        Credentials credentials = new Credentials( "3bBCIdoFCNMUCJHFPZIQtuVAFQzb16O11O3twEnzz9MT5Huhng/ rRKDEcIfdA7AMcGSzStRAyHSCx44yEHsRVmLTeYMQfBEFFpcgm", "iIm9ujCG8IkvWOaTSFT3diNSEhNkjr0ttRf7hDnwEDMoO3u3S0");
        this.orderManager = new OrderManager(credentials, this);
        this.orderManager.bind(this, new ServiceBindListener() {

            @Override
            public void onServiceBoundError(Throwable throwable) {
                orderManagerServiceBinded = false;

                Toast.makeText(getApplicationContext(),
                        String.format("Erro fazendo bind do serviço de ordem -> %s",
                                throwable.getMessage()), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onServiceBound() {
                orderManagerServiceBinded = true;

                StringBuilder sb = new StringBuilder();
                for(PrimaryProduct pp : orderManager.retrievePaymentType(getApplicationContext())){
                    sb.append(pp.getId()).append(": ").append(pp.getName()).append(" (").append(pp.getCode()).append(")\n");
                    for(SecondaryProduct sp : pp.getSecondaryProducts()){
                        sb.append("- ").append(sp.getId()).append(": ").append(sp.getName()).append(" (").append(sp.getCode()).append(")\n");
                    }
                }
                paymetsTypesTextView.setText(sb.toString());
            }

            @Override
            public void onServiceUnbound() {
                orderManagerServiceBinded = false;
            }
        });
    }*/

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
            logout();
            dialog.dismiss();
        });
        builder.setNegativeButton("Cancelar", (dialog, which) -> {
            dialog.dismiss();
        });
        builder.create().show();
    }

    private void logout(){
        ProgressDialog mDialog = new ProgressDialog(MainActivity.this);
        mDialog.setMessage("Aguarde...");
        mDialog.setCancelable(false);
        mDialog.show();

        this.apiService.logout().enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.clear().apply();

                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                mDialog.dismiss();
                finish();
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