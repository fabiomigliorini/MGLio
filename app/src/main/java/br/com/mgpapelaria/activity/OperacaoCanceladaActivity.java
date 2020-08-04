package br.com.mgpapelaria.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import java.math.BigDecimal;
import java.util.Date;

import br.com.mgpapelaria.R;
import br.com.mgpapelaria.model.VendaRegistrada;

public class OperacaoCanceladaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operacao_cancelada);

        new Handler().postDelayed(this::finish, 2000);
    }
}