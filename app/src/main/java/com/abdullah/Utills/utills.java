package com.abdullah.Utills;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class utills {
    public static byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
        return outputStream.toByteArray();
    }
    public static String DateFormate(Long time){
        Date date = new Date(time);
        String stringDate = DateFormat.getDateInstance().format(date);
        return stringDate;
    }

    public static String convertSecondsToHMmSs(long milisec) {
        long seconds=milisec/1000;
        long s = seconds % 60;
        long m = (seconds / 60) % 60;
        long h = (seconds / (60 * 60)) % 24;
        return String.format("%d:%02d:%02d", h,m,s);
    }

    public static String TimeFormat(Long time){
        Date date = new Date(time);
        String stringDate = DateFormat.getTimeInstance().format(date);
        return stringDate;
    }

    public static Bitmap getImage(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }


    public static void snak(View view,String msg){
        Snackbar.make(view,msg,Snackbar.LENGTH_SHORT).show();
    }


    ///this function filter the array lsit according to search query
    public static ArrayList<HashMap<String,Object>> getFilter( ArrayList<HashMap<String,Object>> books,String q){
        ArrayList<HashMap<String,Object>> books_filtered=new ArrayList<>();
        if(q.isEmpty()){
            return books;
        }
        for ( HashMap<String,Object>book:books) {
            if(book.get("title").toString().toLowerCase().contains(q.toLowerCase())){
                books_filtered.add(book);
            }
        }

        return books_filtered;
    }
}
