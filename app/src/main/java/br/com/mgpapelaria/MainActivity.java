package br.com.mgpapelaria;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        //getSupportActionBar().hide();
    }

    @OnClick(R.id.venda_avulsa_button)
    void onVendaAvulsaClicked(){
        Intent intent = new Intent(this, PinpadActivity.class);
        startActivity(intent);
    }
}