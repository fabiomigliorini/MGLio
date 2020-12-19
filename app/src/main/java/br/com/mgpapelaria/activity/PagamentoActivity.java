package br.com.mgpapelaria.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;

import br.com.mgpapelaria.R;
import br.com.mgpapelaria.api.ApiService;
import br.com.mgpapelaria.api.RetrofitUtil;
import br.com.mgpapelaria.dao.PagamentoDAO;
import br.com.mgpapelaria.dao.PedidoDAO;
import br.com.mgpapelaria.database.AppDatabase;
import br.com.mgpapelaria.fragment.pagamento.CreditoFragment;
import br.com.mgpapelaria.fragment.pagamento.CreditoParceladoFragment;
import br.com.mgpapelaria.fragment.pagamento.FormaPagamentoFragment;
import br.com.mgpapelaria.fragment.pagamento.PagamentoBaseFragment;
import br.com.mgpapelaria.model.OrderRequest;
import br.com.mgpapelaria.model.Pagamento;
import br.com.mgpapelaria.model.Pedido;
import br.com.mgpapelaria.model.PedidoWithPagamentos;
import br.com.mgpapelaria.util.CieloSdkUtil;
import br.com.mgpapelaria.util.SharedPreferencesHelper;
import butterknife.ButterKnife;
import cielo.orders.domain.CheckoutRequest;
import cielo.orders.domain.Order;
import cielo.sdk.order.OrderManager;
import cielo.sdk.order.payment.PaymentCode;
import cielo.sdk.order.payment.PaymentError;
import cielo.sdk.order.payment.PaymentListener;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static br.com.mgpapelaria.util.CieloSdkUtil.getCredentials;

public class PagamentoActivity extends AppCompatActivity {
    public static final Integer PAGAMENTO_EFETUADO_RESULT = 1;
    public static final String ORDER = "order";
    public static final String VALOR_PAGO = "valor_pago";
    public final String TAG = "PAYMENT_LISTENER";
    private Order order = null;
    private Long valorPago;
    private OrderManager orderManager = null;
    private ApiService apiService;
    private PedidoDAO pedidoDAO;
    private PagamentoDAO pagamentoDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pagamento);
        ButterKnife.bind(this);

        getWindow().setStatusBarColor(Color.TRANSPARENT);

        String token = SharedPreferencesHelper.getToken(this);
        this.apiService = RetrofitUtil.createService(this, ApiService.class, token);
        AppDatabase db = AppDatabase.build(this);
        this.pedidoDAO = db.pedidoDAO();
        this.pagamentoDAO = db.pagamentoDAO();

        Bundle bundle = getIntent().getExtras();
        if(bundle == null || !bundle.containsKey(VALOR_PAGO)){
            throw new RuntimeException("Valor pago é obrigatório");
        }

        this.valorPago = bundle.getLong(VALOR_PAGO);
        if(bundle.containsKey(ORDER)){
            this.order = (Order) bundle.getSerializable(ORDER);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.configSDK(this::initFragment);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
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
                proximoFragment = new CreditoFragment(this.valorPago, this::onOptionClicked);
                break;
            case CreditoFragment.CREDITO_PARCELADO_OPTION:
                proximoFragment = new CreditoParceladoFragment();
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
        orderManager.placeOrder(this.order);

        PaymentListener paymentListener = new PaymentListener() {

            @Override
            public void onStart() {
            }

            @Override
            public void onPayment(@NonNull Order paidOrder) {
                order = paidOrder;
                order.markAsPaid();
                orderManager.updateOrder(order);
                AsyncTask.execute(() -> {
                    PedidoWithPagamentos pedidoWithPagamentos = persistOrder(order);
                    sendOrder(pedidoWithPagamentos);

                    setResult(PAGAMENTO_EFETUADO_RESULT);
                    finish();
                });

            }

            @Override
            public void onCancel() {
                order.markAsRejected();
                orderManager.updateOrder(order);
                finish();
            }

            @Override
            public void onError(@NonNull PaymentError paymentError) {
                FirebaseCrashlytics.getInstance().recordException(new Exception(new Gson().toJson(paymentError)));
                order.cancel();
                orderManager.updateOrder(order);
                finish();
            }

        };

        if(paymentCode != PaymentCode.VOUCHER_ALIMENTACAO){
            CheckoutRequest.Builder requestBuilder = new CheckoutRequest.Builder()
                    .orderId(this.order.getId())
                    .amount(this.valorPago)
                    .paymentCode(paymentCode);

            if(paymentCode == PaymentCode.CREDITO_PARCELADO_LOJA){
                requestBuilder = requestBuilder.installments((int)args);
            }

            CheckoutRequest request = requestBuilder.build();
            orderManager.checkoutOrder(request, paymentListener);
        }else{
            orderManager.checkoutOrder(order.getId(), this.valorPago, "3000", "4", paymentListener);
        }
    }

    private void replaceFragment(PagamentoBaseFragment fragment){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left, R.anim.slide_out_right, R.anim.slide_in_right);
        transaction.replace(R.id.fragments_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private PedidoWithPagamentos persistOrder(Order order){
        Pedido pedido = new Pedido();
        pedido.userId = SharedPreferencesHelper.getUserId(this);
        pedido.orderId = order.getId();

        String nome = order.getPayments().size() > 0 ? order.getPayments().get(0).getPaymentFields().get("clientName") : null;
        if(nome == null || nome.equals("")){
            nome = order.getReference();
        }

        pedido.nome = nome;
        pedido.data = order.getCreatedAt();
        pedido.valor = order.getPrice();
        pedido.status = order.getStatus().name();
        pedido.sincronizado = false;
        pedido.order = order;
        long pedidoId = this.pedidoDAO.insertPedido(pedido);

        Pagamento pagamento = new Pagamento();
        pagamento.pedidoId = (int)pedidoId;
        pagamento.paymentId = order.getPayments().get(0).getId();
        pagamento.userId = SharedPreferencesHelper.getUserId(this);
        pagamento.userName = SharedPreferencesHelper.getUser(this);

        this.pagamentoDAO.insertPagamento(pagamento);

        return this.pedidoDAO.getWithPagamentosById((int) pedidoId);
    }

    private void updateSincronizadoStatus(String orderId){
        AsyncTask.execute(() -> {
            this.pedidoDAO.updatePedidoSincronizado(orderId, true);
        });
    }

    private void sendOrder(PedidoWithPagamentos pedidoWithPagamentos){
        String url = SharedPreferencesHelper.getBaseUrlUpdateOrder(this);
        this.apiService.updateOrder(url, new OrderRequest(pedidoWithPagamentos)).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.code() == 200){
                    updateSincronizadoStatus(pedidoWithPagamentos.pedido.order.getId());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                FirebaseCrashlytics.getInstance().recordException(t);
            }

        });
    }

    protected void configSDK(CieloSdkUtil.SdkListener listener) {
        orderManager = new OrderManager(getCredentials(), this);
        orderManager.bind(this, new CieloSdkUtil.BindListener(listener));
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