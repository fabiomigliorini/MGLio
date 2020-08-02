package br.com.mgpapelaria.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import br.com.mgpapelaria.R;
import br.com.mgpapelaria.model.VendaRegistrada;

public class VendasRegistradasAdapter extends RecyclerView.Adapter<VendasRegistradasAdapter.ViewHolder> {
    private List<VendaRegistrada> vendas;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView descricaoTextView;
        public TextView valorTextView;
        public TextView dataCriacaoTextView;
        public ViewHolder(View v) {
            super(v);
            descricaoTextView = v.findViewById(R.id.descricao_text_view);
            valorTextView = v.findViewById(R.id.valor_text_view);
            dataCriacaoTextView = v.findViewById(R.id.data_criacao_text_view);
        }
    }

    public VendasRegistradasAdapter(List<VendaRegistrada> vendas) {
        this.vendas = vendas;
    }

    @Override
    public VendasRegistradasAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.venda_registrada_item_list_view, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        VendaRegistrada venda = this.vendas.get(position);
        holder.descricaoTextView.setText(venda.getDescricao());
        holder.valorTextView.setText(String.valueOf(venda.getValor().doubleValue()));
        holder.dataCriacaoTextView.setText(venda.getDataCriacao().toString());
    }

    @Override
    public int getItemCount() {
        return this.vendas.size();
    }

    public void clearItens(){
        int total = this.vendas.size();
        this.vendas = new ArrayList<>();
        notifyItemRangeRemoved(0, total);
    }

    public void insertItem(VendaRegistrada venda) {
        this.vendas.add(venda);
        notifyItemInserted(getItemCount());
    }
}
