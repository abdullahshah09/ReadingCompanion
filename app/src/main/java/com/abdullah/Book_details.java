package com.abdullah;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.abdullah.Utills.utills;
import com.abdullah.database.SQL;
import com.abdullah.R;

import java.util.Timer;
import java.util.TimerTask;

public class Book_details extends AppCompatActivity {

    Timer timer;
    long start_time;
    int id,pages;
    boolean end=false;
    EditText pages_read;
    SQL sql;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_details);
        id=getIntent().getIntExtra(SQL.id,-1);
        pages=getIntent().getIntExtra(SQL.pages,-1);
        final String title=getIntent().getStringExtra(SQL.title);
        final TextView duration=(TextView)findViewById(R.id.sesssion_duration_txt);
        pages_read=(EditText) findViewById(R.id.read_pages_edt);
        start_time=System.currentTimeMillis();
        sql=new SQL(this);

        Button button3=(Button)findViewById(R.id.button3);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final long read_time=System.currentTimeMillis()-start_time;
                if(pages_read.getText().toString().isEmpty()||pages_read.getText().toString().equalsIgnoreCase("0") ) {
                    utills.snak(pages_read,"Enter the pages number");
                    return;
                }


//                sql.insert_duration(read_time,id,Integer.parseInt(pages_read.getText().toString()));
                sql.insert_duration(read_time*Integer.parseInt(pages_read.getText().toString()),id,Integer.parseInt(pages_read.getText().toString()));
                finish();



            }
        });




        timer= new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                final long read_time=System.currentTimeMillis()-start_time;
               runOnUiThread(new Runnable() {
                   @Override
                   public void run() {
                       duration.setText(utills.convertSecondsToHMmSs(read_time));
                   }
               });

            }
        },500,1);

    }

    @Override
    public void onBackPressed() {
        if(end){
            super.onBackPressed();
        }else {
        utills.snak(pages_read,"End the Session!!");
        }
    }
}
