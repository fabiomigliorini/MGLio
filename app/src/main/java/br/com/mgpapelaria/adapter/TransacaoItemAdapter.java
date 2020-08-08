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
import cielo.orders.domain.Item;

public class TransacaoItemAdapter extends RecyclerView.Adapter<TransacaoItemAdapter.ViewHolder> {
    private List<Item> items;
    private static ItemClickListener clickListener;
    private NumberFormat nf = DecimalFormat.getCurrencyInstance();

    public interface ItemClickListener {
        void onClickListener(View view, int position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView idTextView;
        public TextView skuTextView;
        public TextView nameTextView;
        public TextView unitPriceTextView;
        public TextView quantityTextView;
        public TextView unitOfMeasureTextView;
        public TextView descriptionTextView;
        public TextView detailsTextView;
        public TextView referenceTextView;
        public TextView amountTextView;

        public ViewHolder(View v) {
            super(v);
            idTextView = v.findViewById(R.id.idTextView);
            skuTextView = v.findViewById(R.id.skuTextView);
            nameTextView = v.findViewById(R.id.nameTextView);
            unitPriceTextView = v.findViewById(R.id.unitPriceTextView);
            quantityTextView = v.findViewById(R.id.quantityTextView);
            unitOfMeasureTextView = v.findViewById(R.id.unitOfMeasureTextView);
            descriptionTextView = v.findViewById(R.id.descriptionTextView);
            detailsTextView = v.findViewById(R.id.detailsTextView);
            referenceTextView = v.findViewById(R.id.referenceTextView);
            amountTextView = v.findViewById(R.id.amountTextView);


            v.setOnClickListener(view -> {
                if(clickListener != null){
                    clickListener.onClickListener(view, getLayoutPosition());
                }
            });
        }
    }

    public TransacaoItemAdapter(List<Item> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public TransacaoItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.transacao_item_list_item, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(TransacaoItemAdapter.ViewHolder holder, int position) {
        Item item = this.items.get(position);
        holder.idTextView.setText(item.getId());
        holder.skuTextView.setText(item.getSku());
        holder.nameTextView.setText(item.getName());
        holder.unitPriceTextView.setText(String.valueOf(item.getUnitPrice()));
        holder.quantityTextView.setText(String.valueOf(item.getQuantity()));
        holder.unitOfMeasureTextView.setText(item.getUnitOfMeasure());
        holder.descriptionTextView.setText(item.getDescription());
        holder.detailsTextView.setText(item.getDetails());
        holder.referenceTextView.setText(item.getReference());
        holder.amountTextView.setText(String.valueOf(item.getAmount()));
        //holder.valorTextView.setText(nf.format(new BigDecimal(item.getPaidAmount()).divide(new BigDecimal(100))));
        //holder.dataCriacaoTextView.setText(DateFormat.format("dd/MM/yyyy HH:mm", transacao.getCreatedAt()));
    }

    @Override
    public int getItemCount() {
        return this.items.size();
    }

    public void setOnItemClickedListenr(ItemClickListener listenr){
        clickListener = listenr;
    }
}