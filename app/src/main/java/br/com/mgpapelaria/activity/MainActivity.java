package br.com.mgpapelaria.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.analytics.FirebaseAnalytics;

import br.com.mgpapelaria.R;
import br.com.mgpapelaria.api.ApiService;
import br.com.mgpapelaria.api.RetrofitUtil;
import br.com.mgpapelaria.util.SharedPreferencesHelper;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cielo.sdk.info.InfoManager;
import cielo.sdk.order.OrderManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private ApiService apiService;

    @BindView(R.id.usuario_text_view)
    TextView usuarioTextView;
    @BindView(R.id.numero_logico_text_view)
    TextView numeroLogicoTextView;
    private FirebaseAnalytics mFirebaseAnalytics;
    private OrderManager orderManager = null;
    private static boolean orderManagerServiceBinded = false;
    /*@BindView(R.id.payments_types_text_view)
    TextView paymetsTypesTextView;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        String token = SharedPreferencesHelper.getToken(this);

        if(token == null){
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        this.apiService = RetrofitUtil.createService(this, ApiService.class, token);

        String usuario = SharedPreferencesHelper.getUser(this);
        String numeroLogico = new InfoManager().getSettings(this).getLogicNumber();
        this.usuarioTextView.setText(usuario);
        this.numeroLogicoTextView.setText(numeroLogico);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
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
                SharedPreferencesHelper.clear(MainActivity.this);

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

}