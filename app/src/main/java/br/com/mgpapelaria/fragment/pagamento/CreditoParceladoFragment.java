package br.com.mgpapelaria.fragment.pagamento;

import android.os.Bundle;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import br.com.mgpapelaria.R;
import br.com.mgpapelaria.util.InputFilterMinMax;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cielo.sdk.order.payment.PaymentCode;

public class CreditoParceladoFragment extends PagamentoBaseFragment {

    @BindView(R.id.quantidade_text_field)
    TextInputLayout quantidadeInputLayout;

    @BindView(R.id.quantidade_edit_text)
    TextInputEditText quantidadeEditText;

    public CreditoParceladoFragment() {

    }

    public static CreditoParceladoFragment newInstance(String param1, String param2) {
        CreditoParceladoFragment fragment = new CreditoParceladoFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_credito_parcelado, container, false);
        ButterKnife.bind(this, view);

        this.quantidadeEditText.setFilters(new InputFilter[]{new InputFilterMinMax(2,99)});
        this.quantidadeEditText.setText("2");
        return view;
    }

    @OnClick(R.id.diminui_parcela_button)
    void diminuiParcela(){
        String valor = this.quantidadeEditText.getText().toString();
        if(!valor.equals("")){
            int valorAtual = Integer.parseInt(valor);
            if (valorAtual > 2) {
                valorAtual--;
                this.quantidadeEditText.setText(String.valueOf(valorAtual));
                this.quantidadeEditText.setSelection(valorAtual > 9 ? 2 :1);
            }
        }else{
            this.quantidadeEditText.setText(String.valueOf(1));
            this.quantidadeEditText.setSelection(1);
        }
        this.quantidadeInputLayout.setError(null);
    }

    @OnClick(R.id.aumenta_parcela_button)
    void aumentaParcela(){
        String valor = this.quantidadeEditText.getText().toString();
        if(!valor.equals("")){
            int valorAtual = Integer.parseInt(valor);
            if (valorAtual < 6) {
                valorAtual++;
                this.quantidadeEditText.setText(String.valueOf(valorAtual));
                this.quantidadeEditText.setSelection(valorAtual > 9 ? 2 :1);
            }
        }else{
            this.quantidadeEditText.setText(String.valueOf(1));
            this.quantidadeEditText.setSelection(1);
        }
        this.quantidadeInputLayout.setError(null);
    }

    @OnClick(R.id.continuar_button)
    void continuar(){
        boolean hasError = false;
        if(this.quantidadeEditText.getText().toString().isEmpty()){
            this.quantidadeInputLayout.setError("Campo obrigatório");
            hasError = true;
        }

        if(hasError){
            return;
        }

        this.formaPagamentoListener.onFormaSelecionadaListener(PaymentCode.CREDITO_PARCELADO_LOJA, Integer.valueOf(this.quantidadeEditText.getText().toString()));
    }
}