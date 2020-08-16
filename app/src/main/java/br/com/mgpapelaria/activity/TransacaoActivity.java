package br.com.mgpapelaria.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import br.com.mgpapelaria.R;
import br.com.mgpapelaria.adapter.TransacaoItemAdapter;
import br.com.mgpapelaria.adapter.TransacaoPagamentosAdapter;
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
    /*@BindView(R.id.items_recylcer_view)
    RecyclerView itemsRecyclerView;
    private TransacaoItemAdapter itemsrecyclerViewAdapter;*/
    @BindView(R.id.payments_recylcer_view)
    RecyclerView pagamentosRecyclerView;
    private TransacaoPagamentosAdapter pagamentosRecyclerViewAdapter;
    private Order transacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transacao);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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

        /*this.itemsRecyclerView.setHasFixedSize(true);
        this.itemsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        this.itemsRecyclerView.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        this.itemsrecyclerViewAdapter = new TransacaoItemAdapter(transacao.getItems());
        this.itemsRecyclerView.setAdapter(this.itemsrecyclerViewAdapter);*/

        this.pagamentosRecyclerView.setHasFixedSize(true);
        this.pagamentosRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        this.pagamentosRecyclerView.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        this.pagamentosRecyclerViewAdapter = new TransacaoPagamentosAdapter(transacao.getPayments());
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
}