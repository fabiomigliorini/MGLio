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
import br.com.mgpapelaria.adapter.VendasAbertasAdapter;
import br.com.mgpapelaria.api.ApiService;
import br.com.mgpapelaria.api.RetrofitUtil;
import br.com.mgpapelaria.model.VendaAberta;
import br.com.mgpapelaria.util.SharedPreferencesHelper;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cielo.sdk.info.InfoManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListaVendasAbertasActivity extends AppCompatActivity {
    public static final String VENDA_ABERTA = "venda_aberta";

    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.no_results_view)
    View noResultsView;
    @BindView(R.id.connection_error_view)
    View connectionErrorView;
    @BindView(R.id.vendas_recylcer_view)
    RecyclerView vendasRecyclerView;
    private VendasAbertasAdapter recyclerViewAdapter;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_vendas_abertas);

        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Vendas Abertas");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String token = SharedPreferencesHelper.getToken(this);

        this.apiService = RetrofitUtil.createService(this, ApiService.class, token);

        this.swipeRefreshLayout.setOnRefreshListener(this::buscaVendasAbertas);
        this.swipeRefreshLayout.setColorSchemeColors(
                Color.parseColor("#3b3bcc"),
                Color.parseColor("#e5de04"),
                Color.parseColor("#ff0000"));

        this.vendasRecyclerView.setHasFixedSize(true);
        this.vendasRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        this.vendasRecyclerView.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        this.recyclerViewAdapter = new VendasAbertasAdapter(new ArrayList<>());
        this.recyclerViewAdapter.setOnItemClickedListenr((view, position) -> {
            Intent intent = new Intent(this, PinpadActivity.class);
            VendaAberta venda = recyclerViewAdapter.getVendas().get(position);
            intent.putExtra(VENDA_ABERTA, venda);

            startActivityForResult(intent, PinpadActivity.PAGAMENTO_REQUEST);
        });
        this.vendasRecyclerView.setAdapter(this.recyclerViewAdapter);

        this.swipeRefreshLayout.setRefreshing(true);
        this.buscaVendasAbertas();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PinpadActivity.PAGAMENTO_REQUEST){
            if (resultCode == PagamentoActivity.PAGAMENTO_EFETUADO_RESULT) {
                finish();
            }
        }
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
        this.noResultsView.setVisibility(View.INVISIBLE);
        this.connectionErrorView.setVisibility(View.INVISIBLE);
        this.buscaVendasAbertas();
    }

    @OnClick(R.id.retry_button)
    void onRetryButtonClicked(){
        this    .onRefreshButtonClicked();
    }

    private void buscaVendasAbertas(){
        String numeroLogico = new InfoManager().getSettings(this).getLogicNumber();
        String url = SharedPreferencesHelper.getBaseUrlListVendasAbertas(this);
        Call<List<VendaAberta>> vendas = this.apiService.getVendasAbertas(url, "04576775000241", "686052");
        vendas.enqueue(new Callback<List<VendaAberta>>() {
            @Override
            public void onResponse(Call<List<VendaAberta>> call, Response<List<VendaAberta>> response) {
                if(response.code() == 200){
                    recyclerViewAdapter.apagaVendas();
                    recyclerViewAdapter.setVendas(response.body());
                    vendasRecyclerView.setVisibility(View.VISIBLE);
                    noResultsView.setVisibility(View.INVISIBLE);
                    connectionErrorView.setVisibility(View.INVISIBLE);
                }
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<List<VendaAberta>> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                vendasRecyclerView.setVisibility(View.INVISIBLE);
                connectionErrorView.setVisibility(View.VISIBLE);
            }
        });
    }
}