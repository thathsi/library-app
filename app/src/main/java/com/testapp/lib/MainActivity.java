package com.testapp.lib;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private DBHandler dbHandler;
    private RecyclerView bookContainer, userContainer;
    private List<Book> bookList;
    private BookAdapter bookAdapter;
    private List<Users> userList;
    private UserAdapter userAdapter;

    private Button addBook, addMem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHandler = new DBHandler(MainActivity.this);
        if (isRecordsTableEmpty() || isMembersTableEmpty()){
            addFirstRecs();
            Toast.makeText(MainActivity.this,"Loading initial records...", Toast.LENGTH_LONG).show();
        }
        loadBooks();
        loadUsers();

        addBook = findViewById(R.id.insert_btn);
        addMem = findViewById(R.id.user_insert_btn);

        addBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddActivity.class);
                intent.putExtra("mode", "book");
                startActivity(intent);
            }
        });

        addMem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddActivity.class);
                intent.putExtra("mode", "member");
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        loadBooks();
        loadUsers();
    }

    private void addFirstRecs() {

        long currentTimeMillis = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        String formattedTimestamp = sdf.format(new Date(currentTimeMillis));

        dbHandler.addRecord("The Good little Ceylonese Girl", "Ashok Ferrey","It covers Sri Lankans finding their places in different corners of the world! Humorous and entertaining , the tales cover the individuals in different circumstances and situations while yet managing to stick to their Sri Lankan roots.");
        dbHandler.addRecord("The Jam Fruit Tree","Carl Muller","Muller transports the readers into the past times of Lanka and gives us a hilarious and compelling biography on the lives of his people- the burghers of Sri Lanka. The Jam Fruit Tree is one of Muller’s best works and is the initial book in the Burgher trilogy, winning the Gratiaen Memorial Prize for best work in English Literature by a Sri Lankan in 1993.");
        dbHandler.addRecord("Funny Boy","Shyam Selvaduri","Arjie is an oddity- a funny boy. We follow his large family of affluent Tamils through his eyes as he comes to terms with his homosexuality and the racism of the society in which he lives. Violent roots and tragedy befalls as the novel progresses against the backdrop of the war between the army and the Tamil Tigers.");
        dbHandler.addRecord("On Sal Mal Lane","Ru Freeman","Sri Lanka, 1979. The Herath family has just moved to Sal Mal Lane, a quiet street disturbed only by the cries of the children whose triumphs and tragedies sustain the families that live there. As the neighbors adapt to the newcomers in different ways, the children fill their days with cricket matches, romantic crushes, and small rivalries.");
        dbHandler.addRecord("Colpetty People","Ashok Ferry","Ferrey’s debut covers the journey of Sri Lankans as they navigate worlds between Ceylon and the West.\n" +
                "Ferrey, born in Sri Lanka yet raised in East Africa, had this very novel shortlisted for the Gratiaen Prize in 2003.");

        dbHandler.addMembers("Lahiru Yasas",formattedTimestamp);
        dbHandler.addMembers("Kasunya Isurindi",formattedTimestamp);
        dbHandler.addMembers("Nimal Amunugama",formattedTimestamp);
        dbHandler.addMembers("Kasun Arawinda",formattedTimestamp);
        dbHandler.addMembers("Chandrika kodithuwakkuo",formattedTimestamp);
    }

    private void loadBooks() {

        bookContainer = findViewById(R.id.book_container);
        dbHandler = new DBHandler(this);
        bookList = new ArrayList<>();
        bookAdapter = new BookAdapter(bookList, this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        bookContainer.setLayoutManager(layoutManager);
        bookContainer.setAdapter(bookAdapter);

        SQLiteDatabase db = dbHandler.getReadableDatabase();
        String[] projection = {"rec_id", "title", "author", "description"};
        Cursor cursor = db.query("records", projection, null, null, null, null, "rec_id DESC");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int rec_id = Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow("rec_id")));
                String id = String.valueOf(rec_id);
                String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
                String author = cursor.getString(cursor.getColumnIndexOrThrow("author"));
                String desc = cursor.getString(cursor.getColumnIndexOrThrow("description"));

                Book book = new Book(id, title, author, desc);
                bookList.add(book);
            } while (cursor.moveToNext());

            cursor.close();
            bookAdapter.notifyDataSetChanged();
        }

        db.close();

    }

    private void loadUsers() {

        userContainer = findViewById(R.id.user_container);
        dbHandler = new DBHandler(this);
        userList = new ArrayList<>();
        userAdapter = new UserAdapter(userList, this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        userContainer.setLayoutManager(layoutManager);
        userContainer.setAdapter(userAdapter);

        SQLiteDatabase db = dbHandler.getReadableDatabase();
        String[] projection = {"mem_id", "name", "date"};
        Cursor cursor = db.query("members", projection, null, null, null, null, "mem_id DESC");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int rec_id = Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow("mem_id")));
                String id = String.valueOf(rec_id);
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));

                Users user = new Users(id, name, date);
                userList.add(user);
            } while (cursor.moveToNext());

            cursor.close();
            userAdapter.notifyDataSetChanged();
        }

        db.close();

    }

    public boolean isRecordsTableEmpty() {
        SQLiteDatabase db = dbHandler.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM records", null);
        if (cursor != null) {
            cursor.moveToFirst();
            int count = cursor.getInt(0);
            cursor.close();
            return count == 0;
        }
        return true; // Assume empty if cursor is null
    }

    public boolean isMembersTableEmpty() {
        SQLiteDatabase db = dbHandler.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM members", null);
        if (cursor != null) {
            cursor.moveToFirst();
            int count = cursor.getInt(0);
            cursor.close();
            return count == 0;
        }
        return true; // Assume empty if cursor is null
    }

}