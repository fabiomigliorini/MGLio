package br.com.mgpapelaria.fragment.pagamento;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import br.com.mgpapelaria.R;

public class CrediarioFragment extends PagamentoBaseFragment {

    public CrediarioFragment() {
        // Required empty public constructor
    }

    public static CrediarioFragment newInstance(String param1, String param2) {
        CrediarioFragment fragment = new CrediarioFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_crediario, container, false);
    }
}