package br.com.mgpapelaria.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MenuItem;
import android.view.View;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.mgpapelaria.R;
import br.com.mgpapelaria.adapter.VendasRegistradasAdapter;
import br.com.mgpapelaria.model.VendaRegistrada;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ListaVendasRegistradasActivity extends AppCompatActivity {
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.no_results_view)
    View noResultsView;
    @BindView(R.id.vendas_recylcer_view)
    RecyclerView vendasRecyclerView;
    //private List<VendaRegistrada> vendas = new ArrayList<>();
    private VendasRegistradasAdapter recyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_vendas_registradas);

        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Vendas");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                recyclerViewAdapter.clearItens();
                recyclerViewAdapter.insertItem(new VendaRegistrada(1, "Venda 1", new BigDecimal(100), new Date()));
                recyclerViewAdapter.insertItem(new VendaRegistrada(2, "Venda 2", new BigDecimal(352.85), new Date()));
                recyclerViewAdapter.insertItem(new VendaRegistrada(3, "Venda 3", new BigDecimal(1000), new Date()));

                swipeRefreshLayout.setRefreshing(false);
                vendasRecyclerView.setVisibility(View.VISIBLE);
                noResultsView.setVisibility(View.INVISIBLE);
            }
        }, 2000);
    }
}