package br.com.mgpapelaria.fragment.pagamento;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import br.com.mgpapelaria.R;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cielo.sdk.order.payment.PaymentCode;

public class VoucherFragment extends PagamentoBaseFragment {

    public VoucherFragment() {
        // Required empty public constructor
    }

    public static VoucherFragment newInstance(String param1, String param2) {
        VoucherFragment fragment = new VoucherFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_voucher, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick(R.id.voucher_refeicao_button)
    void onRefeicaoButtonClicked(){
        this.formaPagamentoListener.onFormaSelecionadaListener(PaymentCode.VOUCHER_REFEICAO, null);
    }

    @OnClick(R.id.voucher_alimentacao_button)
    void onAlimentacaoButtonClicked(){
        this.formaPagamentoListener.onFormaSelecionadaListener(PaymentCode.VOUCHER_ALIMENTACAO, null);
    }
}