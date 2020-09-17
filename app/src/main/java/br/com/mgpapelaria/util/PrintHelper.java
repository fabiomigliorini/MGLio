package br.com.mgpapelaria.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cielo.orders.domain.PrinterAttributes;
import cielo.sdk.order.PrinterListener;
import cielo.sdk.printer.PrinterManager;

public class PrintHelper {
    public static HashMap<String, Integer> getLeftStyle(){
        return getStyle(PrinterAttributes.VAL_ALIGN_LEFT, false);
    }

    public static HashMap<String, Integer> getLeftStyle(boolean bold){
        return getStyle(PrinterAttributes.VAL_ALIGN_LEFT, bold);
    }

    public static HashMap<String, Integer> getCenterStyle(){
        return getStyle(PrinterAttributes.VAL_ALIGN_CENTER, false);
    }

    public static HashMap<String, Integer> getCenterStyle(boolean bold){
        return getStyle(PrinterAttributes.VAL_ALIGN_CENTER, bold);
    }

    public static HashMap<String, Integer> getRightStyle(){
        return getStyle(PrinterAttributes.VAL_ALIGN_RIGHT, false);
    }

    public static HashMap<String, Integer> getRightStyle(boolean bold){
        return getStyle(PrinterAttributes.VAL_ALIGN_RIGHT, bold);
    }

    public static List<Map<String, Integer>> getColumnStyle(boolean bold){
        return getColumnStyle(bold, true, true, true);
    }

    public static List<Map<String, Integer>> getColumnStyle(){
        return getColumnStyle(false, true, true, true);
    }

    public static List<Map<String, Integer>> getColumnStyle(boolean left, boolean center, boolean right){
        return getColumnStyle(false, true, true, true);
    }

    public static List<Map<String, Integer>> getColumnStyle(boolean bold, boolean left, boolean center, boolean right){
        List<Map<String, Integer>> style =  new ArrayList<>();
        if(left){
            style.add(getLeftStyle(bold));
        }
        if(center){
            style.add(getCenterStyle(bold));
        }
        if(right){
            style.add(getRightStyle(bold));
        }

        return style;
    }

    public static HashMap<String, Integer> getStyle(int align, Boolean bold){
        HashMap<String, Integer> style =  new HashMap<>();
        style.put(PrinterAttributes.KEY_ALIGN, align);
        style.put(PrinterAttributes.KEY_TYPEFACE, bold ? 1 : 0);
        style.put(PrinterAttributes.KEY_TEXT_SIZE, 17);
        return style;
    }

    public static void printTest(PrinterManager printerManager, PrinterListener printerListener){
        for(int i = 0; i < 9; i++){
            HashMap<String, Integer> tf =  new HashMap<>();
            tf.put(PrinterAttributes.KEY_ALIGN, PrinterAttributes.VAL_ALIGN_LEFT);
            tf.put(PrinterAttributes.KEY_TYPEFACE, i);
            tf.put(PrinterAttributes.KEY_TEXT_SIZE, 17);

            printerManager.printText("TypeFace " + i, tf, printerListener);
        }
    }

    public static String formatCNPJ(String cnpj){
        if(cnpj == null){
            cnpj = "04576775000242";
        }
        return cnpj.replaceAll("([0-9]{2})([0-9]{3})([0-9]{3})([0-9]{4})([0-9]{2})","$1\\.$2\\.$3/$4-$5");
    }

    public static String formatDateTime(String date){
        SimpleDateFormat sdf = (SimpleDateFormat) SimpleDateFormat.getInstance();
        sdf.applyPattern("dd/MM/yy  HH:mm");
        return sdf.format(Long.valueOf(date));
    }

    public static String formatDate(String date){
        SimpleDateFormat sdf = (SimpleDateFormat) SimpleDateFormat.getInstance();
        sdf.applyPattern("dd/MM/yy");
        return sdf.format(Long.valueOf(date));
    }

    public static String formatTime(String date){
        SimpleDateFormat sdf = (SimpleDateFormat) SimpleDateFormat.getInstance();
        sdf.applyPattern("HH:mm");
        return sdf.format(Long.valueOf(date));
    }

    public static String formatValor(Long valor){
        BigDecimal valorDecimal = new BigDecimal(valor).divide(new BigDecimal("100"));
        NumberFormat nf = DecimalFormat.getInstance();
        nf.setMinimumFractionDigits(2);
        return nf.format(valorDecimal);
    }
}
