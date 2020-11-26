package br.com.mgpapelaria.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;

import br.com.mgpapelaria.R;
import br.com.mgpapelaria.util.SharedPreferencesHelper;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

public class OptionsActivity extends AppCompatActivity {
    @BindView(R.id.base_url_desenvolvimento_edit_text)
    EditText baseUrlDesenvolvimentoEditText;
    @BindView(R.id.base_url_producao_edit_text)
    EditText baseUrlProducaoEditText;
    @BindView(R.id.lista_vendas_abertas_edit_text)
    EditText listaVendasAbertasEditText;
    @BindView(R.id.atualiza_pedido_edit_text)
    EditText atualizaPedidoEditText;
    private boolean baseUrlAuthProducao = true;
    @BindView(R.id.radio_producao)
    RadioButton radioProducao;
    @BindView(R.id.radio_desenvolvimento)
    RadioButton radioDesenvolvimento;
    @BindView(R.id.auth_preview_edit_text)
    EditText authPreviewEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Configurações");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String baseUrlDefault = SharedPreferencesHelper.getBaseUrlDefault(this);
        String baseUrlProducao = SharedPreferencesHelper.getBaseUrl(this, true);
        String baseUrlDesenvolvimento = SharedPreferencesHelper.getBaseUrl(this, false);

        this.baseUrlProducaoEditText.setText(baseUrlProducao);
        this.baseUrlDesenvolvimentoEditText.setText(baseUrlDesenvolvimento);
        this.listaVendasAbertasEditText.setText(SharedPreferencesHelper.getBaseUrlListVendasAbertas(this));
        this.atualizaPedidoEditText.setText(SharedPreferencesHelper.getBaseUrlUpdateOrder(this));
        this.baseUrlAuthProducao = baseUrlDefault.equals(baseUrlProducao);

        if(baseUrlAuthProducao){
            radioProducao.setChecked(true);
            radioDesenvolvimento.setChecked(false);
        }else{
            radioProducao.setChecked(false);
            radioDesenvolvimento.setChecked(true);
        }

        this.updateAuthPreview(baseUrlAuthProducao ? baseUrlProducao : baseUrlDesenvolvimento);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @OnClick({R.id.copy_to_atualiza_pedido_button, R.id.copy_to_lista_vendas_button})
    void onCopyButton(ImageButton copyButton){
        boolean atualizaInput = true;
        switch (copyButton.getId()){
            case R.id.copy_to_atualiza_pedido_button:
                atualizaInput = true;
                break;
            case R.id.copy_to_lista_vendas_button:
                atualizaInput = false;
                break;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        boolean finalAtualizaInput = atualizaInput;

        String[] lista = {"Copiar da produção", "Copiar do desenvolvimento"};
        builder.setItems(lista, (dialog, item) -> {
            String baseUrl = "";

            switch (item) {
                case 0: // producao
                    baseUrl = baseUrlProducaoEditText.getText().toString();
                    break;
                case 1: // desenvolvimento
                    baseUrl = baseUrlDesenvolvimentoEditText.getText().toString();
                    break;
            }

            if(finalAtualizaInput){
                atualizaPedidoEditText.setText(baseUrl + "lio/order");
            }else{
                listaVendasAbertasEditText.setText(baseUrl + "lio/vendas-abertas");
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @OnClick({R.id.radio_desenvolvimento, R.id.radio_producao})
    void onRadioSelected(RadioButton radioButton) {
        switch (radioButton.getId()){
            case R.id.radio_producao:
                this.baseUrlAuthProducao = true;
                break;
            case R.id.radio_desenvolvimento:
                this.baseUrlAuthProducao = false;
                break;
        }

        String baseUrl = "";
        if(baseUrlAuthProducao){
            baseUrl = this.baseUrlProducaoEditText.getText().toString().trim();
        }else{
            baseUrl = this.baseUrlDesenvolvimentoEditText.getText().toString().trim();
        }
        this.updateAuthPreview(baseUrl);
    }

    private void updateAuthPreview(String baseUrl){
        StringBuilder sb = new StringBuilder();
        sb.append(baseUrl).append("auth/login").append("\n");
        sb.append(baseUrl).append("auth/logout").append("\n");
        sb.append(baseUrl).append("auth/refresh").append("\n");
        sb.append(baseUrl).append("auth/user");

        this.authPreviewEditText.setText(sb.toString());
    }

    @OnClick(R.id.salvar_button)
    void salvar(){
        SharedPreferencesHelper.setBaseUrlProducao(this, this.baseUrlProducaoEditText.getText().toString().trim());
        SharedPreferencesHelper.setBaseUrlDesenvolvimento(this, this.baseUrlDesenvolvimentoEditText.getText().toString().trim());
        SharedPreferencesHelper.setBaseUrlDefault(this, SharedPreferencesHelper.getBaseUrl(this, this.baseUrlAuthProducao));
        SharedPreferencesHelper.setBaseUrlListVendasAbertas(this, this.listaVendasAbertasEditText.getText().toString().trim());
        SharedPreferencesHelper.setBaseUrlUpdateOrder(this, this.atualizaPedidoEditText.getText().toString().trim());

        finish();
    }

    @OnClick(R.id.restaurar_button)
    void restaurar(){
        this.baseUrlProducaoEditText.setText("http://api.mgspa.mgpapelaria.com.br/api/v1/");
        this.baseUrlDesenvolvimentoEditText.setText("http://192.168.1.198:91/api/v1/");
        this.listaVendasAbertasEditText.setText("http://api.mgspa.mgpapelaria.com.br/api/v1/lio/vendas-abertas");
        this.atualizaPedidoEditText.setText("http://api.mgspa.mgpapelaria.com.br/api/v1/lio/order");
        this.baseUrlAuthProducao = true;
        this.radioProducao.setChecked(true);
        this.radioDesenvolvimento.setChecked(false);
        this.updateAuthPreview("http://api.mgspa.mgpapelaria.com.br/api/v1/");
    }
}