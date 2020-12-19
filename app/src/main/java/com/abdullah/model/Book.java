package com.abdullah.model;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import com.abdullah.database.SQL;
import com.abdullah.interfaces.OnInsertBookListiner;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.HashMap;

public class Book {

    String title,secription,author,gener,isbn10,isb13,cover;
    int rating=0;
    int pages;
    int rating_count;

    public Book(String title, String secription, String author, String gener, String isbn10, String isb13, String cover, int rating, int pages, int rating_count) {
        this.title = title;
        this.secription = secription;
        this.author = author;
        this.gener = gener;
        this.isbn10 = isbn10;
        this.isb13 = isb13;
        this.cover = cover;
        this.rating = rating;
        this.pages = pages;
        this.rating_count = rating_count;
    }

    public void save_to_collection(final SQL db, final OnInsertBookListiner listiner){


        Picasso.get().load(cover).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                HashMap<String, Object> image_item = new HashMap<>();
                image_item.put(SQL.title, title);
                image_item.put(SQL.description, secription);
                image_item.put(SQL.author, author);
                image_item.put(SQL.cover_page, bitmap);
                image_item.put(SQL.book_genre, gener);
                image_item.put(SQL.pages, pages);
                if(db.put_image(image_item)){
                    listiner.sucess();
                }else {

                }

            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        });

    }
    public String getTitle() {
        return title;
    }

    public String getSecription() {
        return secription;
    }

    public String getAuthor() {
        return author;
    }

    public String getGener() {
        return gener;
    }

    public String getIsbn10() {
        return isbn10;
    }

    public String getIsb13() {
        return isb13;
    }

    public String getCover() {
        return cover;
    }

    public int getRating() {
        return rating;
    }

    public int getPages() {
        return pages;
    }

    public int getRating_count() {
        return rating_count;
    }
}
