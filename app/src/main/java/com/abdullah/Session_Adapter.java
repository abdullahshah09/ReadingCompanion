package com.abdullah;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.abdullah.Utills.utills;
import com.abdullah.database.SQL;
import com.abdullah.R;

import java.util.ArrayList;
import java.util.HashMap;

public class Session_Adapter extends RecyclerView.Adapter <Session_Adapter.ViewHolder>{


    ArrayList<HashMap<String,Object>> sessions=new ArrayList<>();
    Context context;

    public Session_Adapter(ArrayList<HashMap<String, Object>> sessions, Context context) {
        this.sessions = sessions;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.sessions_item,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final @NonNull ViewHolder holder, int position) {


      final   HashMap<String,Object> session=sessions.get(position);

        holder.title.setText("Date "+ utills.DateFormate((Long) session.get(SQL.time)) );
        holder.title.append("\nPages "+session.get(SQL.read_pages)+"\n");
        holder.title.append("Length of time "+ utills.convertSecondsToHMmSs((Long) session.get(SQL.read_time))+"\n");


    }

    @Override
    public int getItemCount() {
        return sessions.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{


        TextView title;

        public ViewHolder(@NonNull View v) {
            super(v);
            title=(TextView)v.findViewById(R.id.item_session_txt);

        }
    }
}
