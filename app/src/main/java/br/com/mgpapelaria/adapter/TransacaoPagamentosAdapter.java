package br.com.mgpapelaria.adapter;

import android.graphics.Color;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.com.mgpapelaria.R;
import br.com.mgpapelaria.model.Pagamento;
import cielo.sdk.order.payment.Payment;

public class TransacaoPagamentosAdapter  extends RecyclerView.Adapter<TransacaoPagamentosAdapter.ViewHolder> {
    private List<Payment> payments;
    private List<Pagamento> pagamentos;
    private static ItemClickListener clickListener;

    public interface ItemClickListener {
        void onClickListener(View view, int position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView statusTextView;
        public TextView usuarioTextView;
        public TextView dataTextView;

        public ViewHolder(View v) {
            super(v);
            statusTextView = v.findViewById(R.id.status_text_view);
            usuarioTextView = v.findViewById(R.id.usuario_text_view);
            dataTextView = v.findViewById(R.id.data_text_view);

            v.setOnClickListener(view -> {
                if(clickListener != null){
                    clickListener.onClickListener(view, getLayoutPosition());
                }
            });
        }
    }

    public TransacaoPagamentosAdapter(List<Payment> payments, List<Pagamento> pagamentos) {
        this.payments = payments;
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
        Payment pagamento = this.payments.get(position);
        this.setStatus(holder.statusTextView, pagamento.getPaymentFields().get("v40Code"));
        String usuario = "";
        if(this.pagamentos.size() > position){
            usuario = this.pagamentos.get(position).userName;
        }
        holder.usuarioTextView.setText(usuario);
        long requestDate = Long.parseLong(pagamento.getRequestDate());
        holder.dataTextView.setText(DateFormat.format("dd/MM/yyyy HH:mm:ss", requestDate));
    }

    @Override
    public int getItemCount() {
        return this.payments.size();
    }

    private void setStatus(TextView textView, String code){
        if(code.equals("28")){
            textView.setTextColor(Color.parseColor("#ffff4444"));
            textView.setText("CANCELAMENTO");
        }else{
            textView.setTextColor(Color.parseColor("#388E3C"));
            textView.setText("APROVAÇÃO");
        }
    }

    public void setOnItemClickedListenr(ItemClickListener listenr){
        clickListener = listenr;
    }
}
