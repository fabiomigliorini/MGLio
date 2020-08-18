package br.com.mgpapelaria.adapter;

import android.graphics.Color;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import br.com.mgpapelaria.R;
import cielo.orders.domain.Order;
import cielo.orders.domain.Status;

public class TransacoesAdapter extends RecyclerView.Adapter<TransacoesAdapter.ViewHolder> {
private List<Order> transacoes;
private static ItemClickListener clickListener;
private NumberFormat nf = DecimalFormat.getCurrencyInstance();

public interface ItemClickListener {
    void onClickListener(View view, int position);
}

public static class ViewHolder extends RecyclerView.ViewHolder {
    public TextView descricaoTextView;
    public TextView dataTextView;
    public TextView valorTextView;
    public TextView statusTextView;
    public ViewHolder(View v) {
        super(v);
        descricaoTextView = v.findViewById(R.id.descricao_text_view);
        dataTextView = v.findViewById(R.id.data_text_view);
        valorTextView = v.findViewById(R.id.valor_text_view);
        statusTextView = v.findViewById(R.id.status_text_view);

        v.setOnClickListener(view -> {
            if(clickListener != null){
                clickListener.onClickListener(view, getLayoutPosition());
            }
        });
    }
}

    public TransacoesAdapter(List<Order> transacoes) {
        this.transacoes = transacoes;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.transacao_list_item, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Order transacao = this.transacoes.get(position);
        holder.descricaoTextView.setText(transacao.getPayments().get(0).getPaymentFields().get("clientName"));
        holder.dataTextView.setText(DateFormat.format("dd/MM/yyyy HH:mm", transacao.getCreatedAt()));
        holder.valorTextView.setText(nf.format(new BigDecimal(transacao.getPrice()).divide(new BigDecimal(100))));
        if(transacao.getStatus() == Status.CANCELED){
            holder.statusTextView.setText("CANCELADO");
            holder.statusTextView.setTextColor(Color.RED);
        }else{
            holder.statusTextView.setText("");
        }
    }

    @Override
    public int getItemCount() {
        return this.transacoes.size();
    }

    public void apagaTransacoes(){
        int total = this.transacoes.size();
        this.transacoes = new ArrayList<>();
        notifyItemRangeRemoved(0, total);
    }

    public void adicionaTransacao(Order transacao) {
        this.transacoes.add(transacao);
        notifyItemInserted(getItemCount());
    }

    public void adicionaTransacoes(List<Order> transacoes) {
        this.transacoes = transacoes;
        notifyItemRangeInserted(0, getItemCount());
    }

    public List<Order> getTransacoes(){
        return this.transacoes;
    }

    public void setOnItemClickedListenr(ItemClickListener listenr){
        clickListener = listenr;
    }
}