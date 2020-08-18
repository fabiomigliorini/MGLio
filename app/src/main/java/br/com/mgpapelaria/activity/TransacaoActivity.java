package br.com.mgpapelaria.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import br.com.mgpapelaria.R;
import br.com.mgpapelaria.adapter.TransacaoPagamentosAdapter;
import br.com.mgpapelaria.api.ApiService;
import br.com.mgpapelaria.api.RetrofitUtil;
import br.com.mgpapelaria.model.OrderRequest;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cielo.orders.domain.CancellationRequest;
import cielo.orders.domain.Credentials;
import cielo.orders.domain.Order;
import cielo.sdk.order.OrderManager;
import cielo.sdk.order.ServiceBindListener;
import cielo.sdk.order.cancellation.CancellationListener;
import cielo.sdk.order.payment.Payment;
import cielo.sdk.order.payment.PaymentError;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TransacaoActivity extends AppCompatActivity {
    public static final String TRANSACAO = "transacao";
    public static final Integer CANCELAMENTO_EFETUADO_RESULT = 1;

    @BindView(R.id.nome_cliente)
    TextView nomeClienteTextView;
    @BindView(R.id.item_descricao)
    TextView itemDescricaoTextView;
    @BindView(R.id.price)
    TextView priceTextView;
    @BindView(R.id.cancelar_button)
    MaterialButton cancelarButton;
    @BindView(R.id.payments_recylcer_view)
    RecyclerView pagamentosRecyclerView;
    private TransacaoPagamentosAdapter pagamentosRecyclerViewAdapter;
    private NumberFormat nf = DecimalFormat.getCurrencyInstance();
    private Order transacao;
    private OrderManager orderManager = null;
    private static boolean orderManagerServiceBinded = false;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transacao);
        ButterKnife.bind(this);

        this.apiService = RetrofitUtil.build().create(ApiService.class);
        this.configSDK();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if(bundle.containsKey(TRANSACAO)){
                transacao = (Order) bundle.getSerializable(TRANSACAO);
            }
        }

        if(transacao == null){
            return;
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(transacao.getReference());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(transacao.getPayments().size() > 0){
            this.nomeClienteTextView.setText(transacao.getPayments().get(0).getPaymentFields().get("clientName"));
        }else{
            this.nomeClienteTextView.setVisibility(View.GONE);
        }
        this.itemDescricaoTextView.setText(transacao.getItems().get(0).getName());
        this.priceTextView.setText(nf.format(new BigDecimal(transacao.getPrice()).divide(new BigDecimal(100))));

        boolean pagamentoCancelado = false;
        for(Payment payment : transacao.getPayments()){
            pagamentoCancelado = payment.getPaymentFields().get("v40Code").equals("28");
        }
        if(pagamentoCancelado){
            this.cancelarButton.setVisibility(View.GONE);
        }

        this.pagamentosRecyclerView.setHasFixedSize(true);
        this.pagamentosRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        this.pagamentosRecyclerView.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        this.pagamentosRecyclerViewAdapter = new TransacaoPagamentosAdapter(transacao.getPayments(), this);
        this.pagamentosRecyclerView.setAdapter(this.pagamentosRecyclerViewAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_transacao, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_mostrar_json:
                Intent intent = new Intent(this, TransacaoJsonActivity.class);
                intent.putExtra(TransacaoJsonActivity.TRANSACAO, this.transacao);
                startActivity(intent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        orderManager.unbind();
        super.onDestroy();
    }

    @OnClick(R.id.cancelar_button)
    void cancelarPagamento(){
        CancellationRequest request = new CancellationRequest.Builder()
                .orderId(this.transacao.getId()) /* Obrigatório */
                .authCode(this.transacao.getPayments().get(0).getAuthCode()) /* Obrigatório */
                .cieloCode(this.transacao.getPayments().get(0).getCieloCode()) /* Obrigatório */
                .value(this.transacao.getPayments().get(0).getAmount()) /* Obrigatório */
                //.ec("0000000000000003") /* Opcional */
                .build();

        orderManager.cancelOrder(request, new CancellationListener() {
            @Override
            public void onSuccess(Order order) {
                Toast.makeText(getApplicationContext(),"O pagamento foi cancelado.", Toast.LENGTH_LONG).show();
                order.cancel();
                orderManager.updateOrder(order);
                transacao = order;
                sendOrder(order);
                setResult(CANCELAMENTO_EFETUADO_RESULT);
                finish();
            }

            @Override
            public void onCancel() {
                Toast.makeText(getApplicationContext(),"A operação foi cancelada.", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(PaymentError paymentError) {
                Toast.makeText(getApplicationContext(),"Houve um erro no cancelamento", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void sendOrder(Order order){
        this.apiService.updateOrder(new OrderRequest(order)).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.code() == 200){
                    Log.i("PAGAMENTO", response.raw().toString());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                //TODO: Salvar no bd como não enviado
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
}