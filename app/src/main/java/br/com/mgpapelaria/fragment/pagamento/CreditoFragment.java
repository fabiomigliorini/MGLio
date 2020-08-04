package br.com.mgpapelaria.fragment.pagamento;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import br.com.mgpapelaria.R;
import butterknife.ButterKnife;

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
}