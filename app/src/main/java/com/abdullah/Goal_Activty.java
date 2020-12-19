package com.abdullah;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.abdullah.R;
import com.abdullah.Utills.utills;
import com.abdullah.database.SQL;
import com.abdullah.model.Goal;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Formatter;

public class Goal_Activty extends AppCompatActivity {


    Button button4_goal_setup,button5_dayily,button5_monthl;
    EditText editText;
    int mode=1;
    SQL sql;
    TextView textView_hours,textView5;
    ArrayList<Goal> goals=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal__activty);
        button4_goal_setup=(Button)findViewById(R.id.button4_goal_setup);
        button5_dayily=(Button)findViewById(R.id.button5_dayily);
        button5_monthl=(Button)findViewById(R.id.button4_monthl);
        editText=(EditText) findViewById(R.id.editText2);
        textView_hours=(TextView) findViewById(R.id.textView6);
        textView5=(TextView) findViewById(R.id.textView5);


        button5_monthl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mode=1;
                update_ui();
                button5_dayily.setTextColor(Color.BLACK);
                button5_monthl.setTextColor(Color.WHITE);
                button5_dayily.setBackgroundTintList(Goal_Activty.this.getResources().getColorStateList(R.color.unSelected));
                button5_monthl.setBackgroundTintList(Goal_Activty.this.getResources().getColorStateList(R.color.selected));

            }
        });
        button5_dayily.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mode=2;
                update_ui();
                button5_dayily.setTextColor(Color.WHITE);
                button5_monthl.setTextColor(Color.BLACK);
                button5_dayily.setBackgroundTintList(Goal_Activty.this.getResources().getColorStateList(R.color.selected));
                button5_monthl.setBackgroundTintList(Goal_Activty.this.getResources().getColorStateList(R.color.unSelected));


            }
        });




//        update_ui();


        button4_goal_setup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            startActivity(new Intent(Goal_Activty.this,Setup_Goal.class).putExtra("type",mode));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        update_ui();
    }

    private void update_ui() {
        sql=new SQL(this);
        goals.clear();

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                goals.addAll( sql.get_Active_Goal(mode));
                if(goals.size()==0){
                    utills.snak(button4_goal_setup,"No Goal found");
                    textView_hours.setText("0/0");

                    textView5.setText("Started on: " );
                    textView5.append("\nEnd on: ");
                    return;
                }
                Log.e("SQL","got duration "+goals.get(0).getRead_duration());

                DecimalFormat precision = new DecimalFormat("0.00");
                ;

                textView_hours.setText(precision.format((double)goals.get(0).getRead_duration()/(3600000))+"/"+(goals.get(0).getDuration()/(3600000)));

                textView5.setText("Started on: "+ utills.DateFormate(goals.get(0).getTime()));
                textView5.append("\nEnd on: "+utills.DateFormate(goals.get(0).getEnd_time()));
            }
        });
    }
}
