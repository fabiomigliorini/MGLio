package br.com.mgpapelaria.dao;

import androidx.room.Dao;
import androidx.room.Insert;

import br.com.mgpapelaria.model.Pagamento;
import br.com.mgpapelaria.model.Pedido;

@Dao
public interface PagamentoDAO {
    @Insert
    void insertPagamento(Pagamento pagamento);
}
