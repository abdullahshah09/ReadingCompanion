package com.abdullah;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.abdullah.Utills.utills;
import com.abdullah.database.SQL;
import com.abdullah.R;

public class Add_page extends AppCompatActivity {

    SQL db;
    EditText text;
    String last_conetent="";
    int bookid;
    int mode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_page);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        text=(EditText)findViewById(R.id.page_content);
        bookid=getIntent().getIntExtra("id",-1);
        mode=getIntent().getIntExtra("mode",-1);
        if(mode==2){
            Button button=(Button)findViewById(R.id.new_page_added_btn);
            button.setText("update");
            text.setText(getIntent().getStringExtra(SQL.content));
            this.setTitle("Notes about the book");
        }else {
            this.setTitle("New Pages to book");

        }
        db=new SQL(this);
//        text.setText(bookid+"");




    }

    public void new_page_aded(View view){
        if(text.getText().toString().isEmpty()){
            return;
        }
        String conetent=text.getText().toString();


        if(bookid==-1){
            return;
        }


        if(mode==2){
            utills.snak(view,"updated");
            db.getWritableDatabase().execSQL("update books set description='"+conetent +"' where id="+bookid);
//            text.setText("");
            finish();
            return;
        }
        if(db.put_page(conetent,bookid)){
            text.setText("");
            utills.snak(view,"Page saved");
        }else {
            utills.snak(view,"try again");
        }





    }

}
