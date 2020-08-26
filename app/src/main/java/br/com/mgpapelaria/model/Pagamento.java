package br.com.mgpapelaria.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class Pagamento implements Serializable {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public int pedidoId;
    @NonNull
    public String paymentId = "";
    public int userId;
    @NonNull
    public String userName = "";
}
