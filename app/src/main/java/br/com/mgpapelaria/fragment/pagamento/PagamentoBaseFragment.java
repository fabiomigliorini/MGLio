package br.com.mgpapelaria.fragment.pagamento;

import androidx.fragment.app.Fragment;

public class PagamentoBaseFragment extends Fragment {
    protected OptionListener optionListener;

    public interface OptionListener{
        void onOptionClickListener(String option);
    }

    public void setOptionListener(OptionListener optionListener){
        this.optionListener = optionListener;
    }
}
