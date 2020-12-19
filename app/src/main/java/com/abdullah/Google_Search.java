package com.abdullah;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.abdullah.Utills.utills;
import com.abdullah.database.SQL;
import com.abdullah.interfaces.OnInsertBookListiner;
import com.abdullah.model.Book;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Google_Search extends AppCompatActivity {
    EditText GB_edt_search;
    String TAG="GB";

    RequestQueue queue;
    ProgressDialog dialog;
    final String baseURL="https://www.googleapis.com/books/v1/volumes";
    Button button;
    ArrayList<Book> books=new ArrayList<>();
    RecyclerView google_books_recycleView;
    GB_Adapter gbAdapter;
    SQL sql;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google__search);
        GB_edt_search=(EditText)findViewById(R.id.GB_edt_search);
        queue = Volley.newRequestQueue(this);
        dialog=new ProgressDialog(this);
        dialog.setCancelable(false);
        button=(Button)findViewById(R.id.btn_go) ;
        gbAdapter=new GB_Adapter();
        sql=new SQL(this);
        google_books_recycleView=(RecyclerView)findViewById(R.id.google_books_recycleView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);


        google_books_recycleView.setLayoutManager(linearLayoutManager);
        google_books_recycleView.setAdapter(gbAdapter);






        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!GB_edt_search.getText().toString().isEmpty()){
                    books.clear();
                    searc_book(GB_edt_search.getText().toString());
                    dialog.show();
                }
            }
        });





    }


    public void searc_book(final String s)  {
        String URL=baseURL
                +"?q="+s
                +"&key=AIzaSyCb1VyYoQ3CFIIDHgCoqZpIa_8T_2Zt6tk"
                +"&printType=books";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response)  {
                try {
                    JSONObject object=new JSONObject(response);
                    JSONArray Items=object.getJSONArray("items");
                    int size=object.getInt("totalItems");
                    for (int i=0;i<Items.length();i++){

                        String title=Items.getJSONObject(i).getJSONObject("volumeInfo").getString("title");
                        String descrition= "";
                        try {
                            descrition = Items.getJSONObject(i).getJSONObject("volumeInfo").getString("description");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        String author=Items.getJSONObject(i).getJSONObject("volumeInfo").getJSONArray("authors").getString(0);//items[0].volumeInfo.industryIdentifiers[0].identifier
                        String ISBN10=Items.getJSONObject(i).getJSONObject("volumeInfo").getJSONArray("industryIdentifiers").getJSONObject(0).getString("identifier");
                        String ISBN13=Items.getJSONObject(i).getJSONObject("volumeInfo").getJSONArray("industryIdentifiers").getJSONObject(1).getString("identifier");
                        String gener= "";
                        try {
                            gener = Items.getJSONObject(i).getJSONObject("volumeInfo").getJSONArray("categories").getString(0);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        String cover_url=Items.getJSONObject(i).getJSONObject("volumeInfo").getJSONObject("imageLinks").getString("thumbnail");//items[0].volumeInfo.

                        int pages=Items.getJSONObject(i).getJSONObject("volumeInfo").getInt("pageCount");
                        int Avegrating=0;//Items.getJSONObject(i).getJSONObject("volumeInfo").getInt("averageRating");//items[0].volumeInfo.
                        int ratingCount=0;//Items.getJSONObject(i).getJSONObject("volumeInfo").getInt("ratingsCount");//items[0].volumeInfo.
                        Book book=new Book(title,descrition,author,gener,ISBN10,ISBN13,cover_url,Avegrating,pages,ratingCount);
                        books.add(book);
                        Log.e(TAG,title);
                    }

                    Log.e(TAG,"books added"+books.size());





                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(TAG,"error"+e.toString());

                }
                gbAdapter.notifyDataSetChanged();

//                Snackbar.make(pass,"Registration "+response,Snackbar.LENGTH_SHORT).show();
                dialog.dismiss();

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG,error.toString());

//                Snackbar.make(pass,"Registration Failed",Snackbar.LENGTH_SHORT).show();
                dialog.dismiss();

            }
        }){
            @Override
            public void cancel() {
                super.cancel();
//                Snackbar.make(pass,"Registration Cancled",Snackbar.LENGTH_SHORT).show();
                dialog.dismiss();
            }



            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<>();
                params.put("q",s);
                params.put("key","AIzaSyCb1VyYoQ3CFIIDHgCoqZpIa_8T_2Zt6tk");
                params.put("printType","books");
//                params.put("password", pass.getText().toString());
//                params.put("confirm_password", pass.getText().toString());
//                params.put("terms_and_conditions","1");
//                params.put("username","username");

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
//                params.put("Content-Type","application/x-www-form-urlencoded");
//                params.put("Authorization","Basic YWtzb3lzZXplcjk1QGdtYWlsLmNvbTpzZXplcjEyMw==");
                return params;
            }



            @Override
            protected void deliverResponse(String response) {
                super.deliverResponse(response);
            }




        };

        queue.add(stringRequest);
    }

    public class GB_Adapter extends RecyclerView.Adapter<GB_Adapter.ViewHolder> {

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view= LayoutInflater.from(Google_Search.this).inflate(R.layout.item_gb,parent,false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final @NonNull ViewHolder holder, int position) {
           final Book book=books.get(position);



            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    Picasso.get().load(book.getCover()+":keyes&key=AIzaSyCb1VyYoQ3CFIIDHgCoqZpIa_8T_2Zt6tk").into(new Target() {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                            holder.img.setImageBitmap(bitmap);
                        }

                        @Override
                        public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                            Log.e(TAG,e.toString());


                        }

                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {

                        }
                    });
                    Log.e(TAG,book.getCover());

                }
            });
            holder.title.setText(book.getTitle());
            holder.desc.setText(book.getSecription());
            holder.extra.setText("author: "+book.getAuthor());
            holder.extra.append("\npages: "+book.getPages());
            holder.extra.append("\nrating: "+book.getRating());
            holder.extra.append("\ngenre: "+book.getGener());
            holder.extra.append("\nISBN10: "+book.getIsbn10());
            holder.extra.append("\nISBN13: "+book.getIsb13());
            holder.button6.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    book.save_to_collection(sql, new OnInsertBookListiner() {
                        @Override
                        public void sucess() {
                            utills.snak(view,"Book added");
                        }
                    });
                }
            });

        }

        @Override
        public int getItemCount() {
            return books.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder{
            ImageView img;
            TextView title,desc,extra;
            Button button6;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                img=(ImageView)itemView.findViewById(R.id.itemgb_img);
                title=(TextView)itemView.findViewById(R.id.gb_item_title);
                desc=(TextView)itemView.findViewById(R.id.item_gb_details2);
                extra=(TextView)itemView.findViewById(R.id.item_gb_extra);
                button6=(Button) itemView.findViewById(R.id.button6);
            }
        }
    }
}
