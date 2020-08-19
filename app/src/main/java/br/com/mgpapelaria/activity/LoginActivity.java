package br.com.mgpapelaria.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import br.com.mgpapelaria.R;
import br.com.mgpapelaria.api.ApiService;
import br.com.mgpapelaria.api.RetrofitUtil;
import br.com.mgpapelaria.model.LoginRequest;
import br.com.mgpapelaria.model.LoginResponse;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    @BindView(R.id.usuario_text_field)
    TextInputLayout usuarioInputLayout;
    @BindView(R.id.usuario_edit_text)
    TextInputEditText usuarioEditText;
    @BindView(R.id.senha_text_field)
    TextInputLayout senhaInputLayout;
    @BindView(R.id.senha_edit_text)
    TextInputEditText senhaEditText;
    private ApiService apiService;
    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        this.sharedPref = getSharedPreferences("MG_Pref", Context.MODE_PRIVATE);
        String token = this.sharedPref.getString("token", null);
        if(token != null){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        this.apiService = RetrofitUtil.createService(this, ApiService.class);
    }

    @OnClick(R.id.entrar_button)
    void login(){
        if(!this.isFormValid()){
            return;
        }

        this.apiService.login(new LoginRequest(
                this.usuarioEditText.getText().toString(),
                this.senhaEditText.getText().toString()))
            .enqueue(new Callback<LoginResponse>() {
                @Override
                public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                    if(response.code() == 200){
                        String token = response.body().getToken();
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString("token", token);
                        editor.apply();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }else if(response.code() == 401){
                        showErrorDialog("Usuário e/ou senha inválidos.");
                    }else{
                        showErrorDialog("Ocorreu um erro ao processar a requisição.");
                    }
                }

                @Override
                public void onFailure(Call<LoginResponse> call, Throwable t) {
                    showErrorDialog("Ocorreu um erro ao processar a requisição.");
                }
            });

    }

    private void showErrorDialog(String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setTitle("Ops!");
        builder.setPositiveButton("Ok", (dialog, which) -> {
            dialog.dismiss();
        });
        builder.create().show();
    }

    private boolean isFormValid(){
        boolean valid = true;
        if(usuarioEditText.getText().toString().isEmpty()){
            valid = false;
            usuarioInputLayout.setError("Campo obrigatório");
        }else{
            usuarioInputLayout.setError(null);
        }

        if(senhaEditText.getText().toString().isEmpty()){
            valid = false;
            senhaInputLayout.setError("Campo obrigatório");
        }else{
            senhaInputLayout.setError(null);
        }

        return valid;
    }
}