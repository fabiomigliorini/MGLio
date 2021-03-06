package br.com.mgpapelaria.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import br.com.mgpapelaria.R;
import br.com.mgpapelaria.api.ApiService;
import br.com.mgpapelaria.api.RetrofitUtil;
import br.com.mgpapelaria.model.LoginRequest;
import br.com.mgpapelaria.model.LoginResponse;
import br.com.mgpapelaria.model.UsuarioResponse;
import br.com.mgpapelaria.util.SharedPreferencesHelper;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private static int LOGIN_QR_REQUEST = 1000;
    @BindView(R.id.usuario_text_field)
    TextInputLayout usuarioInputLayout;
    @BindView(R.id.usuario_edit_text)
    TextInputEditText usuarioEditText;
    @BindView(R.id.senha_text_field)
    TextInputLayout senhaInputLayout;
    @BindView(R.id.senha_edit_text)
    TextInputEditText senhaEditText;
    private ApiService apiService;
    private Integer valor;

    @Override
    protected void onResume() {
        super.onResume();
        String token = SharedPreferencesHelper.getToken(this);
        if(token != null){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        if(getIntent() != null){
            Bundle bundle = getIntent().getExtras();
            if(bundle != null){
                if(bundle.getInt(PinpadActivity.VALOR, -1) != -1){
                    this.valor = bundle.getInt(PinpadActivity.VALOR);
                }
            }
        }

        this.apiService = RetrofitUtil.createService(this, ApiService.class);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() != null) {
                String token = result.getContents();
                Log.i("TOKEN", token);

                ProgressDialog mDialog = new ProgressDialog(LoginActivity.this);
                mDialog.setMessage("Aguarde...");
                mDialog.setCancelable(false);
                mDialog.show();

                getUserInfo(token, new UserInfoCallback() {
                    @Override
                    public void onResponse() {
                        SharedPreferencesHelper.setToken(LoginActivity.this, token);
                        mDialog.dismiss();
                        goToMainOrPinpadActivity();
                    }

                    @Override
                    public void onError() {
                        mDialog.dismiss();
                        showErrorDialog("Erro ao processar a requisição ou QRCode inválido.");
                    }
                });
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @OnClick(R.id.entrar_button)
    void login(){
        if(!this.isFormValid()){
            return;
        }

        ProgressDialog mDialog = new ProgressDialog(LoginActivity.this);
        mDialog.setMessage("Aguarde...");
        mDialog.setCancelable(false);
        mDialog.show();

        this.apiService.login(new LoginRequest(
                this.usuarioEditText.getText().toString(),
                this.senhaEditText.getText().toString()))
            .enqueue(new Callback<LoginResponse>() {
                @Override
                public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                    if(response.code() == 200){
                        String token = response.body().getToken();
                        Log.i("TOKEN", token);
                        SharedPreferencesHelper.setToken(LoginActivity.this, token);

                        getUserInfo(token, new UserInfoCallback() {
                            @Override
                            public void onResponse() {
                                mDialog.dismiss();
                                goToMainOrPinpadActivity();
                            }

                            @Override
                            public void onError() {
                                mDialog.dismiss();
                                showErrorDialog("Ocorreu um erro ao processar a requisição.");
                            }
                        });

                    }else if(response.code() == 401){
                        mDialog.dismiss();
                        showErrorDialog("Usuário e/ou senha inválidos.");
                    }else{
                        mDialog.dismiss();
                        showErrorDialog("Ocorreu um erro ao processar a requisição.");
                    }
                }

                @Override
                public void onFailure(Call<LoginResponse> call, Throwable t) {
                    mDialog.dismiss();
                    showErrorDialog("Ocorreu um erro ao processar a requisição.");
                }
            });

    }

    /*@OnClick(R.id.entrar_qr_button)
    public void loginQR(){
        //Intent intent = new Intent(this, LoginQRActivity.class);
        //startActivityForResult(intent, LOGIN_QR_REQUEST);
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setOrientationLocked(true);
        integrator.setPrompt("");
        integrator.initiateScan();
    }*/

    private void goToMainOrPinpadActivity(){
        Intent intent;

        if(valor != null){
            intent = new Intent(LoginActivity.this, PinpadActivity.class);
            intent.putExtra(PinpadActivity.VALOR, valor);
            intent.setAction("br.com.mgpapelaria.PINPAD");
        }else{
            intent = new Intent(LoginActivity.this, MainActivity.class);
        }
        startActivity(intent);
        LoginActivity.this.finish();
        valor = null;
    }

    public interface UserInfoCallback{
        void onResponse();
        void onError();
    }

    private void getUserInfo(String token, UserInfoCallback callback){
        ApiService apiService = RetrofitUtil.createService(LoginActivity.this, ApiService.class, token);
        apiService.getUser().enqueue(new Callback<UsuarioResponse>() {
            @Override
            public void onResponse(Call<UsuarioResponse> call, Response<UsuarioResponse> response) {
                if(response.code() == 200){
                    String usuario = response.body().getUser().getUsuario();
                    SharedPreferencesHelper.setUser(
                            LoginActivity.this,
                            usuario,
                            response.body().getUser().getId()
                    );

                    FirebaseCrashlytics.getInstance().setCustomKey("usuario", usuario);
                    callback.onResponse();
                }else{
                    callback.onError();
                }
            }

            @Override
            public void onFailure(Call<UsuarioResponse> call, Throwable t) {
                callback.onError();
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