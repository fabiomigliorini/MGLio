package br.com.mgpapelaria.fragment.pagamento;

import android.os.Bundle;

import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import br.com.mgpapelaria.R;
import br.com.mgpapelaria.util.InputFilterMinMax;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CrediarioFragment extends PagamentoBaseFragment {

    @BindView(R.id.quantidade_text_field)
    TextInputLayout quantidadeInputLayout;

    @BindView(R.id.quantidade_edit_text)
    TextInputEditText quantidadeEditText;

    @BindView(R.id.data_text_field)
    TextInputLayout dataInputLayout;

    @BindView(R.id.data_edit_text)
    TextInputEditText dateEditText;

    private Date selectedDate;

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
        View view = inflater.inflate(R.layout.fragment_crediario, container, false);
        ButterKnife.bind(this, view);

        this.quantidadeEditText.setFilters(new InputFilter[]{new InputFilterMinMax(1,99)});
        this.quantidadeEditText.setText("1");
        return view;
    }

    @OnClick(R.id.diminui_parcela_button)
    void diminuiParcela(){
        String valor = this.quantidadeEditText.getText().toString();
        if(!valor.equals("")){
            int valorAtual = Integer.parseInt(valor);
            if (valorAtual > 1) {
                valorAtual--;
                this.quantidadeEditText.setText(String.valueOf(valorAtual));
                this.quantidadeEditText.setSelection(valorAtual > 9 ? 2 :1);
            }
        }else{
            this.quantidadeEditText.setText(String.valueOf(1));
            this.quantidadeEditText.setSelection(1);
        }

    }

    @OnClick(R.id.aumenta_parcela_button)
    void aumentaParcela(){
        String valor = this.quantidadeEditText.getText().toString();
        if(!valor.equals("")){
            int valorAtual = Integer.parseInt(valor);
            if (valorAtual < 99) {
                valorAtual++;
                this.quantidadeEditText.setText(String.valueOf(valorAtual));
                this.quantidadeEditText.setSelection(valorAtual > 9 ? 2 :1);
            }
        }else{
            this.quantidadeEditText.setText(String.valueOf(1));
            this.quantidadeEditText.setSelection(1);
        }

    }

    @OnClick(R.id.data_edit_text)
    void openDatePicker(){
        MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
        builder.setSelection(this.selectedDate != null ? this.selectedDate.getTime() : new Date().getTime());
        MaterialDatePicker<Long> datePicker = builder.build();
        datePicker.addOnPositiveButtonClickListener(selection -> {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            String dateFormatted = sdf.format((Long)selection);
            try {
                this.selectedDate = sdf.parse(dateFormatted);
                this.dataInputLayout.setError(null);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            dateEditText.setText(dateFormatted);
        });
        datePicker.show(getFragmentManager(), "date");
    }

    @OnClick(R.id.continuar_button)
    void continuar(){
        boolean hasError = false;
        if(this.quantidadeEditText.getText().toString().isEmpty()){
            this.quantidadeInputLayout.setError("Campo obrigatório");
            hasError = true;
        }

        if(this.selectedDate == null){
            this.dataInputLayout.setError("Campo obrigatório");
            hasError = true;
        }

        if(hasError){
            return;
        }
    }
}