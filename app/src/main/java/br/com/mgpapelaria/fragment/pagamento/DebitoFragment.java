package br.com.mgpapelaria.fragment.pagamento;

import android.content.Context;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.google.android.material.textfield.TextInputEditText;

import br.com.mgpapelaria.R;
import br.com.mgpapelaria.util.MoneyTextWatcher;
import butterknife.BindView;
import butterknife.ButterKnife;

public class DebitoFragment extends PagamentoBaseFragment {
    @BindView(R.id.valor_edit_text)
    TextInputEditText valorEditText;

    public DebitoFragment() {
        // Required empty public constructor
    }

    public static DebitoFragment newInstance(String param1, String param2) {
        DebitoFragment fragment = new DebitoFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_debito, container, false);
        ButterKnife.bind(this, view);

        this.valorEditText.addTextChangedListener(new MoneyTextWatcher(this.valorEditText));
        this.valorEditText.setText("0");
        this.valorEditText.requestFocus();

        ((InputMethodManager) (getActivity()).getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_IMPLICIT_ONLY);

        return view;
    }
}