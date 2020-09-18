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
    public static final String CREDITO_PARCELADO_OPTION = "credito_parcelado";
    private OptionListener optionListener;

    public interface OptionListener{
        void onOptionClickListener(String option);
    }

    public CreditoFragment() {
        // Required empty public constructor
    }

    public CreditoFragment(OptionListener listener) {
        this.optionListener = listener;
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

    public void setOptionListener(OptionListener optionListener){
        this.optionListener = optionListener;
    }

    @OnClick(R.id.credito_avista_button)
    void onRefeicaoButtonClicked(){
        this.formaPagamentoListener.onFormaSelecionadaListener(PaymentCode.CREDITO_AVISTA, null);
    }

    @OnClick(R.id.credito_parcelado_loja_button)
    void onCreditoParceladoLojaButtonClicked(){
        //this.formaPagamentoListener.onFormaSelecionadaListener(PaymentCode.CREDITO_PARCELADO_LOJA, null);
        if(this.optionListener != null){
            this.optionListener.onOptionClickListener(CREDITO_PARCELADO_OPTION);
        }
    }
}