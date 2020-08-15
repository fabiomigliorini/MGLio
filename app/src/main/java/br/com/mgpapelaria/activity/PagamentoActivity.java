package br.com.mgpapelaria.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import br.com.mgpapelaria.fragment.pagamento.CrediarioFragment;
import br.com.mgpapelaria.fragment.pagamento.CreditoFragment;
import br.com.mgpapelaria.fragment.pagamento.DebitoFragment;
import br.com.mgpapelaria.fragment.pagamento.FormaPagamentoFragment;
import br.com.mgpapelaria.fragment.pagamento.PagamentoBaseFragment;
import br.com.mgpapelaria.fragment.pagamento.VoucherFragment;
import butterknife.ButterKnife;
import cielo.orders.domain.CheckoutRequest;
import cielo.orders.domain.Credentials;
import cielo.orders.domain.Order;
import cielo.sdk.order.OrderManager;
import cielo.sdk.order.ServiceBindListener;
import cielo.sdk.order.payment.PaymentCode;
import cielo.sdk.order.payment.PaymentError;
import cielo.sdk.order.payment.PaymentListener;

public class PagamentoActivity extends AppCompatActivity {
    public static final Integer PAGAMENTO_EFETUADO_RESULT = 1;
    public static final String ORDER = "order";
    public static final String VALOR_PAGO = "valor_pago";
    public final String TAG = "PAYMENT_LISTENER";
    private Order order = null;
    private Long valorPago;
    private OrderManager orderManager = null;
    private static boolean orderManagerServiceBinded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pagamento);
        ButterKnife.bind(this);

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
        /*TypedArray a = getTheme().obtainStyledAttributes(R.style.AppTheme, new int[] {R.attr.homeAsUpIndicator});
        int attributeResourceId = a.getResourceId(0, 0);
        Drawable icon = ContextCompat.getDrawable(this, attributeResourceId);
        a.recycle();
        getSupportActionBar().setHomeAsUpIndicator(icon);*/
        PagamentoBaseFragment proximoFragment;
        switch (option){
            case FormaPagamentoFragment.CREDITO_OPTION:
                proximoFragment = new CreditoFragment();
                break;
            case FormaPagamentoFragment.DEBITO_OPTION:
                proximoFragment = new DebitoFragment();
                break;
            case FormaPagamentoFragment.CREDIARIO_OPTION:
                proximoFragment = new CrediarioFragment();
                break;
            case FormaPagamentoFragment.VOUCHER_OPTION:
                proximoFragment = new VoucherFragment();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + option);
        }

        proximoFragment.setFormaPagamentoListener(this::defineFormaDePagamento);
        this.replaceFragment(proximoFragment);
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
                order = paidOrder;
                order.markAsPaid();
                orderManager.updateOrder(order);

                setResult(PAGAMENTO_EFETUADO_RESULT);
                finish();
            }

            @Override
            public void onCancel() {
                finish();
            }

            @Override
            public void onError(@NonNull PaymentError paymentError) {
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
                orderManager.createDraftOrder("REFERENCIA DA ORDEM");
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