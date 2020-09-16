package br.com.mgpapelaria.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import br.com.mgpapelaria.R;
import br.com.mgpapelaria.model.Pagamento;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cielo.sdk.order.payment.Payment;

public class TransacaoBottomSheetFragment extends BottomSheetDialogFragment {
    public static String PAGAMENTO = "pagamento";
    public static String USUARIO = "usuario";
    private ItemClickListener itemClickListener;
    @BindView(R.id.card_label_text_view)
    TextView cardLabelTextView;
    @BindView(R.id.mask_text_view)
    TextView maskTextView;
    @BindView(R.id.usuario_text_view)
    TextView usuarioTextView;

    public interface ItemClickListener{
        void imprimirItemClicked(boolean viaCliente);
        //void enviarEmailClicked();
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
        Bundle bundle = getArguments();
        if(bundle != null){
            String usuario = bundle.getString(USUARIO);
            Payment payment = (Payment) bundle.getSerializable(PAGAMENTO);

            cardLabelTextView.setText(payment.getPaymentFields().get("cardLabelApplication"));
            String mask = payment.getMask();
            if(mask.equals("mock_mask")){
                mask = "123456-7890";
            }
            String maskFormatted = mask.substring(0, 4) + "   " + mask.substring(4,6) + "**   ****   " + mask.substring(7, 11);
            maskTextView.setText(maskFormatted);
            usuarioTextView.setText(usuario);
        }
        return view;
    }

    @OnClick(R.id.imprimir_2_via__estabelecimento_button)
    void onImprimir2ViaEstabelecimentoItemClicked(){
        if(this.itemClickListener != null){
            this.itemClickListener.imprimirItemClicked(false);
        }
    }

    @OnClick(R.id.imprimir_2_via__estabelecimento_button)
    void onImprimir2ViaClienteItemClicked(){
        if(this.itemClickListener != null){
            this.itemClickListener.imprimirItemClicked(true);
        }
    }

    /*@OnClick(R.id.enviar_email_button)
    void onEnviarEmailItemClicked(){
        if(this.itemClickListener != null){
            this.itemClickListener.enviarEmailClicked();
        }
    }*/

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
}
