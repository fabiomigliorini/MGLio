package br.com.mgpapelaria.fragment.pagamento;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import br.com.mgpapelaria.R;

public class DebitoFragment extends PagamentoBaseFragment {

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
        return inflater.inflate(R.layout.fragment_debito, container, false);
    }
}