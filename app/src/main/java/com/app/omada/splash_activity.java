package com.app.omada;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class splash_activity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private  Handler handler = new Handler();
    boolean found = false;
    Boolean entered = false;
    ImageView imageView2;
    Window window;
    SQLiteDatabase database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        database = this.openOrCreateDatabase("Users",MODE_PRIVATE,null);
        imageView2 = findViewById(R.id.imageView2);
        try{
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        }catch (Exception e){
            e.printStackTrace();
        }
        //FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.background2));
        mAuth = FirebaseAuth.getInstance();
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(!isFinishing()){
                    if(found == true){
                        entered = true;
                        startActivity(new Intent(splash_activity.this,MainActivity.class));
                        finish();
                    }else{
                        startActivity(new Intent(splash_activity.this,log.class));
                        finish();
                    }

                }

            }

        }, 1000);
    }
    @Override
    protected void onPause(){
        super.onPause();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {

            }
        };
        handler.removeCallbacks(runnable);

    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser!=null){
            found = true;
            database.execSQL("CREATE TABLE IF NOT EXISTS usersData(name VARCHAR,email VARCHAR, phone VARCHAR, status VARCHAR, skills VARCHAR,image BLOB,posts INT,totalposts INT)");
            Cursor c = database.rawQuery("SELECT * FROM usersData",null);
            c.moveToFirst();
            int nameIndex = c.getColumnIndex("name");
            UsersInfo.setUsername(c.getString(nameIndex));
            int emailIndex = c.getColumnIndex("email");
            UsersInfo.setEmail(c.getString(emailIndex));
            int phoneIndex = c.getColumnIndex("phone");
            UsersInfo.setPhone(c.getString(phoneIndex));
            int statusIndex = c.getColumnIndex("status");
            UsersInfo.setGender(c.getString(statusIndex));
            int skillsIndex = c.getColumnIndex("skills");
            UsersInfo.setSkills(c.getString(skillsIndex));
            int imageIndex = c.getColumnIndex("image");
            UsersInfo.setImage(c.getString(imageIndex));
            int postsIndex = c.getColumnIndex("posts");
            UsersInfo.setPosts(c.getInt(postsIndex));
            int totalpostsIndex = c.getColumnIndex("totalposts");
            UsersInfo.setTotalposts(c.getInt(totalpostsIndex));


        }
    }
}
