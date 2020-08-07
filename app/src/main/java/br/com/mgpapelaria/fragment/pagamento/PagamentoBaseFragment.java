package br.com.mgpapelaria.fragment.pagamento;

import androidx.fragment.app.Fragment;

import cielo.sdk.order.payment.PaymentCode;

public class PagamentoBaseFragment extends Fragment {

    protected FormaPagamentoListener formaPagamentoListener;


    public interface FormaPagamentoListener{
        void onFormaSelecionadaListener(PaymentCode code, Object args);
    }

    public void setFormaPagamentoListener(FormaPagamentoListener listener){
        this.formaPagamentoListener = listener;
    }
}
