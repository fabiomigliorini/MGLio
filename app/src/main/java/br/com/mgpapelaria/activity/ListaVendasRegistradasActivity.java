package br.com.mgpapelaria.activity;

import androidx.annotation.NonNull;
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
import android.os.Message;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.mgpapelaria.R;
import br.com.mgpapelaria.adapter.VendasRegistradasAdapter;
import br.com.mgpapelaria.api.ApiService;
import br.com.mgpapelaria.api.RetrofitUtil;
import br.com.mgpapelaria.model.VendaRegistrada;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListaVendasRegistradasActivity extends AppCompatActivity {
    public static final String VENDA_REGISTRADA = "venda_registrada";

    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.no_results_view)
    View noResultsView;
    @BindView(R.id.vendas_recylcer_view)
    RecyclerView vendasRecyclerView;
    //private List<VendaRegistrada> vendas = new ArrayList<>();
    private VendasRegistradasAdapter recyclerViewAdapter;
    ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_vendas_registradas);

        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Vendas");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.apiService = RetrofitUtil.build().create(ApiService.class);

        this.swipeRefreshLayout.setOnRefreshListener(this::buscaVendasRegistradas);
        this.swipeRefreshLayout.setColorSchemeColors(
                Color.parseColor("#3b3bcc"),
                Color.parseColor("#e5de04"),
                Color.parseColor("#ff0000"));

        this.vendasRecyclerView.setHasFixedSize(true);
        this.vendasRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        this.vendasRecyclerView.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        this.recyclerViewAdapter = new VendasRegistradasAdapter(new ArrayList<>());
        this.recyclerViewAdapter.setOnItemClickedListenr((view, position) -> {
            Intent intent = new Intent(this, PinpadActivity.class);
            VendaRegistrada venda = recyclerViewAdapter.getVendas().get(position);
            intent.putExtra(VENDA_REGISTRADA, venda);

            startActivity(intent);
        });
        this.vendasRecyclerView.setAdapter(this.recyclerViewAdapter);

        this.swipeRefreshLayout.setRefreshing(true);
        this.buscaVendasRegistradas();
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
        this.buscaVendasRegistradas();
    }

    private void buscaVendasRegistradas(){
        Call<List<VendaRegistrada>> vendas = this.apiService.getVendasAbertas("04576775000241", "686052");
        vendas.enqueue(new Callback<List<VendaRegistrada>>() {
            @Override
            public void onResponse(Call<List<VendaRegistrada>> call, Response<List<VendaRegistrada>> response) {
                recyclerViewAdapter.apagaVendas();
                recyclerViewAdapter.setVendas(response.body());
                swipeRefreshLayout.setRefreshing(false);
                vendasRecyclerView.setVisibility(View.VISIBLE);
                noResultsView.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onFailure(Call<List<VendaRegistrada>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                vendasRecyclerView.setVisibility(View.INVISIBLE);
                noResultsView.setVisibility(View.INVISIBLE);
            }
        });
    }
}