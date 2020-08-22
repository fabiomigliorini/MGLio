package br.com.mgpapelaria.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import br.com.mgpapelaria.R;
import br.com.mgpapelaria.adapter.TransacoesAdapter;
import br.com.mgpapelaria.model.Pedido;
import br.com.mgpapelaria.database.AppDatabase;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ListaTransacoesActivity extends AppCompatActivity {
    public static final Integer TRANSACAO_REQUEST = 1;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.no_results_view)
    View noResultsView;
    @BindView(R.id.transacoes_recylcer_view)
    RecyclerView transacoesRecyclerView;
    private TransacoesAdapter recyclerViewAdapter;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("LISTA", "ON CREATE");
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
            Pedido transacao = recyclerViewAdapter.getTransacoes().get(position);
            intent.putExtra(TransacaoActivity.TRANSACAO, transacao);

            startActivityForResult(intent, TRANSACAO_REQUEST);
        });
        this.transacoesRecyclerView.setAdapter(this.recyclerViewAdapter);

        this.swipeRefreshLayout.setRefreshing(true);

        this.db = AppDatabase.build(this);

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == TRANSACAO_REQUEST){
            if (resultCode == TransacaoActivity.CANCELAMENTO_EFETUADO_RESULT
            || resultCode == TransacaoActivity.SINCRONIZACAO_EFETUADA_RESULT) {
                this.onRefreshButtonClicked();
            }
        }
    }



    @OnClick(R.id.refresh_button)
    void onRefreshButtonClicked(){
        this.swipeRefreshLayout.setRefreshing(true);
        this.buscaTransacoes();
    }

    private void buscaTransacoes(){
        AsyncTask.execute(() -> {
            List<Pedido> pedidos = this.db.pedidoDAO().getAll();
            runOnUiThread(() -> {
                if(pedidos.size() > 0){
                    this.transacoesRecyclerView.setVisibility(View.VISIBLE);
                    this.noResultsView.setVisibility(View.GONE);
                    this.recyclerViewAdapter.apagaTransacoes();
                    this.recyclerViewAdapter.adicionaTransacoes(pedidos);
                }else{
                    this.transacoesRecyclerView.setVisibility(View.GONE);
                    this.noResultsView.setVisibility(View.VISIBLE);
                }
                this.swipeRefreshLayout.setRefreshing(false);
            });

        });
    }
}