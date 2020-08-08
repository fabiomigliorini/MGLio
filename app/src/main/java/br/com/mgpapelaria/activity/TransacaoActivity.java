package br.com.mgpapelaria.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.ArrayList;

import br.com.mgpapelaria.R;
import br.com.mgpapelaria.adapter.TransacaoItemAdapter;
import br.com.mgpapelaria.adapter.TransacoesAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;
import cielo.orders.domain.Order;

public class TransacaoActivity extends AppCompatActivity {
    public static final String TRANSACAO = "transacao";

    @BindView(R.id.orderId)
    TextView orderIdTextView;
    @BindView(R.id.price)
    TextView priceTextView;
    @BindView(R.id.paidAmount)
    TextView paidAmountTextView;
    @BindView(R.id.pendingAmount)
    TextView pendingAmountTextView;
    @BindView(R.id.reference)
    TextView referenceTextView;
    @BindView(R.id.number)
    TextView numberTextView;
    @BindView(R.id.notes)
    TextView notesTextView;
    @BindView(R.id.status)
    TextView statusTextView;
    @BindView(R.id.createdAt)
    TextView createdAtTextView;
    @BindView(R.id.updatedAt)
    TextView updatedAtTextView;
    @BindView(R.id.releaseDate)
    TextView releaseDateTextView;
    @BindView(R.id.type)
    TextView typeTextView;
    @BindView(R.id.items_recylcer_view)
    RecyclerView itemsRecyclerView;
    private TransacaoItemAdapter recyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transacao);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Order transacao = null;
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if(bundle.containsKey(TRANSACAO)){
                transacao = (Order) bundle.getSerializable(TRANSACAO);
            }
        }

        if(transacao == null){
            return;
        }

        this.orderIdTextView.setText(transacao.getId());
        this.priceTextView.setText(String.valueOf(transacao.getPrice()));
        this.paidAmountTextView.setText(String.valueOf(transacao.getPaidAmount()));
        this.pendingAmountTextView.setText(String.valueOf(transacao.pendingAmount()));
        this.referenceTextView.setText(transacao.getReference());
        this.numberTextView.setText(transacao.getNumber());
        this.notesTextView.setText(transacao.getNotes());
        this.statusTextView.setText(transacao.getStatus().name());
        this.createdAtTextView.setText(transacao.getCreatedAt().toString());
        this.updatedAtTextView.setText(transacao.getUpdatedAt().toString());
        if(transacao.getReleaseDate() != null){
            this.releaseDateTextView.setText(transacao.getReleaseDate().toString());
        }
        this.typeTextView.setText(transacao.getType().name());

        this.itemsRecyclerView.setHasFixedSize(true);
        this.itemsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        this.itemsRecyclerView.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        this.recyclerViewAdapter = new TransacaoItemAdapter(transacao.getItems());
        /*this.recyclerViewAdapter.setOnItemClickedListenr((view, position) -> {
            Intent intent = new Intent(this, TransacaoActivity.class);
            Order transacao = recyclerViewAdapter.getTransacoes().get(position);
            intent.putExtra(TransacaoActivity.TRANSACAO, transacao);

            startActivity(intent);
        });*/
        this.itemsRecyclerView.setAdapter(this.recyclerViewAdapter);
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
}