package br.com.mgpapelaria.api;

import java.util.List;

import br.com.mgpapelaria.model.VendaRegistrada;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    @GET("vendas-abertas")
    Call<List<VendaRegistrada>> getVendasAbertas(@Query("cnpj") String cnpj, @Query("terminal") String terminal);
}
