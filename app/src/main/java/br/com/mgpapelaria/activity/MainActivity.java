package br.com.mgpapelaria.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.material.switchmaterial.SwitchMaterial;

import br.com.mgpapelaria.R;
import br.com.mgpapelaria.util.OrderManagerSingleton;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import cielo.orders.domain.CheckoutRequest;
import cielo.orders.domain.Credentials;
import cielo.orders.domain.Order;
import cielo.sdk.order.OrderManager;
import cielo.sdk.order.ServiceBindListener;
import cielo.sdk.order.payment.PaymentError;
import cielo.sdk.order.payment.PaymentListener;

public class MainActivity extends AppCompatActivity {
    private OrderManager orderManager = null;
    private static boolean orderManagerServiceBinded = false;
    public final String TAG = "PAYMENT_LISTENER";
    private Order order = null;
    //private SharedPreferences sharedPreferences;
    //@BindView(R.id.pagamento_nativo_switch)
    //SwitchMaterial pagamentoNativoSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        /*sharedPreferences = getSharedPreferences(getString(R.string.pagamento_nativo_pref_key), Context.MODE_PRIVATE);

        pagamentoNativoSwitch.setChecked(sharedPreferences.getBoolean(getString(R.string.pagamento_nativo_pref_key), false));
        pagamentoNativoSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Log.i("MG", isChecked ? "Sim" : "Não");
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(getString(R.string.pagamento_nativo_pref_key), isChecked);
            editor.apply();
        });*/
        //getSupportActionBar().hide();
        //OrderManagerSingleton.getInstance();
        this.configSDK();
    }

    @OnClick(R.id.venda_registrada_button)
    void onVendaAvulsaClicked(){
        Intent intent = new Intent(this, ListaVendasRegistradasActivity.class);
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

    @OnClick(R.id.teste_pinpad_button)
    void onTestePinpadButtonClicked(){
        order = orderManager.createDraftOrder("Pedido");
        order.addItem(
                "sku", "Produtos de papelaria",
                5,
                1, "QTD");

        orderManager.updateOrder(order);

        orderManager.placeOrder(order);

        CheckoutRequest request = new CheckoutRequest.Builder()
                .orderId(order.getId())  //Obrigatório
                .build();

        orderManager.checkoutOrder(request, new PaymentListener() {

            @Override
            public void onStart() {
                Log.d(TAG, "ON START");
            }

            @Override
            public void onPayment(@NonNull Order paidOrder) {
                order = paidOrder;
                order.markAsPaid();
                orderManager.updateOrder(order);

                Toast.makeText(getApplicationContext(), "Pagamento efetuado com sucesso", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancel() {
                Toast.makeText(getApplicationContext(), "Pagamento cancelado", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(@NonNull PaymentError paymentError) {
                Toast.makeText(getApplicationContext(), "Erro ao efetuar o pagamento", Toast.LENGTH_LONG).show();
            }

        });
    }

    public void configSDK() {
        Credentials credentials = new Credentials( "3bBCIdoFCNMUCJHFPZIQtuVAFQzb16O11O3twEnzz9MT5Huhng/ rRKDEcIfdA7AMcGSzStRAyHSCx44yEHsRVmLTeYMQfBEFFpcgm", "iIm9ujCG8IkvWOaTSFT3diNSEhNkjr0ttRf7hDnwEDMoO3u3S0");
        orderManager = new OrderManager(credentials, this);
        orderManager.bind(this, new ServiceBindListener() {
            @Override
            public void onServiceBoundError(Throwable throwable) {
                Toast.makeText(getApplicationContext(),
                        String.format("Erro fazendo bind do serviço de ordem -> %s",
                                throwable.getMessage()), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onServiceBound() {
            }

            @Override
            public void onServiceUnbound() {
            }
        });
    }
}