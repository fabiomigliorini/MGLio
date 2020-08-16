package br.com.mgpapelaria.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.maltaisn.calcdialog.CalcDialog;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import br.com.mgpapelaria.R;
import br.com.mgpapelaria.model.VendaRegistrada;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import cielo.orders.domain.Credentials;
import cielo.orders.domain.Order;
import cielo.sdk.order.OrderManager;
import cielo.sdk.order.ServiceBindListener;

public class PinpadActivity extends AppCompatActivity implements CalcDialog.CalcDialogCallback {
    public static final Integer PAGAMENTO_REQUEST = 1;
    @BindView(R.id.valor_textView)
    TextView valorTextView;
    @BindView(R.id.pagar_button)
    Button pagarButton;
    private long valorLimpo = 0;
    private VendaRegistrada vendaRegistrada;
    private final CalcDialog calcDialog = new CalcDialog();
    private OrderManager orderManager = null;
    private static boolean orderManagerServiceBinded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pinpad);
        ButterKnife.bind(this);

        String titulo = "";
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if(bundle.containsKey(ListaVendasRegistradasActivity.VENDA_REGISTRADA)){
                this.vendaRegistrada = (VendaRegistrada) bundle.getSerializable(ListaVendasRegistradasActivity.VENDA_REGISTRADA);
                titulo = vendaRegistrada.getCodNegocio().toString();
                this.valorLimpo = vendaRegistrada.getValorSaldo().multiply(new BigDecimal(100)).longValue();
            }
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(titulo);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.calcDialog.getSettings().setNumberFormat(NumberFormat.getCurrencyInstance());
        this.calcDialog.getSettings().setSignBtnShown(false);
        //this.calcDialog.getSettings().setMinValue(new BigDecimal(0));
        //this.calcDialog.getSettings().setMinValue(new BigDecimal(999999999));

        this.setValor(this.valorLimpo);
        this.configSDK();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PAGAMENTO_REQUEST){
            if (resultCode == PagamentoActivity.PAGAMENTO_EFETUADO_RESULT) {
                finish();
            }
        }
    }

    @Override
    public void onValueEntered(int requestCode, @Nullable BigDecimal value) {
        if(this.isBetween(value, new BigDecimal(0), new BigDecimal(99999999))){
            this.valorLimpo = value.multiply(new BigDecimal(100)).longValue();
            this.setValor(this.valorLimpo);
        }else{
            Toast.makeText(this, "Valor inválido", Toast.LENGTH_SHORT).show();
        }
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
        //calcDialog.getSettings().
        calcDialog.getSettings().setInitialValue(new BigDecimal(this.valorLimpo).divide(new BigDecimal(100)));
        calcDialog.show(getSupportFragmentManager(), "calc_dialog");
    }

    @OnClick(R.id.pagar_button)
    void onPagarButtonClicked(){
        Intent intent = new Intent(this, PagamentoActivity.class);
        intent.putExtra(PagamentoActivity.VALOR_PAGO, this.valorLimpo);
        intent.putExtra(PagamentoActivity.ORDER, this.criarPedido(this.vendaRegistrada));
        startActivityForResult(intent, PAGAMENTO_REQUEST);
    }

    private Order criarPedido(VendaRegistrada vendaRegistrada){
        Order order;
        if(vendaRegistrada != null){
            String orderName = vendaRegistrada.getCodNegocio().toString();
            order = this.orderManager.createDraftOrder("Pedido: #" + orderName);
            order.setNumber(orderName);
        }else{
            order = this.orderManager.createDraftOrder("Valor avulso");
        }
        order.addItem("fake-sku", "Produtos de papelaria", this.valorLimpo, 1, "QTD");

        this.orderManager.updateOrder(order);

        return order;
    }

    protected void configSDK() {
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
            }

            @Override
            public void onServiceUnbound() {
                orderManagerServiceBinded = false;
            }
        });
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

    private boolean isBetween(BigDecimal price, BigDecimal start, BigDecimal end){
        return price.compareTo(start) > 0 && price.compareTo(end) < 0;
    }
}