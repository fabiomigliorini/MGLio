package br.com.mgpapelaria.adapter;

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import br.com.mgpapelaria.R;
import br.com.mgpapelaria.model.VendaAberta;

public class VendasAbertasAdapter extends RecyclerView.Adapter<VendasAbertasAdapter.ViewHolder> {
    private List<VendaAberta> vendas;
    private static ItemClickListener clickListener;
    private final NumberFormat nf = DecimalFormat.getCurrencyInstance();

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

    public VendasAbertasAdapter(List<VendaAberta> vendas) {
        this.vendas = vendas;
    }

    @NonNull
    @Override
    public VendasAbertasAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.venda_aberta_list_item, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        VendaAberta venda = this.vendas.get(position);
        holder.descricaoTextView.setText("Venda #" + venda.getCodNegocio().toString());
        holder.valorTextView.setText(nf.format(venda.getValorSaldo()));
        holder.dataCriacaoTextView.setText(DateFormat.format("dd/MM/yyyy HH:mm", venda.getLancamento()));
    }

    @Override
    public int getItemCount() {
        return this.vendas.size();
    }

    public void apagaVendas(){
        int total = this.vendas.size();
        this.vendas = new ArrayList<>();
        notifyItemRangeRemoved(0, total);
    }

    public void setVendas(List<VendaAberta> vendas){
        this.vendas = vendas;
        notifyItemRangeInserted(0, this.getItemCount());
    }

    public List<VendaAberta> getVendas(){
        return this.vendas;
    }

    public void setOnItemClickedListenr(ItemClickListener listenr){
        clickListener = listenr;
    }
}
