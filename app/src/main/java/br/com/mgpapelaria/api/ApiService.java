package br.com.mgpapelaria.api;

import java.util.List;

import br.com.mgpapelaria.model.Filial;
import br.com.mgpapelaria.model.LoginRequest;
import br.com.mgpapelaria.model.LoginResponse;
import br.com.mgpapelaria.model.OrderRequest;
import br.com.mgpapelaria.model.VendaAberta;
import cielo.orders.domain.Order;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {
    @Headers({"Accept:application/json", "Content-Type:application/json;"})
    @GET("lio/vendas-abertas")
    Call<List<VendaAberta>> getVendasAbertas(@Query("cnpj") String cnpj, @Query("terminal") String terminal);

    @Headers({"Accept:application/json"})
    @POST("lio/order")
    Call<Void> updateOrder(@Body OrderRequest order);

    @Headers({"Accept:application/json"})
    @GET("select/filial")
    Call<List<Filial>> selectFilial();

    @Headers({"Accept:application/json"})
    @POST("auth/login")
    Call<LoginResponse> login(@Body LoginRequest login);

    @Headers({"Accept:application/json"})
    @GET("auth/logout")
    Call<Void> logout();

    @Headers({"Accept:application/json"})
    @GET("auth/refresh")
    Call<String> refreshToken();
}
