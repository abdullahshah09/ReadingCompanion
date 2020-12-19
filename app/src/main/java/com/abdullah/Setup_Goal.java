package com.abdullah;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.abdullah.Utills.utills;
import com.abdullah.database.SQL;

public class Setup_Goal extends AppCompatActivity {

    Button button4_goal_setup;
    EditText editText;
    int mode=1;
    SQL sql;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup__goal);
        mode=getIntent().getIntExtra("type",-1);
        Log.e("SQL","created goal for "+mode);
        sql=new SQL(this);

        button4_goal_setup=(Button)findViewById(R.id.button7);
        editText=(EditText) findViewById(R.id.editText2);

        button4_goal_setup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long type=0;
               if(mode==-1){
                   return;
               }
               if(mode==1){
                   type=(long) 24*60*60*1000;
               }else if(mode==2) {
                   type=(long)30*24*60*60*1000;

               }
               long duration=(long) Integer.parseInt(editText.getText().toString())*3600000;
                Log.e("SQL","duration "+duration);

               long endLong=System.currentTimeMillis()+type;


               if(sql.make_goal(duration,endLong,mode)){
                   finish();
               }else {
                   utills.snak(view,"unable to make new goal");
               }




            }
        });
    }
}
