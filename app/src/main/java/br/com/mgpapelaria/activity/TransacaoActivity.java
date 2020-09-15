package br.com.mgpapelaria.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
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

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.mgpapelaria.R;
import br.com.mgpapelaria.adapter.TransacaoPagamentosAdapter;
import br.com.mgpapelaria.api.ApiService;
import br.com.mgpapelaria.api.RetrofitUtil;
import br.com.mgpapelaria.dao.PagamentoDAO;
import br.com.mgpapelaria.dao.PedidoDAO;
import br.com.mgpapelaria.database.AppDatabase;
import br.com.mgpapelaria.fragment.TransacaoBottomSheetFragment;
import br.com.mgpapelaria.model.OrderRequest;
import br.com.mgpapelaria.model.Pagamento;
import br.com.mgpapelaria.model.Pedido;
import br.com.mgpapelaria.model.PedidoWithPagamentos;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cielo.orders.domain.CancellationRequest;
import cielo.orders.domain.Credentials;
import cielo.orders.domain.Order;
import cielo.orders.domain.PrinterAttributes;
import cielo.orders.domain.Status;
import cielo.sdk.order.OrderManager;
import cielo.sdk.order.PrinterListener;
import cielo.sdk.order.ServiceBindListener;
import cielo.sdk.order.cancellation.CancellationListener;
import cielo.sdk.order.payment.Payment;
import cielo.sdk.order.payment.PaymentError;
import cielo.sdk.printer.PrinterManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static br.com.mgpapelaria.util.PrintHelper.formatCNPJ;
import static br.com.mgpapelaria.util.PrintHelper.formatDate;
import static br.com.mgpapelaria.util.PrintHelper.formatValor;
import static br.com.mgpapelaria.util.PrintHelper.getCenterStyle;
import static br.com.mgpapelaria.util.PrintHelper.getColumnStyle;
import static br.com.mgpapelaria.util.PrintHelper.getLeftStyle;
import static br.com.mgpapelaria.util.PrintHelper.getRightStyle;

public class TransacaoActivity extends AppCompatActivity {
    public static final String TRANSACAO = "transacao";
    public static final Integer CANCELAMENTO_EFETUADO_RESULT = 1;
    public static final Integer SINCRONIZACAO_EFETUADA_RESULT = 2;

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
    private Pedido transacao;
    private List<Pagamento> pagamentos;
    private OrderManager orderManager = null;
    private static boolean orderManagerServiceBinded = false;
    private ApiService apiService;
    private PedidoDAO pedidoDAO;
    private PagamentoDAO pagamentoDAO;
    private boolean sincronizadoValorInicial = false;
    private SharedPreferences sharedPref;
    private PrinterListener printerListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transacao);
        ButterKnife.bind(this);

        this.apiService = RetrofitUtil.createService(this, ApiService.class);
        this.configSDK();
        AppDatabase database = AppDatabase.build(this);
        this.pedidoDAO = database.pedidoDAO();
        this.pagamentoDAO = database.pagamentoDAO();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if(bundle.containsKey(TRANSACAO)){
                PedidoWithPagamentos pedidoWithPagamentos = (PedidoWithPagamentos)bundle.getSerializable(TRANSACAO);
                transacao = pedidoWithPagamentos.pedido;
                pagamentos = pedidoWithPagamentos.pagamentos;
            }
        }

        if(transacao == null){
            return;
        }

        this.sharedPref = getSharedPreferences("MG_Pref", Context.MODE_PRIVATE);
        this.sincronizadoValorInicial = transacao.sincronizado;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(transacao.order.getReference());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        if(transacao.order.getPayments().size() > 0){
            this.nomeClienteTextView.setText(transacao.order.getPayments().get(0).getPaymentFields().get("clientName"));
        }else{
            this.nomeClienteTextView.setVisibility(View.GONE);
        }
        this.itemDescricaoTextView.setText(transacao.order.getItems().get(0).getName());
        this.priceTextView.setText(nf.format(new BigDecimal(transacao.order.getPrice()).divide(new BigDecimal(100))));

        if(transacao.order.getStatus() == Status.CANCELED){
            this.cancelarButton.setVisibility(View.GONE);
        }

        this.pagamentosRecyclerView.setHasFixedSize(true);
        this.pagamentosRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        this.pagamentosRecyclerView.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        this.pagamentosRecyclerViewAdapter = new TransacaoPagamentosAdapter(transacao.order.getPayments(), this);
        this.pagamentosRecyclerViewAdapter.setOnItemClickedListenr(new TransacaoPagamentosAdapter.ItemClickListener() {
            @Override
            public void onClickListener(View view, int position) {
                TransacaoBottomSheetFragment bottomSheetFragment = new TransacaoBottomSheetFragment();
                Bundle bundle1 = new Bundle();
                bundle1.putSerializable(TransacaoBottomSheetFragment.USUARIO, pagamentos.get(position).userName);
                bundle1.putSerializable(TransacaoBottomSheetFragment.PAGAMENTO, transacao.order.getPayments().get(position));
                bottomSheetFragment.setArguments(bundle1);
                bottomSheetFragment.setItemClickListener(new TransacaoBottomSheetFragment.ItemClickListener() {
                    @Override
                    public void imprimirItemClicked() {
                        imprimirSegundaVia(transacao.order.getPayments().get(position), bottomSheetFragment);
                    }

                    @Override
                    public void enviarEmailClicked() {

                    }
                });
                bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
            }
        });
        this.pagamentosRecyclerView.setAdapter(this.pagamentosRecyclerViewAdapter);

        this.printerListener = new PrinterListener() {
            @Override
            public void onPrintSuccess() {

            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onWithoutPaper() {

            }
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_transacao, menu);
        if(this.transacao.sincronizado){
            MenuItem menuItem = menu.findItem(R.id.action_enviar_order);
            menuItem.setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if(this.sincronizadoValorInicial != this.transacao.sincronizado){
                    setResult(SINCRONIZACAO_EFETUADA_RESULT);
                }
                finish();
                return true;
            case R.id.action_mostrar_json:
                Intent intent = new Intent(this, TransacaoJsonActivity.class);
                intent.putExtra(TransacaoJsonActivity.TRANSACAO, this.transacao.order);
                startActivity(intent);
                return true;
            case R.id.action_enviar_order:
                ProgressDialog mDialog = new ProgressDialog(TransacaoActivity.this);
                mDialog.setMessage("Aguarde...");
                mDialog.setCancelable(false);
                mDialog.show();

                AsyncTask.execute(() -> {
                    int pedidoId = pedidoDAO.getPedidoIdByOrderId(transacao.order.getId());
                    PedidoWithPagamentos pedidoWithPagamentos = pedidoDAO.getWithPagamentosById(pedidoId);
                    this.sendOrder(pedidoWithPagamentos, mDialog);
                });
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
                .orderId(this.transacao.order.getId()) /* Obrigatório */
                .authCode(this.transacao.order.getPayments().get(0).getAuthCode()) /* Obrigatório */
                .cieloCode(this.transacao.order.getPayments().get(0).getCieloCode()) /* Obrigatório */
                .value(this.transacao.order.getPayments().get(0).getAmount()) /* Obrigatório */
                //.ec("0000000000000003") /* Opcional */
                .build();

        orderManager.cancelOrder(request, new CancellationListener() {
            @Override
            public void onSuccess(Order order) {
                order.cancel();
                orderManager.updateOrder(order);
                transacao.order = order;
                AsyncTask.execute(() -> {
                    int pedidoId = pedidoDAO.getPedidoIdByOrderId(order.getId());
                    alteraStatusOrder(order);
                    persistePagamento(pedidoId, order);
                    PedidoWithPagamentos pedidoWithPagamentos = pedidoDAO.getWithPagamentosById(pedidoId);
                    sendOrder(pedidoWithPagamentos);
                    setResult(CANCELAMENTO_EFETUADO_RESULT);
                    finish();
                });
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

    private void alteraStatusOrder(Order order){
        pedidoDAO.cancelaOrder(order.getId(), order);
    }

    private void persistePagamento(int pedidoId, Order order){
        Pagamento pagamento = new Pagamento();
        pagamento.pedidoId = pedidoId;
        pagamento.paymentId = order.getPayments().get(1).getId();
        pagamento.userId = this.sharedPref.getInt("userId", -1);
        pagamento.userName = this.sharedPref.getString("user", null);

        this.pagamentoDAO.insertPagamento(pagamento);
    }

    private void sendOrder(PedidoWithPagamentos pedidoWithPagamentos){
        sendOrder(pedidoWithPagamentos, null);
    }

    private void sendOrder(PedidoWithPagamentos pedidoWithPagamentos, ProgressDialog dialog){
        this.apiService.updateOrder(new OrderRequest(pedidoWithPagamentos)).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.code() == 200){
                    AsyncTask.execute(() -> {
                        pedidoDAO.updatePedidoSincronizado(pedidoWithPagamentos.pedido.order.getId(), true);
                    });
                    transacao.sincronizado = true;
                    invalidateOptionsMenu();
                    if(dialog != null){
                        dialog.dismiss();
                    }
                }else{
                    //TODO: Colocar algum erro aqui
                    AsyncTask.execute(() -> {
                        pedidoDAO.updatePedidoSincronizado(pedidoWithPagamentos.pedido.order.getId(), false);
                    });
                    if(dialog != null){
                        dialog.dismiss();
                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                AsyncTask.execute(() -> {
                    pedidoDAO.updatePedidoSincronizado(pedidoWithPagamentos.pedido.order.getId(), false);
                });
                if(dialog != null){
                    dialog.dismiss();
                }
            }

        });
    }



    private void imprimirSegundaVia(Payment payment, BottomSheetDialogFragment bottomSheetDialogFragment){
        Bitmap logo = BitmapFactory.decodeResource(getResources(), R.drawable.logo_cielo);

        PrinterManager printerManager = new PrinterManager(TransacaoActivity.this);

        printerManager.printImage(logo, getCenterStyle(), printerListener);
        printerManager.printText(payment.getBrand(), getCenterStyle(), printerListener);
        printerManager.printText(payment.getPaymentFields().get("productName"), getCenterStyle(), printerListener);
        printerManager.printText(" ", getLeftStyle(), printerListener);
        printerManager.printText("*REIMPRESSÃO*", getCenterStyle(), printerListener);
        printerManager.printText(" ", getLeftStyle(), printerListener);
        printerManager.printText(payment.getMask(), getCenterStyle(), printerListener);
        printerManager.printText("VIA  CLIENTE / POS=" + payment.getTerminal(), getCenterStyle(), printerListener);
        printerManager.printText("CNPJ " + formatCNPJ(payment.getPaymentFields().get("document")), getLeftStyle(), printerListener);
        printerManager.printText(payment.getPaymentFields().get("merchantName"), getLeftStyle(true), printerListener);
        printerManager.printText(payment.getPaymentFields().get("cityState"), getLeftStyle(), printerListener);

        String[] text1 = new String[] {
                "DOC="+payment.getCieloCode(),
                formatDate(payment.getRequestDate()),
                "ONL-C" //TODO C para credito e D para debito?
        };

        printerManager.printMultipleColumnText(text1, getColumnStyle(true), printerListener);
        printerManager.printText(payment.getPaymentFields().get("typeName"), getLeftStyle(), printerListener);
        int parcelas = Integer.parseInt(payment.getPaymentFields().get("numberOfQuotas"));
        if(parcelas > 0){
            printerManager.printText("PARCELADO LOJA EM " + String.format("%02d", parcelas) + " PARCELAS", getLeftStyle(), printerListener);
        }
        printerManager.printText(" ", getLeftStyle(), printerListener);
        printerManager.printText("*REIMPRESSÃO*", getCenterStyle(), printerListener);
        printerManager.printText(" ", getLeftStyle(), printerListener);

        boolean cancelamento = false;
        if(payment.getPaymentFields().get("v40Code").equals("28")){
            cancelamento = true;
        }

        String[] text2 = new String[] {
                cancelamento ? "VALOR CANCELAMENTO:" : "VALOR:",
                "",
                formatValor(payment.getAmount())
        };

        printerManager.printMultipleColumnText(text2, getColumnStyle(true), printerListener);

        if(cancelamento){
            printerManager.printText("DADOS DA VENDA ORIGINAL", getLeftStyle(), printerListener);
            String[] text3 = new String[] {
                    "DOC="+payment.getPaymentFields().get("originalTransactionalId"),
                    payment.getPaymentFields().get("originalTransactionalDate"),
                    ""
            };
            printerManager.printMultipleColumnText(text3, getColumnStyle(), printerListener);
            String textoCancelamento = "SOLICITACAO DE CANCELAMENTO REGISTRADA APOS A APROVACAOO " +
                    "O CREDITO AO PORTADOR DO CARTAO SERA FEITO PELO BANCO EMISSOR";
            printerManager.printText(textoCancelamento, getCenterStyle(), printerListener);
        }

        printerManager.printText("\n\n\n\n", getLeftStyle(), new PrinterListener() {
            @Override
            public void onPrintSuccess() {
                Log.i("PRINT", "onPrintSuccess");
                bottomSheetDialogFragment.dismiss();
            }

            @Override
            public void onError(Throwable throwable) {
                Log.i("PRINT", "onError");
            }

            @Override
            public void onWithoutPaper() {
                Log.i("PRINT", "onWithoutPaper");
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