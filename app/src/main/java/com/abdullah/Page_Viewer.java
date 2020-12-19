package com.abdullah;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.abdullah.Utills.utills;
import com.abdullah.database.SQL;
import com.abdullah.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class Page_Viewer extends AppCompatActivity {

    int id;
    RecyclerView page_view_recycleView;
    ArrayList<HashMap<String,Object>> pages=new ArrayList<>();
    SQL db;
     Pages_Adapter pagesAdapter;
    LinearLayoutManager linearLayoutManager;
    int read_pages=0;
    long start_time;
    int start_page=0;
    boolean end=false;
    AlertDialog alertDialog;
    Timer timer;

    @Override
    protected void onPause() {
        super.onPause();

    }




    @Override
    public void onBackPressed() {

       final int current_page=linearLayoutManager.findLastVisibleItemPosition();
       final int read_page=current_page-read_pages;
        final long read_time=System.currentTimeMillis()-start_time;

        final AlertDialog.Builder builder=new AlertDialog.Builder(this)

                .setTitle("Session End")
                .setMessage("Do you want to end this session you have read "+(current_page-start_page)+" and duration"+(read_time) )
                .setCancelable(false)
                .setNeutralButton("Cencel  This Session", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(alertDialog!=null){
                            alertDialog.dismiss();
                        }
                    }
                })
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        new Handler().post(new Runnable() {
                            @Override
                            public void run() {
                                db.getWritableDatabase().execSQL("update books set read_pages="+current_page+" where id="+id);
                                db.insert_duration(read_time,id,read_page);
                                Log.e("PAGEVIEW","read oages"+linearLayoutManager.findLastVisibleItemPosition());
                               end=true;
                               onBackPressed();

                            }
                        });
                    }
                });
        if(end){
            super.onBackPressed();
        }else {
            alertDialog= builder.show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page__viewer);
        start_time=System.currentTimeMillis();
        id=getIntent().getIntExtra(SQL.id,0);
        read_pages=getIntent().getIntExtra(SQL.read_pages,1);
        start_page=read_pages;

        String title=getIntent().getStringExtra(SQL.title);
        page_view_recycleView=(RecyclerView)findViewById(R.id.page_view_recycleView);
        Log.e("PAGEVIEW","now read oages"+read_pages);
        utills.snak(page_view_recycleView,"Your session is started");


        db=new SQL(this);
        pagesAdapter=new Pages_Adapter(pages,this,title,db);
         linearLayoutManager=new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false);
        page_view_recycleView.setLayoutManager(linearLayoutManager);
        page_view_recycleView.setAdapter(pagesAdapter);



       timer= new Timer();
       timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                final int current_page=linearLayoutManager.findLastVisibleItemPosition();
                final long read_time=System.currentTimeMillis()-start_time;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Page_Viewer.this.setTitle("Session duration "+ utills.convertSecondsToHMmSs(read_time)+" pages "+(current_page-start_page));
                    }
                });
            }
        },500,1);




        new Handler().post(new Runnable() {
            @Override
            public void run() {
                pages.clear();
                pages.addAll(db.getPages(id));
                pagesAdapter.notifyDataSetChanged();

                    linearLayoutManager.scrollToPosition(read_pages);
                    if(pages.size()==0){
                        timer.cancel();
                        utills.snak(page_view_recycleView,"This book is empty");
                        end=true;

                    }





            }
        });



    }





}
