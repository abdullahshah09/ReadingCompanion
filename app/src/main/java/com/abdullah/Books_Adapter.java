package com.abdullah;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.abdullah.Utills.utills;
import com.abdullah.database.SQL;
import com.abdullah.R;
import com.abdullah.interfaces.Book_click_listiner;

import java.util.ArrayList;
import java.util.HashMap;

public class Books_Adapter extends RecyclerView.Adapter<Books_Adapter.ViewHolder> {


    ArrayList<HashMap<String, Object>> books = new ArrayList<>();
    Context context;
    SQL sql;
    Book_click_listiner listiner;
    AlertDialog d;

    public void setListiner(Book_click_listiner listiner) {
        this.listiner = listiner;
    }

    public Books_Adapter(ArrayList<HashMap<String, Object>> books, Context context, SQL sql) {
        this.books = books;
        this.context = context;
        this.sql = sql;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.book_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final @NonNull ViewHolder holder, final int position) {


        final HashMap<String, Object> book = books.get(position);
        holder.author.setText("Author: " + book.get(SQL.author).toString());
        holder.author.append("\nGenre: " + book.get(SQL.book_genre).toString());
        holder.title.setText("Book: " + book.get(SQL.title).toString());
        holder.desc.setText(book.get(SQL.description).toString());

        final String date = utills.DateFormate((Long) book.get(SQL.time));
        final String time = utills.convertSecondsToHMmSs((Long) book.get(SQL.read_time));
        final String goal = utills.convertSecondsToHMmSs((Long) book.get(SQL.goal_time));

        holder.time.setText("Date added: " + date + "\nPages: " + ((Integer) book.get(SQL.read_pages) + 1) + "/" + ((Integer) book.get(SQL.pages)));
        holder.time.append("\n Time elapsed: " + time);
        holder.goal.setText("Goal Time: " + goal);
        holder.cover.setImageBitmap((Bitmap) book.get(SQL.cover_page));

        holder.goal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText editText=new EditText(context);
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);




                AlertDialog.Builder builder=new AlertDialog.Builder(context)
                        .setTitle("Enter your goal, in min")
                        .setView(editText)
                        .setCancelable(false)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                    ///updating the gaol

                                sql.getWritableDatabase().execSQL("update books set goal_time="+Double.parseDouble(editText.getText().toString()) +" where id ="+(Integer)book.get(SQL.id));

                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                d.dismiss();
                            }
                        });
                d=builder.create();
                d.show();


            }
        });
//
//        holder.author.setVisibility(View.GONE);
//        holder.desc.setVisibility(View.GONE);
//        holder.time.setVisibility(View.GONE);
//        holder.button.setVisibility(View.GONE);

//       new Handler().post(new Runnable() {
//           @Override
//           public void run() {
//               Session_Adapter session_adapter=new Session_Adapter(sql.getSessions((Integer) book.get(SQL.id)),context);
//               LinearLayoutManager linearLayoutManager=new LinearLayoutManager(context, LinearLayoutManager.VERTICAL,false);
//               holder.book_serssions_recyclerView.setLayoutManager(linearLayoutManager);
//               holder.book_serssions_recyclerView.setAdapter(session_adapter);
//           }
//       });

        holder.desc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ///
                context.startActivity(new Intent(context, Add_page.class).putExtra(SQL.content, holder.desc.getText().toString()).putExtra("mode", 2).putExtra(SQL.read_pages, (Integer) book.get(SQL.read_pages)).putExtra(SQL.title, book.get(SQL.title).toString()).putExtra(SQL.id, (Integer) book.get("id")));

            }
        });

        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(context, Add_page.class).putExtra(SQL.read_pages, (Integer) book.get(SQL.read_pages)).putExtra(SQL.title, book.get(SQL.title).toString()).putExtra(SQL.id, (Integer) book.get("id")));

            }
        });

        holder.button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listiner != null) {
                    listiner.clicked(position, book);
                }
                context.startActivity(new Intent(context, Book_details.class)
                        .putExtra(SQL.pages, (Integer) book.get(SQL.pages))
                        .putExtra(SQL.title, book.get(SQL.title).toString())
                        .putExtra(SQL.id, (Integer) book.get("id")));

            }
        });

        holder.cover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                context.startActivity(new Intent(context, Book_details.class)
//                        .putExtra(SQL.author,book.get(SQL.author).toString())
//                        .putExtra(SQL.book_genre,book.get(SQL.book_genre).toString())
//                        .putExtra(SQL.title,book.get(SQL.title).toString())
//                        .putExtra(SQL.time,date)
//                        .putExtra(SQL.time,book.get(SQL.title).toString())
//                        .putExtra(SQL.description,book.get(SQL.description).toString())
//                        .putExtra(SQL.id,(Integer) book.get("id")));

                holder.author.setVisibility(View.VISIBLE);
                holder.desc.setVisibility(View.VISIBLE);
                holder.time.setVisibility(View.VISIBLE);
//                holder.button.setVisibility(View.VISIBLE);
//                holder.cover.setVisibility(View.GONE);
                if (listiner != null) {
                    listiner.clicked(position, book);
                }


            }
        });


    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView cover;
        TextView title, desc, author, time,goal;
        Button button, button2;
        RecyclerView book_serssions_recyclerView;

        public ViewHolder(@NonNull View v) {
            super(v);
            cover = (ImageView) v.findViewById(R.id.item_cover);
            title = (TextView) v.findViewById(R.id.item_title);
            desc = (TextView) v.findViewById(R.id.item_desc);
            author = (TextView) v.findViewById(R.id.item_author);
            time = (TextView) v.findViewById(R.id.item_time);
            button = (Button) v.findViewById(R.id.button);
            button2 = (Button) v.findViewById(R.id.button2);
            goal = (TextView) v.findViewById(R.id.item_goal);
            book_serssions_recyclerView = (RecyclerView) v.findViewById(R.id.book_serssions_recyclerView);
        }
    }
}
