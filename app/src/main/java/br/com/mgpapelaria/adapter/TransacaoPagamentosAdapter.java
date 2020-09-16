package br.com.mgpapelaria.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

import br.com.mgpapelaria.R;
import cielo.sdk.order.payment.Payment;

public class TransacaoPagamentosAdapter  extends RecyclerView.Adapter<TransacaoPagamentosAdapter.ViewHolder> {
    private List<Payment> pagamentos;
    private static ItemClickListener clickListener;
    private NumberFormat nf = DecimalFormat.getCurrencyInstance();
    private Context context;

    public interface ItemClickListener {
        void onClickListener(View view, int position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView descricaoTextView;
        public TextView dataTextView;
        public TextView valorTextView;
        public TextView statusTextView;
        public AppCompatImageView brandImageView;

        public ViewHolder(View v) {
            super(v);
            descricaoTextView = v.findViewById(R.id.descricao_text_view);
            dataTextView = v.findViewById(R.id.data_text_view);
            valorTextView = v.findViewById(R.id.valor_text_view);
            statusTextView = v.findViewById(R.id.status_text_view);
            brandImageView = v.findViewById(R.id.brand_imageView);

            v.setOnClickListener(view -> {
                if(clickListener != null){
                    clickListener.onClickListener(view, getLayoutPosition());
                }
            });
        }
    }

    public TransacaoPagamentosAdapter(List<Payment> pagamentos, Context context) {
        this.pagamentos = pagamentos;
        this.context = context;
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
        String product = pagamento.getPaymentFields().get("productName");
        holder.descricaoTextView.setText(product);
        holder.dataTextView.setText(DateFormat.format("dd/MM/yyyy HH:mm", Long.valueOf(pagamento.getRequestDate())));
        holder.valorTextView.setText(nf.format(new BigDecimal(pagamento.getAmount()).divide(new BigDecimal(100))));
        this.setStatus(holder.statusTextView, pagamento.getPaymentFields().get("v40Code"));
        holder.brandImageView.setImageDrawable(getBrandImage(pagamento.getBrand()));
    }

    @Override
    public int getItemCount() {
        return this.pagamentos.size();
    }

    private void setStatus(TextView textView, String code){
        if(code.equals("28")){
            textView.setTextColor(Color.RED);
            textView.setText("CANCELAMENTO");
        }else{
            textView.setTextColor(Color.GREEN);
            textView.setText("");
        }
    }

    private Drawable getBrandImage(String brand){
        switch (brand.toUpperCase()){
            case "MASTERCARD":
                return this.context.getDrawable(R.drawable.ic_mastercard_40);
            case "VISA":
                return this.context.getDrawable(R.drawable.ic_visa_40);
            case "ELO":
                return this.context.getDrawable(R.drawable.ic_elo_40);
            default:
                FirebaseCrashlytics.getInstance().log("Bandeira não definida: " + brand);
                return this.context.getDrawable(R.drawable.ic_credit_card_40);
        }
    }

    public void setOnItemClickedListenr(ItemClickListener listenr){
        clickListener = listenr;
    }
}
