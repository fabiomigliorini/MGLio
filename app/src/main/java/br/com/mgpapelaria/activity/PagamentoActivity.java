package br.com.mgpapelaria.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import br.com.mgpapelaria.R;
import br.com.mgpapelaria.api.ApiService;
import br.com.mgpapelaria.api.RetrofitUtil;
import br.com.mgpapelaria.database.AppDatabase;
import br.com.mgpapelaria.fragment.pagamento.CreditoFragment;
import br.com.mgpapelaria.fragment.pagamento.FormaPagamentoFragment;
import br.com.mgpapelaria.fragment.pagamento.PagamentoBaseFragment;
import br.com.mgpapelaria.model.OrderRequest;
import br.com.mgpapelaria.model.Pedido;
import butterknife.ButterKnife;
import cielo.orders.domain.CheckoutRequest;
import cielo.orders.domain.Credentials;
import cielo.orders.domain.Order;
import cielo.sdk.order.OrderManager;
import cielo.sdk.order.ServiceBindListener;
import cielo.sdk.order.payment.PaymentCode;
import cielo.sdk.order.payment.PaymentError;
import cielo.sdk.order.payment.PaymentListener;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PagamentoActivity extends AppCompatActivity {
    public static final Integer PAGAMENTO_EFETUADO_RESULT = 1;
    public static final String ORDER = "order";
    public static final String VALOR_PAGO = "valor_pago";
    public final String TAG = "PAYMENT_LISTENER";
    private Order order = null;
    private Long valorPago;
    private OrderManager orderManager = null;
    private static boolean orderManagerServiceBinded = false;
    private ApiService apiService;
    private AppDatabase db;
    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pagamento);
        ButterKnife.bind(this);

        this.apiService = RetrofitUtil.createService(this, ApiService.class);
        this.db = AppDatabase.build(this);

        Bundle bundle = getIntent().getExtras();
        if(bundle == null || !bundle.containsKey(VALOR_PAGO)){
            throw new RuntimeException("Valor pago é obrigatório");
        }

        this.valorPago = bundle.getLong(VALOR_PAGO);
        if(bundle.containsKey(ORDER)){
            this.order = (Order) bundle.getSerializable(ORDER);
        }

        sharedPref = getSharedPreferences("MG_Pref", Context.MODE_PRIVATE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        //toolbar.setNavigationIcon(R.drawable.ic_baseline_close_24);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.configSDK();

        /*this.getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                cancelaOperacao();
            }
        });*/

        this.initFragment();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //cancelaOperacao();
                //finish();
                hideKeyboard(this);
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        orderManager.unbind();
        super.onDestroy();
    }

    private void initFragment(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        FormaPagamentoFragment fragment = new FormaPagamentoFragment();
        fragment.setOptionListener(this::onOptionClicked);
        fragmentTransaction.add(R.id.fragments_container, fragment);
        fragmentTransaction.commit();
    }

    private void onOptionClicked(String option){
        PagamentoBaseFragment proximoFragment;
        switch (option){
            case FormaPagamentoFragment.CREDITO_OPTION:
                proximoFragment = new CreditoFragment();
                break;
            case FormaPagamentoFragment.DEBITO_OPTION:
                proximoFragment = null;
                defineFormaDePagamento(PaymentCode.DEBITO_AVISTA, null);
                break;
            case FormaPagamentoFragment.VALE_CULTURA_OPTION:
                proximoFragment = null;
                defineFormaDePagamento(PaymentCode.VOUCHER_ALIMENTACAO, null);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + option);
        }

        if(proximoFragment != null){
            proximoFragment.setFormaPagamentoListener(this::defineFormaDePagamento);
            this.replaceFragment(proximoFragment);
        }
    }

    private void defineFormaDePagamento(PaymentCode paymentCode, Object args){
        //OrderManager orderManager = OrderManagerSingleton.getInstance();

        orderManager.placeOrder(this.order);
        /*String ec = merchantCode.getText().toString();
        String userEmail = email.getText().toString();*/
        int parcelas = 0;



        /*if (!ec.equals(""))
            requestBuilder.ec(ec);

        if (!userEmail.equals(""))
            requestBuilder.email(userEmail);*/

        CheckoutRequest request = new CheckoutRequest.Builder()
                .orderId(this.order.getId())
                .amount(this.valorPago)
                .paymentCode(paymentCode)
                .installments(parcelas).build();

        orderManager.checkoutOrder(request, new PaymentListener() {

            @Override
            public void onStart() {
                Log.d(TAG, "ON START");
            }

            @Override
            public void onPayment(@NonNull Order paidOrder) {
                Toast.makeText(getApplicationContext(), "onPayment", Toast.LENGTH_SHORT).show();
                order = paidOrder;
                order.markAsPaid();
                orderManager.updateOrder(order);
                persistOrder(order);
                sendOrder(order);

                setResult(PAGAMENTO_EFETUADO_RESULT);
                finish();
            }

            @Override
            public void onCancel() {
                //Toast.makeText(getApplicationContext(), "Cancelado", Toast.LENGTH_SHORT).show();
                order.markAsRejected();
                orderManager.updateOrder(order);
                finish();
            }

            @Override
            public void onError(@NonNull PaymentError paymentError) {
                //Toast.makeText(getApplicationContext(), "Erro", Toast.LENGTH_SHORT).show();
                order.cancel();
                orderManager.updateOrder(order);
                finish();
            }

        });
    }

    private void replaceFragment(PagamentoBaseFragment fragment){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left, R.anim.slide_out_right, R.anim.slide_in_right);
        transaction.replace(R.id.fragments_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void cancelaOperacao(){
        Intent intent = new Intent(this, OperacaoCanceladaActivity.class);
        startActivity(intent);
        finish();
    }

    private void persistOrder(Order order){
        AsyncTask.execute(() -> {
            Pedido pedido = new Pedido();
            pedido.userId = sharedPref.getInt("userId", -1);
            pedido.orderId = order.getId();
            pedido.nome = order.getPayments().get(0).getPaymentFields().get("clientName");
            pedido.data = order.getCreatedAt();
            pedido.valor = order.getPrice();
            pedido.status = order.getStatus().name();
            pedido.sincronizado = false;
            pedido.order = order;
            this.db.pedidoDAO().insertPedido(pedido);
        });
    }

    private void updateSincronizadoStatus(String orderId){
        AsyncTask.execute(() -> {
            this.db.pedidoDAO().updatePedidoSincronizado(orderId, true);
        });
    }

    private void sendOrder(Order order){
        this.apiService.updateOrder(new OrderRequest(order)).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.code() == 200){
                    updateSincronizadoStatus(order.getId());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
            }

        });
    }

    protected void configSDK() {
        Credentials credentials = new Credentials( "3bBCIdoFCNMUCJHFPZIQtuVAFQzb16O11O3twEnzz9MT5Huhng/ rRKDEcIfdA7AMcGSzStRAyHSCx44yEHsRVmLTeYMQfBEFFpcgm", "iIm9ujCG8IkvWOaTSFT3diNSEhNkjr0ttRf7hDnwEDMoO3u3S0");
        orderManager = new OrderManager(credentials, this);
        orderManager.bind(this, new ServiceBindListener() {

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
            }

            @Override
            public void onServiceUnbound() {
                orderManagerServiceBinded = false;
            }
        });
    }

    public static void hideKeyboard(Context context) {
        try {
            ((Activity) context).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            if ((((Activity) context).getCurrentFocus() != null) && (((Activity) context).getCurrentFocus().getWindowToken() != null)) {
                ((InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(((Activity) context).getCurrentFocus().getWindowToken(), 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}