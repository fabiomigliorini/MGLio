package br.com.mgpapelaria.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import br.com.mgpapelaria.R;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TransacaoBottomSheetFragment extends BottomSheetDialogFragment {
    private ItemClickListener itemClickListener;

    public interface ItemClickListener{
        void imprimirItemClicked(boolean viaCliente);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.transacao_bottom_sheet, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick(R.id.imprimir_2_via_estabelecimento_button)
    void onImprimir2ViaEstabelecimentoItemClicked(){
        if(this.itemClickListener != null){
            this.itemClickListener.imprimirItemClicked(false);
        }
    }

    @OnClick(R.id.imprimir_2_via_cliente_button)
    void onImprimir2ViaClienteItemClicked() {
        if (this.itemClickListener != null) {
            this.itemClickListener.imprimirItemClicked(true);
        }
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
}
