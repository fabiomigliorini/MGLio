package br.com.mgpapelaria.fragment.pagamento;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import br.com.mgpapelaria.R;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cielo.sdk.order.payment.PaymentCode;

public class CreditoFragment extends PagamentoBaseFragment {

    public CreditoFragment() {
        // Required empty public constructor
    }

    public static CreditoFragment newInstance(String param1, String param2) {
        CreditoFragment fragment = new CreditoFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_credito, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick(R.id.credito_avista_button)
    void onRefeicaoButtonClicked(){
        this.formaPagamentoListener.onFormaSelecionadaListener(PaymentCode.CREDITO_AVISTA, null);
    }

    @OnClick(R.id.credito_parcelado_loja_button)
    void onCreditoParceladoLojaButtonClicked(){
        this.formaPagamentoListener.onFormaSelecionadaListener(PaymentCode.CREDITO_PARCELADO_LOJA, null);
    }

    @OnClick(R.id.credito_parcelado_banco_button)
    void onCreditoParceladoBancoButtonClicked(){
        this.formaPagamentoListener.onFormaSelecionadaListener(PaymentCode.CREDITO_PARCELADO_BNCO, null);
    }
}