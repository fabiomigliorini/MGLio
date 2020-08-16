package br.com.mgpapelaria.adapter;

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

public class TransacoesAdapter extends RecyclerView.Adapter<TransacoesAdapter.ViewHolder> {
private List<Order> transacoes;
private static ItemClickListener clickListener;
private NumberFormat nf = DecimalFormat.getCurrencyInstance();

public interface ItemClickListener {
    void onClickListener(View view, int position);
}

public static class ViewHolder extends RecyclerView.ViewHolder {
    public TextView descricaoTextView;
    public TextView valorTextView;
    public TextView dataCriacaoTextView;
    public ViewHolder(View v) {
        super(v);
        descricaoTextView = v.findViewById(R.id.descricao_text_view);
        valorTextView = v.findViewById(R.id.valor_text_view);
        dataCriacaoTextView = v.findViewById(R.id.data_criacao_text_view);

        v.setOnClickListener(view -> {
            if(clickListener != null){
                clickListener.onClickListener(view, getLayoutPosition());
            }
        });
    }
}

    public TransacoesAdapter(List<Order> transacoes) {
        this.transacoes = transacoes;
        //this.nf.setMinimumFractionDigits(2);
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
        holder.descricaoTextView.setText(transacao.getReference());
        holder.valorTextView.setText(nf.format(new BigDecimal(transacao.getPrice()).divide(new BigDecimal(100))));
        holder.dataCriacaoTextView.setText(DateFormat.format("dd/MM/yyyy HH:mm", transacao.getCreatedAt()));
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