package com.abdullah;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.abdullah.database.SQL;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.RadarData;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Stats extends AppCompatActivity {

    LineChart char_1;
    SQL sql;
    ArrayList<com.abdullah.model.Stats> stats=new ArrayList<>();

    Button btn_stats_weekly,btn_stats_monthly;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        btn_stats_weekly=(Button)findViewById(R.id.btn_stats_weekly);
        btn_stats_monthly=(Button)findViewById(R.id.btn_stats_monthly);
        char_1=(LineChart)findViewById(R.id.char_1);

        update_char(7);

        btn_stats_monthly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                update_char(20);
                btn_stats_monthly.setTextColor(Color.WHITE);
                btn_stats_weekly.setTextColor(Color.BLACK);
                btn_stats_weekly.setBackgroundTintList(Stats.this.getResources().getColorStateList(R.color.unSelected));
                btn_stats_monthly.setBackgroundTintList(Stats.this.getResources().getColorStateList(R.color.selected));

            }
        });

        btn_stats_weekly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                btn_stats_monthly.setTextColor(Color.BLACK);
                btn_stats_weekly.setTextColor(Color.WHITE);
                btn_stats_weekly.setBackgroundTintList(Stats.this.getResources().getColorStateList(R.color.selected));
                btn_stats_monthly.setBackgroundTintList(Stats.this.getResources().getColorStateList(R.color.unSelected));
                update_char(7);
            }
        });


//        char_1.getLineData().setDrawValues(true);

//        RadarData radarData=new RadarData();


        sql=new SQL(this);





    }

    public void update_char(final int days){
        new Handler().post(new Runnable() {
            @Override
            public void run() {

                try {
                    stats.clear();
                    char_1.invalidate();
                    char_1.clear();

                    stats.addAll(sql.getStats(days));
                    List<Entry> list=new ArrayList<>();

                    int a=0;
                    Log.e("SQL","got stats"+stats.size());

                    for (com.abdullah.model.Stats s: stats) {

                        try {
                            Calendar calendar=Calendar.getInstance();

                            calendar.setTimeInMillis(s.getTime());

                            Entry entry=new Entry();
                            entry.setX(calendar.get(calendar.DAY_OF_MONTH));
                            entry.setY((float) s.getDuration()/3600000);
                            list.add(entry);
                            Log.e("SQL",(float) s.getDuration()/3600000+"day "+ calendar.get(calendar.DAY_OF_MONTH));
                        } catch (NegativeArraySizeException e) {
                            e.printStackTrace();
                        }
                        a++;
                    }
                    Log.e("SQL","list "+list.size());


                    LineData lineData=new LineData();
                    LineDataSet dataset=new LineDataSet(list,"Monthly");
                    dataset.setLineWidth(2.0f);
                    dataset.setDrawFilled(true);
                    dataset.setCircleHoleColor(0);
                    dataset.setCircleRadius(4);
                    dataset.setColor(getColor(R.color.selected));
                    dataset.setCircleColor(getColor(R.color.selected));
//                char_1.getXAxis().setDrawAxisLine(false);
//                char_1.getA().setDrawAxisLine(false);

                    lineData.addDataSet(dataset);
                    lineData.setDrawValues(false);

//                lineData.setDrawFilled(true);
                    char_1.setData(lineData);
                } catch (NegativeArraySizeException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
