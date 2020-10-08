package br.com.mgpapelaria.api;

import java.util.List;

import br.com.mgpapelaria.model.LoginRequest;
import br.com.mgpapelaria.model.LoginResponse;
import br.com.mgpapelaria.model.OrderRequest;
import br.com.mgpapelaria.model.UsuarioResponse;
import br.com.mgpapelaria.model.VendaAberta;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface ApiService {
    @Headers({"Accept:application/json", "Content-Type:application/json;"})
    @GET()
    Call<List<VendaAberta>> getVendasAbertas(@Url String url, @Query("cnpj") String cnpj, @Query("terminal") String terminal);

    @Headers({"Accept:application/json"})
    @POST()
    Call<Void> updateOrder(@Url String url, @Body OrderRequest order);

    @Headers({"Accept:application/json"})
    @POST("auth/login")
    Call<LoginResponse> login(@Body LoginRequest login);

    @Headers({"Accept:application/json"})
    @GET("auth/logout")
    Call<Void> logout();

    @GET("auth/refresh")
    Call<ResponseBody> refreshToken();

    @Headers({"Accept:application/json"})
    @GET("auth/user")
    Call<UsuarioResponse> getUser();
}
