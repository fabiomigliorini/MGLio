package br.com.mgpapelaria.fragment.pagamento;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import br.com.mgpapelaria.R;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FormaPagamentoFragment extends PagamentoBaseFragment {
    public static final String CREDITO_OPTION = "credito";
    public static final String DEBITO_OPTION = "debito";
    public static final String CREDIARIO_OPTION = "crediario";
    public static final String VOUCHER_OPTION = "voucher";

    public FormaPagamentoFragment() {
        // Required empty public constructor
    }

    public static FormaPagamentoFragment newInstance(String param1, String param2) {
        FormaPagamentoFragment fragment = new FormaPagamentoFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forma_pagamento, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick(R.id.credito_button)
    void onCreditoButtonClicked(){
        if(optionListener != null){
            optionListener.onOptionClickListener(CREDITO_OPTION);
        }
    }

    @OnClick(R.id.debito_button)
    void onDebitoButtonClicked(){
        if(optionListener != null){
            optionListener.onOptionClickListener(DEBITO_OPTION);
        }
    }

    @OnClick(R.id.crediario_button)
    void onCrediarioButtonClicked(){
        if(optionListener != null){
            optionListener.onOptionClickListener(CREDIARIO_OPTION);
        }
    }

    @OnClick(R.id.voucher_button)
    void onVoucherButtonClicked(){
        if(optionListener != null){
            optionListener.onOptionClickListener(VOUCHER_OPTION);
        }
    }
}