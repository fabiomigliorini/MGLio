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
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import br.com.mgpapelaria.R;
import br.com.mgpapelaria.adapter.VendasAbertasAdapter;
import br.com.mgpapelaria.api.ApiService;
import br.com.mgpapelaria.api.RetrofitUtil;
import br.com.mgpapelaria.model.VendaAberta;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cielo.orders.domain.Settings;
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
        toolbar.setTitle("Vendas");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.apiService = RetrofitUtil.createService(this, ApiService.class);

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

            startActivity(intent);
        });
        this.vendasRecyclerView.setAdapter(this.recyclerViewAdapter);

        this.swipeRefreshLayout.setRefreshing(true);
        this.buscaVendasAbertas();
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
        this.buscaVendasAbertas();
    }

    private void buscaVendasAbertas(){
        String numeroLogico = new InfoManager().getSettings(this).getLogicNumber();
        Call<List<VendaAberta>> vendas = this.apiService.getVendasAbertas("04576775000241", numeroLogico);
        vendas.enqueue(new Callback<List<VendaAberta>>() {
            @Override
            public void onResponse(Call<List<VendaAberta>> call, Response<List<VendaAberta>> response) {
                recyclerViewAdapter.apagaVendas();
                recyclerViewAdapter.setVendas(response.body());
                swipeRefreshLayout.setRefreshing(false);
                vendasRecyclerView.setVisibility(View.VISIBLE);
                noResultsView.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onFailure(Call<List<VendaAberta>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                vendasRecyclerView.setVisibility(View.INVISIBLE);
                noResultsView.setVisibility(View.INVISIBLE);
            }
        });
    }
}