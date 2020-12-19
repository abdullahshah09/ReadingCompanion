package com.abdullah.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.util.Log;

import com.abdullah.Utills.utills;
import com.abdullah.model.Goal;
import com.abdullah.model.Stats;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class SQL extends SQLiteOpenHelper {

    public static String title = "title";
    public static String description = "description";
    public static String author = "author";
    public static String pages = "pages";
    public static String read_pages = "read_pages";
    public static String cover_page = "cover_page";
    public static String books = "books";
    public static String time = "time";
    public static String id = "id";
    public static String content = "content";
    public static String duration = "duration";
    public static String read_time = "read_time";
    public static String goal_time = "goal_time";
    public static String bookid = "bookid";
    public static String book_genre = "book_genre";


    public SQL(Context context) {
        super(context, "book_reader.db", null, 4);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String table1 = "CREATE TABLE books (id INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE,title TEXT,description TEXT,author TEXT,book_genre TEXT,pages INTEGER, read_pages INTEGER,read_time INTEGER,goal_time INTEGER,cover_page BLOB, time INTEGER)";
        final String pages = "CREATE TABLE pages (id INTEGER,content TEXT, time INTEGER)";
        final String duration = "CREATE TABLE duration (id INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE,bookid INTEGER,read_time INTEGER,read_pages INTEGER,time INTEGER)";
        final String goal = "CREATE TABLE goals (id INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE,duration INTEGER,read_duration INTEGER,end_time INTEGER,type INTEGER,time INTEGER)";
        final String stats = "CREATE TABLE stats (id INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE,read_duration INTEGER,day INTEGER,month INTEGER,time INTEGER)";

        sqLiteDatabase.execSQL(pages);
        sqLiteDatabase.execSQL(table1);
        sqLiteDatabase.execSQL(duration);
        sqLiteDatabase.execSQL(goal);
        sqLiteDatabase.execSQL(stats);

    }

    //"CREATE TABLE goals id ,duration read_duration ,end_time,type ,time
//id ,read_duration INTEGER,day INTEGER,month INTEGER,time INTEGER)
    public ArrayList<Stats> getStats(int days) {
        ArrayList<Stats> stats = new ArrayList<>();

        long time_targ = System.currentTimeMillis() - (days * 86400000);
        Calendar time = Calendar.getInstance();
        time.setTimeInMillis(time_targ);
        Log.e("SQL", "tagret" + time_targ);
        ///12-7=5
        Cursor cursor = getReadableDatabase().rawQuery("Select * from stats where time >" + (time_targ), null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                Stats stats1 = new Stats(cursor.getLong(1), cursor.getInt(0), cursor.getLong(4), cursor.getInt(2), cursor.getInt(3));
                try {

                    if ((stats1.getDay() == stats.get(stats.size() - 1).getDay())) {
                        stats.get(stats.size() - 1).add_duration(stats1.getDuration());
                        Log.e("SQL", "status added " + stats1.getDay());
                    } else {
                        stats.add(stats1);
                    }


                } catch (Exception e) {
                    Log.e("SQL", "status added " + e.toString());

                    stats.add(stats1);
                    e.printStackTrace();
                }


            } while (cursor.moveToNext());
        }

        Log.e("SQL", "STATS " + stats.size());
        return stats;
    }

    public boolean insert_stat(long d) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        ContentValues values = new ContentValues();
        values.put("read_duration", d);
        values.put("time", calendar.getTimeInMillis());
        values.put("day", calendar.get(Calendar.DAY_OF_MONTH));
        values.put("month", calendar.get(Calendar.DAY_OF_MONTH));
        if (getWritableDatabase().insert("stats", null, values) > 0) {
            Log.e("SQL", "stats added" + calendar.get(Calendar.DAY_OF_MONTH));
            return true;

        } else {
            Log.e("SQL", "UNABLE TO ADDED STATS" + calendar.DAY_OF_MONTH);

            return false;
        }
    }

    public ArrayList<Goal> get_Active_Goal(int type) {
        ArrayList<Goal> goals = new ArrayList<>();
        Long time = System.currentTimeMillis();
        String s = "SELECT id,time,end_time,duration,read_duration,type from goals where end_time>" + time + " and type=" + type + " order by time DESC limit 1";
        Cursor cursor = getReadableDatabase().rawQuery(s, null);
        Log.e("SQL", cursor.getCount() + "");
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                Goal goal = new Goal(cursor.getInt(0)
                        , cursor.getLong(1)
                        , cursor.getLong(2)
                        , cursor.getLong(3), cursor.getLong(4), cursor.getInt(5));
                goals.add(goal);
            } while (cursor.moveToNext());
        }
//        Log.e("SQL","goals "+goals.get(0).getId());

        return goals;

    }

    public boolean make_goal(Long duration, Long endTime, int type) {
        Long time = System.currentTimeMillis();

        ContentValues values = new ContentValues();
        values.put("duration", duration);
        values.put("end_time", endTime);
        values.put("time", time);
        values.put("type", type);
        values.put("read_duration", 0);
        if (getWritableDatabase().insert("goals", null, values) > 0) {
            Log.e("SQL", "goal created " + type);
            return true;
        } else {
            return false;
        }

    }


    ///to make new session this
    public boolean insert_duration(long duration_read, int id, int pages) {
        ContentValues values = new ContentValues();
        values.put(bookid, id);
        values.put(read_time, duration_read);
        values.put(read_pages, pages);
//        values.put(goal_time,goal);
        values.put(time, System.currentTimeMillis());
        if (getWritableDatabase().insert(duration, null, values) > 0) {
            String update_query = "update books set read_time =read_time+" + duration_read + ", " + "read_pages =read_pages+" + pages + " where id =" + id;
            String update_query_gaols = "update goals set read_duration =read_duration+" + duration_read;
            getWritableDatabase().execSQL(update_query);
            getWritableDatabase().execSQL(update_query_gaols);
            insert_stat(duration_read);
            Log.e("SQL", "added " + update_query);
//            getWritableDatabase().execSQL("update books set );
            return true;
        } else {
            return false;
        }

    }


    public ArrayList<HashMap<String, Object>> getBooks() {
        ArrayList<HashMap<String, Object>> images = new ArrayList<>();
        Cursor cursor = getReadableDatabase().query(books, new String[]{"id", "title", "description", "author", "pages", "read_pages", "cover_page", "time", "book_genre", "read_time", "goal_time"}, null, null, null, null, "time DESC");
        if (cursor.getCount() != 0) {
            cursor.moveToFirst();
            do {
                HashMap<String, Object> image_item = new HashMap<>();
                image_item.put(SQL.id, cursor.getInt(0));
                image_item.put(SQL.title, cursor.getString(1));
                image_item.put(SQL.description, cursor.getString(2));
                image_item.put(SQL.author, cursor.getString(3));
                image_item.put(SQL.pages, cursor.getInt(4));
                image_item.put(SQL.read_pages, cursor.getInt(5));
                image_item.put(SQL.cover_page, utills.getImage(cursor.getBlob(6)));
                image_item.put(SQL.time, cursor.getLong(7));
                image_item.put(SQL.book_genre, cursor.getString(8));
                image_item.put(SQL.read_time, cursor.getLong(9));
                image_item.put(SQL.goal_time, cursor.getLong(10));
                images.add(image_item);


            } while (cursor.moveToNext());
        }
        Log.e("SQL", "images " + images.size());

        return images;
    }


    public ArrayList<HashMap<String, Object>> getPages(int id) {
        ArrayList<HashMap<String, Object>> images = new ArrayList<>();
        Cursor cursor = getReadableDatabase().query(pages, new String[]{content}, "id=?", new String[]{id + ""}, null, null, "time");
        if (cursor.getCount() != 0) {
            cursor.moveToFirst();
            do {
                HashMap<String, Object> image_item = new HashMap<>();
                image_item.put(SQL.content, cursor.getString(0));

                images.add(image_item);


            } while (cursor.moveToNext());
        }
        Log.e("SQL", "contents " + images.size());

        return images;
    }


    public ArrayList<HashMap<String, Object>> getSessions(int id) {///id of the book
        ArrayList<HashMap<String, Object>> images = new ArrayList<>();
        Cursor cursor = getReadableDatabase().query(duration, new String[]{read_time, read_pages, time}, bookid + "=?", new String[]{id + ""}, null, null, "time DESC");
        if (cursor.getCount() != 0) {
            cursor.moveToFirst();
            do {
                HashMap<String, Object> image_item = new HashMap<>();
                image_item.put(SQL.read_time, cursor.getLong(0));
                image_item.put(SQL.read_pages, cursor.getInt(1));
                image_item.put(SQL.time, cursor.getLong(2));

                images.add(image_item);


            } while (cursor.moveToNext());
        }
        Log.e("SQL", id + "sessions " + images.size());

        return images;
    }


    public boolean put_page(String content, int bookid) {
        ContentValues values = new ContentValues();
        values.put(this.content, content);
        values.put(this.id, bookid);
        values.put(time, System.currentTimeMillis());
        if (getWritableDatabase().insert(pages, null, values) > 0) {
            getWritableDatabase().execSQL("update books set pages =pages+1 where id=" + bookid);
            return true;

        } else {
            return false;
        }


    }


    public boolean put_image(HashMap<String, Object> image) {
        ContentValues values = new ContentValues();
        values.put(title, image.get(title).toString());
        values.put(description, image.get(description).toString());
        values.put(author, image.get(author).toString());
        values.put(pages, image.get(pages).toString());
        values.put(read_pages, 0);
        values.put(cover_page, utills.getBitmapAsByteArray((Bitmap) image.get(cover_page)));
        values.put(time, System.currentTimeMillis());
        values.put(book_genre, image.get(SQL.book_genre).toString());
        values.put(read_time, 0);
        values.put(goal_time, 0);
        long r = getWritableDatabase().insert(books, null, values);
        Log.e("SQL", "r " + r);

        if (r > 0) {
            return true;
        } else {
            return false;
        }
    }


    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS contacts");
    }
}
