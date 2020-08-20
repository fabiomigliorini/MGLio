package br.com.mgpapelaria.database;

import androidx.room.TypeConverter;

import com.google.gson.Gson;

import java.util.Date;

import cielo.orders.domain.Order;

public class Converters {
    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }

    @TypeConverter
    public static Order fromString(String value) {
        return value == null ? null : new Gson().fromJson(value, Order.class);
    }

    @TypeConverter
    public static String orderToString(Order order) {
        return order == null ? null : new Gson().toJson(order);
    }
}
