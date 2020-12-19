package com.abdullah;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.abdullah.database.SQL;
import com.abdullah.R;

import java.util.ArrayList;
import java.util.HashMap;

public class Pages_Adapter extends RecyclerView.Adapter <Pages_Adapter.ViewHolder>{


    ArrayList<HashMap<String,Object>> pages =new ArrayList<>();
    Context context;
    String title;
    SQL sql;





    public Pages_Adapter(ArrayList<HashMap<String, Object>> pages, Context context, String title, SQL sql) {
        this.pages = pages;
        this.context = context;
        this.title = title;
        this.sql = sql;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.page_item,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final @NonNull ViewHolder holder, int position) {
      final   HashMap<String,Object> page=pages.get(position);

        holder.pager.setText((position+1)+"");
        holder.title.setText(title);
        holder.content.setText(page.get(SQL.content).toString());






    }

    @Override
    public int getItemCount() {
        return pages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{


        TextView title,content,pager;

        public ViewHolder(@NonNull View v) {
            super(v);

            title=(TextView)v.findViewById(R.id.page_content_header);
            content=(TextView)v.findViewById(R.id.page_content);
            pager=(TextView)v.findViewById(R.id.page_content_number);

        }
    }
}
