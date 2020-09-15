package br.com.mgpapelaria.adapter;

import android.graphics.Color;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.widget.ImageViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import br.com.mgpapelaria.R;
import br.com.mgpapelaria.model.Pedido;
import cielo.orders.domain.Order;
import cielo.orders.domain.Status;

public class TransacoesAdapter extends RecyclerView.Adapter<TransacoesAdapter.ViewHolder> {
private List<Pedido> transacoes;
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
    public ImageView noSyncImageView;

    public ViewHolder(View v) {
        super(v);
        descricaoTextView = v.findViewById(R.id.descricao_text_view);
        dataTextView = v.findViewById(R.id.data_text_view);
        valorTextView = v.findViewById(R.id.valor_text_view);
        statusTextView = v.findViewById(R.id.status_text_view);
        noSyncImageView = v.findViewById(R.id.no_sync_iamge_view);

        v.setOnClickListener(view -> {
            if(clickListener != null){
                clickListener.onClickListener(view, getLayoutPosition());
            }
        });
    }
}

    public TransacoesAdapter(List<Pedido> transacoes) {
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
        Pedido transacao = this.transacoes.get(position);
        holder.descricaoTextView.setText(transacao.nome.trim());
        holder.dataTextView.setText(DateFormat.format("dd/MM/yyyy HH:mm", transacao.data));
        holder.valorTextView.setText(nf.format(new BigDecimal(transacao.valor).divide(new BigDecimal(100))));
        if(transacao.status.equals("CANCELED")){
            holder.statusTextView.setText("CANCELADO");
            holder.statusTextView.setTextColor(Color.RED);
        }else{
            holder.statusTextView.setText("");
        }
        if(transacao.sincronizado){
            holder.noSyncImageView.setVisibility(View.GONE);
        }else{
            holder.noSyncImageView.setVisibility(View.VISIBLE);
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

    public void adicionaTransacao(Pedido transacao) {
        this.transacoes.add(transacao);
        notifyItemInserted(getItemCount());
    }

    public void adicionaTransacoes(List<Pedido> transacoes) {
        this.transacoes = transacoes;
        notifyItemRangeInserted(0, getItemCount());
    }

    public List<Pedido> getTransacoes(){
        return this.transacoes;
    }

    public void setOnItemClickedListenr(ItemClickListener listenr){
        clickListener = listenr;
    }
}