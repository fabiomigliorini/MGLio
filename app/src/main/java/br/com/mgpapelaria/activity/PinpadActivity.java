package br.com.mgpapelaria.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import br.com.mgpapelaria.R;
import br.com.mgpapelaria.util.OrderManagerSingleton;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import cielo.orders.domain.CheckoutRequest;
import cielo.orders.domain.Credentials;
import cielo.orders.domain.Order;
import cielo.sdk.info.InfoManager;
import cielo.sdk.order.OrderManager;
import cielo.sdk.order.ServiceBindListener;

public class PinpadActivity extends AppCompatActivity {
    public static final String VALOR = "valor";
    public static final String DESCRICAO = "descricao";
    @BindView(R.id.valor_textView)
    TextView valorTextView;
    @BindView(R.id.pagar_button)
    Button pagarButton;
    private long valorLimpo = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pinpad);
        ButterKnife.bind(this);

        String titulo = "";
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if(bundle.containsKey(DESCRICAO)){
                titulo = bundle.getString(DESCRICAO);
            }
            if(bundle.containsKey(VALOR)){
                Float valor = bundle.getFloat(VALOR);
                NumberFormat df = DecimalFormat.getInstance();
                df.setMinimumFractionDigits(2);
                this.valorLimpo = Integer.valueOf(df.format(valor).replaceAll("[.,]", ""));
            }
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(titulo);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.setValor(this.valorLimpo);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.n1_button)
    void onN1ButtonClicked(){
        this.setValor(concatDigito(1));
    }

    @OnClick(R.id.n2_button)
    void onN2ButtonClicked(){
        this.setValor(concatDigito(2));
    }

    @OnClick(R.id.n3_button)
    void onN3ButtonClicked(){
        this.setValor(concatDigito(3));
    }

    @OnClick(R.id.n4_button)
    void onN4ButtonClicked(){
        this.setValor(concatDigito(4));
    }

    @OnClick(R.id.n5_button)
    void onN5ButtonClicked(){
        this.setValor(concatDigito(5));
    }

    @OnClick(R.id.n6_button)
    void onN6ButtonClicked(){
        this.setValor(concatDigito(6));
    }

    @OnClick(R.id.n7_button)
    void onN7ButtonClicked(){
        this.setValor(concatDigito(7));
    }

    @OnClick(R.id.n8_button)
    void onN8ButtonClicked(){
        this.setValor(concatDigito(8));
    }

    @OnClick(R.id.n9_button)
    void onN9ButtonClicked(){
        this.setValor(concatDigito(9));
    }

    @OnClick(R.id.n0_button)
    void onN0ButtonClicked(){
        this.setValor(concatDigito(0));
    }

    @OnClick(R.id.backspace_button)
    void onBackspaceButtonClicked(){
        String valorString = String.valueOf(this.valorLimpo);
        if(valorString.length() > 1) {
            this.valorLimpo = Integer.valueOf(valorString.substring(0, valorString.length() - 1));
        }else{
            this.valorLimpo = 0;
        }

        this.setValor(this.valorLimpo);
    }

    @OnLongClick(R.id.backspace_button)
    void onBackspaceButtonLongClicked(){
        this.valorLimpo = 0;
        this.setValor(this.valorLimpo);
    }

    @OnClick(R.id.calculator_button)
    void onCalculatorButtonClicked(){
        Toast.makeText(this, "Implementar uma calculadora", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.pagar_button)
    void onPagarButtonClicked(){
        Intent intent = new Intent(this, PagamentoActivity.class);
        startActivity(intent);
        /*OrderManager orderManager = OrderManagerSingleton.getInstance();
        orderManager.createDraftOrder("REFERENCIA DA ORDEM");
        Order order = orderManager.createDraftOrder("Pedido");

        order.addItem("sku", "Valor Avulso", this.valorLimpo, 1, "UNIDADE");
        orderManager.updateOrder(order);

        orderManager.placeOrder(order);

        CheckoutRequest.Builder requestBuilder = new CheckoutRequest.Builder()
                .orderId(order.getId())
                .amount(this.valorLimpo)
                .paymentCode(paymentCode)
                .installments(installments);*/
    }

    private long concatDigito(Integer digito){
        if(String.valueOf(this.valorLimpo).length() < 9){
            this.valorLimpo = Integer.valueOf(this.valorLimpo + String.valueOf(digito));
        }
        return this.valorLimpo;
    }

    private void setValor(long valor){
        BigDecimal valorDecimal = new BigDecimal(valor).divide(new BigDecimal("100"));
        this.pagarButton.setEnabled(!valorDecimal.equals(new BigDecimal(0)));
        valorTextView.setText(DecimalFormat.getCurrencyInstance().format(valorDecimal));
    }
}