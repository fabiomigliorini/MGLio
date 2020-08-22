package br.com.mgpapelaria.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import br.com.mgpapelaria.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TransacaoBottomSheetFragment extends BottomSheetDialogFragment {
    private ItemClickListener itemClickListener;

    public interface ItemClickListener{
        void imprimirItemClicked();
        void enviarEmailClicked();
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

    @OnClick(R.id.imprimir_2_via_button)
    void onImprimirItemClicked(){
        if(this.itemClickListener != null){
            this.itemClickListener.imprimirItemClicked();
        }
    }

    @OnClick(R.id.enviar_email_button)
    void onEnviarEmailItemClicked(){
        if(this.itemClickListener != null){
            this.itemClickListener.enviarEmailClicked();
        }
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
}
