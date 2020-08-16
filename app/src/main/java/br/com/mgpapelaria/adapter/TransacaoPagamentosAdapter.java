package br.com.mgpapelaria.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

import br.com.mgpapelaria.R;
import cielo.sdk.order.payment.Payment;

public class TransacaoPagamentosAdapter  extends RecyclerView.Adapter<TransacaoPagamentosAdapter.ViewHolder> {
    private List<Payment> pagamentos;
    private static ItemClickListener clickListener;
    private NumberFormat nf = DecimalFormat.getCurrencyInstance();

    public interface ItemClickListener {
        void onClickListener(View view, int position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView descricaoTextView;

        public ViewHolder(View v) {
            super(v);
            descricaoTextView = v.findViewById(R.id.descricao_text_view);

            v.setOnClickListener(view -> {
                if(clickListener != null){
                    clickListener.onClickListener(view, getLayoutPosition());
                }
            });
        }
    }

    public TransacaoPagamentosAdapter(List<Payment> pagamentos) {
        this.pagamentos = pagamentos;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.transacao_payment_list_item, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Payment pagamento = this.pagamentos.get(position);
        String product = pagamento.getPaymentFields().get("primary_product_name") + " - " + pagamento.getPaymentFields().get("secondary_product_name");
        holder.descricaoTextView.setText(product);
    }

    @Override
    public int getItemCount() {
        return this.pagamentos.size();
    }

    public void setOnItemClickedListenr(ItemClickListener listenr){
        clickListener = listenr;
    }
}
