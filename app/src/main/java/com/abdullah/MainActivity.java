package com.abdullah;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.abdullah.Utills.utills;
import com.abdullah.database.SQL;
import com.abdullah.interfaces.Book_click_listiner;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.abdullah.R;

import java.util.ArrayList;
import java.util.HashMap;


public class MainActivity extends AppCompatActivity {

    ImageButton btn_cover;
    Context context;
    Bitmap cover_btm;
    RecyclerView books_recycler_view, books_recycler_view_sessions;

    SQL db;
    ArrayList<HashMap<String, Object>> books = new ArrayList<>();
    ArrayList<HashMap<String, Object>> books_filterd = new ArrayList<>();
    Books_Adapter booksAdapter;
    String q = "";
    int last_pos;

    ///hash storing book deatil for session data again after adding new session when activity resume it load all sessions using this book object
    HashMap<String, Object> last_book;


    @Override
    protected void onResume() {
        super.onResume();

        ////it refresh data from sqlltel on each activity resume
        update_recycle_view();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        books_recycler_view = findViewById(R.id.books_recycler_view);
        books_recycler_view_sessions = findViewById(R.id.books_recycler_view_sessons);
//        startActivity(new Intent(this,Google_Search.class));

        BottomNavigationView navigationView=(BottomNavigationView)findViewById(R.id.bottom_navigation);
        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id=menuItem.getItemId();
                switch (id){
                    case R.id.navigation_glao:
                        cliced_flag();
                        break;
                        case R.id.navigation_stats:
                       startActivity(new Intent(context,Stats.class));
                        break;
                }
                return false;
            }
        });

        context = this;
        db = new SQL(context);////make data object
        booksAdapter = new Books_Adapter(books_filterd, context, db);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
//        GridLayoutManager gridLayoutManager=new GridLayoutManager(context ,GridLayoutManager.DEFAULT_SPAN_COUNT);
        books_recycler_view.setAdapter(booksAdapter);
        books_recycler_view.setLayoutManager(linearLayoutManager);

        update_recycle_view();

        booksAdapter.setListiner(new Book_click_listiner() {//it custom interface class used to listing click on cver
            @Override
            public void clicked(final int pos, final HashMap<String, Object> book) {
                new Handler().post(new Runnable() {//to avoid any UI thread lag, we use a thread
                    @Override
                    public void run() {
                        last_book = book;
                        updae_session_list(book);
                    }
                });

            }
        });

        final FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ////check required permeission
                Activity thisActivity = MainActivity.this;
                // Here, thisActivity is the current activity
                if (ContextCompat.checkSelfPermission(thisActivity,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

                    // Permission is not granted
                    // Should we show an explanation?
                    if (ActivityCompat.shouldShowRequestPermissionRationale(thisActivity,
                            Manifest.permission.READ_CONTACTS)) {
                        // Show an explanation to the user *asynchronously* -- don't block
                        // this thread waiting for the user's response! After the user
                        // sees the explanation, try again to request the permission.
                    } else {
                        // No explanation needed; request the permission
                        ActivityCompat.requestPermissions(thisActivity,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                4);

                        // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                        // app-defined int constant. The callback method gets the
                        // result of the request.
                    }
                    return;
                } else {
                    // Permission has already been granted
                }

                final Dialog dialog = new Dialog(MainActivity.this);///custom diloge to add any books
                dialog.setContentView(R.layout.fragment_insert__book);
                dialog.getWindow().setLayout(Toolbar.LayoutParams.MATCH_PARENT, Toolbar.LayoutParams.WRAP_CONTENT);
                dialog.show();

                btn_cover = (ImageButton) dialog.findViewById(R.id.imageButton);
                final EditText edt_title = (EditText) dialog.findViewById(R.id.editText_title);
                final EditText edt_desc = (EditText) dialog.findViewById(R.id.edit_text_desc);
                final EditText edt_aouther = (EditText) dialog.findViewById(R.id.editText6_aouther_name);
                final EditText edt_book_genre = (EditText) dialog.findViewById(R.id.editText6_genr);
                final EditText edt_book_pages = (EditText) dialog.findViewById(R.id.editText6_pages);


                Button button_book = (Button) dialog.findViewById(R.id.button_add);
                Button button_add_from_google = (Button) dialog.findViewById(R.id.button_add_from_google);
                button_add_from_google.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        startActivity(new Intent(context,Google_Search.class));
                    }
                });


                button_book.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ///save all data to

                        if (edt_book_genre.getText().toString().isEmpty()) {
                            return;
                        }
                        if (edt_book_pages.getText().toString().isEmpty()) {
                            return;
                        }

                        if (edt_aouther.getText().toString().isEmpty()) {
                            return;
                        }
                        if (edt_desc.getText().toString().isEmpty()) {
                            return;
                        }
                        if (edt_title.getText().toString().isEmpty()) {
                            return;
                        }
                        HashMap<String, Object> image_item = new HashMap<>();
                        if (cover_btm == null) {
                            return;
                        }


                        image_item.put(SQL.title, edt_title.getText().toString());
                        image_item.put(SQL.description, edt_desc.getText().toString());
                        image_item.put(SQL.author, edt_aouther.getText().toString());
                        image_item.put(SQL.cover_page, cover_btm);
                        image_item.put(SQL.book_genre, edt_book_genre.getText().toString());
                        image_item.put(SQL.pages, edt_book_pages.getText().toString());
                        if (db.put_image(image_item)) {
                            dialog.dismiss();
                            utills.snak(fab, "New book is added");
//                           context.startActivity(context,);
                            update_recycle_view();
                        } else {
                            utills.snak(fab, "something went wrongs");

                        }
                    }
                });
            }
        });
    }

    private void updae_session_list(HashMap<String, Object> book) {
        Session_Adapter session_adapter = new Session_Adapter(db.getSessions((Integer) book.get(SQL.id)), MainActivity.this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
        books_recycler_view_sessions.setLayoutManager(linearLayoutManager);
        books_recycler_view_sessions.setAdapter(session_adapter);
    }

    private void update_recycle_view() {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                if (q.isEmpty()) {
                    books_filterd.clear();
                    books_filterd.addAll(db.getBooks());
                    books.clear();
                    books.addAll(books_filterd);
                }
                booksAdapter.notifyDataSetChanged();

                if (last_book != null) {
                    updae_session_list(last_book);
                }

            }
        });
    }

    private void update_recycle_view_search() {
        new Handler().post(new Runnable() {
            @Override
            public void run() {

                booksAdapter.notifyDataSetChanged();

            }
        });
    }

    @Override
    protected void onDestroy() {
        db.close();
        super.onDestroy();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem myActionMenuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) myActionMenuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }


            @Override
            public boolean onQueryTextChange(String s) {
                q = s;
                books_filterd.clear();
                books_filterd.addAll(utills.getFilter(books, s));
                booksAdapter.notifyDataSetChanged();
                return false;
            }
        });

        return true;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                Log.e("MAINACTIVTY", resultUri.toString());

//                btn_cover.setImageDrawable(BitmapDrawable.createFromPath(resultUri.toString()));

                Picasso.get().load(resultUri).into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        btn_cover.setImageBitmap(bitmap);
                        cover_btm = bitmap;
                    }

                    @Override
                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                        utills.snak(btn_cover, "Failed to load image");

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    public void image_load(View view) {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.OFF)

                .setRequestedSize(500, 750, CropImageView.RequestSizeOptions.RESIZE_FIT)
                .start(this);


    }



    public void cliced_flag(){
        startActivity(new Intent(this, Goal_Activty.class));
    }
    public String getPath(Uri uri) {
        String result;
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = uri.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
