package br.com.mgpapelaria.activity;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import br.com.mgpapelaria.R;
import br.com.mgpapelaria.fragment.pagamento.CrediarioFragment;
import br.com.mgpapelaria.fragment.pagamento.CreditoFragment;
import br.com.mgpapelaria.fragment.pagamento.DebitoFragment;
import br.com.mgpapelaria.fragment.pagamento.FormaPagamentoFragment;
import br.com.mgpapelaria.fragment.pagamento.PagamentoBaseFragment;
import br.com.mgpapelaria.fragment.pagamento.VoucherFragment;
import butterknife.ButterKnife;

public class PagamentoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pagamento);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        //toolbar.setNavigationIcon(R.drawable.ic_baseline_close_24);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /*this.getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                cancelaOperacao();
            }
        });*/

        this.initFragment();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //cancelaOperacao();
                //finish();
                hideKeyboard(this);
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initFragment(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        FormaPagamentoFragment fragment = new FormaPagamentoFragment();
        fragment.setOptionListener(this::onOptionClicked);
        fragmentTransaction.add(R.id.fragments_container, fragment);
        fragmentTransaction.commit();
    }

    private void onOptionClicked(String option){
        /*TypedArray a = getTheme().obtainStyledAttributes(R.style.AppTheme, new int[] {R.attr.homeAsUpIndicator});
        int attributeResourceId = a.getResourceId(0, 0);
        Drawable icon = ContextCompat.getDrawable(this, attributeResourceId);
        a.recycle();
        getSupportActionBar().setHomeAsUpIndicator(icon);*/
        PagamentoBaseFragment proximoFragment;
        switch (option){
            case FormaPagamentoFragment.CREDITO_OPTION:
                proximoFragment = new CreditoFragment();
                break;
            case FormaPagamentoFragment.DEBITO_OPTION:
                proximoFragment = new DebitoFragment();
                break;
            case FormaPagamentoFragment.CREDIARIO_OPTION:
                proximoFragment = new CrediarioFragment();
                break;
            case FormaPagamentoFragment.VOUCHER_OPTION:
                proximoFragment = new VoucherFragment();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + option);
        }

        proximoFragment.setOptionListener(this::onOptionClicked);
        this.replaceFragment(proximoFragment);
    }

    private void replaceFragment(PagamentoBaseFragment fragment){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_up, R.anim.slide_out_down, R.anim.slide_in_down);
        transaction.replace(R.id.fragments_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void cancelaOperacao(){
        Intent intent = new Intent(this, OperacaoCanceladaActivity.class);
        startActivity(intent);
        finish();
    }

    public static void hideKeyboard(Context context) {
        try {
            ((Activity) context).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            if ((((Activity) context).getCurrentFocus() != null) && (((Activity) context).getCurrentFocus().getWindowToken() != null)) {
                ((InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(((Activity) context).getCurrentFocus().getWindowToken(), 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}