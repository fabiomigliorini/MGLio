package br.com.mgpapelaria.activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.gson.GsonBuilder;

import br.com.mgpapelaria.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import cielo.orders.domain.Order;

public class TransacaoJsonActivity extends AppCompatActivity {
    public static final String TRANSACAO = "transacao";
    @BindView(R.id.transacao_json_text_view)
    TextView transacaoJsonTextView;
    private Order transacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transacao_json);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if(bundle.containsKey(TRANSACAO)){
                this.transacao = (Order) bundle.getSerializable(TRANSACAO);
            }
        }

        if(this.transacao == null){
            return;
        }

        this.transacaoJsonTextView.setText(new GsonBuilder().setPrettyPrinting().create().toJson(transacao));
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