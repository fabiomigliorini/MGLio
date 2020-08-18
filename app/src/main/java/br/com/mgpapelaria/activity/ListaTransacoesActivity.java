package br.com.mgpapelaria.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import br.com.mgpapelaria.R;
import br.com.mgpapelaria.adapter.TransacoesAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cielo.orders.domain.Credentials;
import cielo.orders.domain.Order;
import cielo.orders.domain.ResultOrders;
import cielo.orders.domain.Status;
import cielo.sdk.order.OrderManager;
import cielo.sdk.order.ServiceBindListener;

public class ListaTransacoesActivity extends AppCompatActivity {
    public static final Integer TRANSACAO_REQUEST = 1;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.no_results_view)
    View noResultsView;
    @BindView(R.id.transacoes_recylcer_view)
    RecyclerView transacoesRecyclerView;
    private TransacoesAdapter recyclerViewAdapter;
    private OrderManager orderManager;

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
        this.recyclerViewAdapter.setOnItemClickedListenr((view, position) -> {
            Intent intent = new Intent(this, TransacaoActivity.class);
            Order transacao = recyclerViewAdapter.getTransacoes().get(position);
            intent.putExtra(TransacaoActivity.TRANSACAO, transacao);

            startActivityForResult(intent, TRANSACAO_REQUEST);
        });
        this.transacoesRecyclerView.setAdapter(this.recyclerViewAdapter);

        this.swipeRefreshLayout.setRefreshing(true);

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
        if(requestCode == TRANSACAO_REQUEST){
            if (resultCode == TransacaoActivity.CANCELAMENTO_EFETUADO_RESULT) {
                this.onRefreshButtonClicked();
            }
        }
    }

    @Override
    protected void onDestroy() {
        orderManager.unbind();
        super.onDestroy();
    }

    @OnClick(R.id.refresh_button)
    void onRefreshButtonClicked(){
        this.swipeRefreshLayout.setRefreshing(true);
        this.buscaTransacoes();
    }

    private void buscaTransacoes(){
        ///TODO Fazer uma paginação aqui pra pegar de 10 em 10
        ResultOrders resultOrders = this.orderManager.retrieveOrders(200, 0);

        if(resultOrders != null){
            this.transacoesRecyclerView.setVisibility(View.VISIBLE);
            this.noResultsView.setVisibility(View.GONE);
            final List<Order> orderList = new ArrayList<>();
            for(Order order : resultOrders.getResults()){
                if(order.getPayments().size() > 0){
                    orderList.add(order);
                }
            }
            this.recyclerViewAdapter.apagaTransacoes();
            this.recyclerViewAdapter.adicionaTransacoes(orderList);
        }else{
            this.transacoesRecyclerView.setVisibility(View.GONE);
            this.noResultsView.setVisibility(View.VISIBLE);
        }
        this.swipeRefreshLayout.setRefreshing(false);
    }

    public void configSDK() {
        Credentials credentials = new Credentials( "3bBCIdoFCNMUCJHFPZIQtuVAFQzb16O11O3twEnzz9MT5Huhng/ rRKDEcIfdA7AMcGSzStRAyHSCx44yEHsRVmLTeYMQfBEFFpcgm", "iIm9ujCG8IkvWOaTSFT3diNSEhNkjr0ttRf7hDnwEDMoO3u3S0");
        this.orderManager = new OrderManager(credentials, this);
        this.orderManager.bind(this, new ServiceBindListener() {

            @Override
            public void onServiceBoundError(Throwable throwable) {
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