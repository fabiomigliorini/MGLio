package br.com.mgpapelaria.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import br.com.mgpapelaria.R;
import br.com.mgpapelaria.util.OrderManagerSingleton;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        //getSupportActionBar().hide();
        OrderManagerSingleton.getInstance();
    }

    @OnClick(R.id.valor_avulso_button)
    void onValorAvusoButtonClicked(){
        Intent intent = new Intent(this, PinpadActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.venda_registrada_button)
    void onVendaAvulsaClicked(){
        Intent intent = new Intent(this, ListaVendasRegistradasActivity.class);
        startActivity(intent);
        //overridePendingTransition(R.anim.slide_out_right, R.anim.slide_in_right);
    }
}