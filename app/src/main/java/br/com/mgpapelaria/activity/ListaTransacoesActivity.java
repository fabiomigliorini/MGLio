package br.com.mgpapelaria.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.mgpapelaria.R;
import br.com.mgpapelaria.adapter.TransacoesAdapter;
import br.com.mgpapelaria.adapter.VendasRegistradasAdapter;
import br.com.mgpapelaria.model.Transacao;
import br.com.mgpapelaria.model.VendaRegistrada;
import br.com.mgpapelaria.util.OrderManagerSingleton;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cielo.orders.domain.Credentials;
import cielo.orders.domain.Order;
import cielo.orders.domain.ResultOrders;
import cielo.sdk.order.OrderManager;
import cielo.sdk.order.ServiceBindListener;

public class ListaTransacoesActivity extends AppCompatActivity {
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.no_results_view)
    View noResultsView;
    @BindView(R.id.transacoes_recylcer_view)
    RecyclerView transacoesRecyclerView;
    private TransacoesAdapter recyclerViewAdapter;
    OrderManager orderManager;
    private final String TAG = "ORDER_LIST";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_transacoes);

        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Transações");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.swipeRefreshLayout.setOnRefreshListener(this::buscaTransacoes);
        this.swipeRefreshLayout.setColorSchemeColors(
                Color.parseColor("#3b3bcc"),
                Color.parseColor("#e5de04"),
                Color.parseColor("#ff0000"));

        this.transacoesRecyclerView.setHasFixedSize(true);
        this.transacoesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        this.transacoesRecyclerView.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        this.recyclerViewAdapter = new TransacoesAdapter(new ArrayList<>());
        /*this.recyclerViewAdapter.setOnItemClickedListenr((view, position) -> {
            Intent intent = new Intent(this, PinpadActivity.class);
            Transacao transacao = recyclerViewAdapter.getTransacoes().get(position);
            intent.putExtra(PinpadActivity.DESCRICAO, transacao.getDescricao());
            intent.putExtra(PinpadActivity.VALOR, transacao.getValor().floatValue());

            startActivity(intent);
        });*/
        this.transacoesRecyclerView.setAdapter(this.recyclerViewAdapter);

        this.swipeRefreshLayout.setRefreshing(true);
        //this.configSDK();
        this.buscaTransacoes();
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

    @OnClick(R.id.refresh_button)
    void onRefreshButtonClicked(){
        this.swipeRefreshLayout.setRefreshing(true);
        this.buscaTransacoes();
    }

    private void buscaTransacoes(){
        /*new Handler().postDelayed(() -> {
            recyclerViewAdapter.apagaVendas();
            recyclerViewAdapter.adicionaVenda(new VendaRegistrada(1, "Venda 1", new BigDecimal(100), new Date()));
            recyclerViewAdapter.adicionaVenda(new VendaRegistrada(2, "Venda 2", new BigDecimal(352.85), new Date()));
            recyclerViewAdapter.adicionaVenda(new VendaRegistrada(3, "Venda 3", new BigDecimal(1000), new Date()));

            swipeRefreshLayout.setRefreshing(false);
            vendasRecyclerView.setVisibility(View.VISIBLE);
            noResultsView.setVisibility(View.INVISIBLE);
        }, 1000);*/
        ResultOrders resultOrders = OrderManagerSingleton.getInstance().retrieveOrders(200, 0);
        final List<Order> orderList = resultOrders.getResults();

        if(!orderList.isEmpty()){
            recyclerViewAdapter.apagaTransacoes();
            recyclerViewAdapter.adicionaTransacoes(orderList);
        }else{
            //vazio
            this.transacoesRecyclerView.setVisibility(View.GONE);
            this.noResultsView.setVisibility(View.VISIBLE);
        }
        swipeRefreshLayout.setRefreshing(false);
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
                buscaTransacoes();
            }

            @Override
            public void onServiceUnbound() {
            }
        });
    }
}