package com.app.omada;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;

@RequiresApi(api = Build.VERSION_CODES.O)
public class forgotpassword extends AppCompatActivity {

    Window window;
    Button send,next,incorrect;
    EditText editText;
    LinearLayout linearLayout;
    ImageView imageView;
    TextView mail;
    private static final SecureRandom secureRandom = new SecureRandom();
    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder();
    private static final String URL_DATA = "HOST_LINK_GOES_HERE";
    ArrayList<String>users = new ArrayList<>();
    boolean found = false;
    String email = "";
    String image = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpassword);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.background2));
        linearLayout = findViewById(R.id.linearLayout6);
        send = findViewById(R.id.button17);
        next = findViewById(R.id.button19);
        incorrect = findViewById(R.id.button18);
        editText = findViewById(R.id.userReset);
        imageView = findViewById(R.id.profile_image);
        mail = findViewById(R.id.textView7);
        loadData();
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for(int i =0;i<users.size();i+=3){
                    if(users.get(i).equals(editText.getText().toString())){
                        email = users.get(i+1);
                        image = users.get(i+2);
                        found = true;
                        break;
                    }
                }
                if(found){
                    mail.setText(email);
                    if(!image.equals("")) {
                        Glide.with(forgotpassword.this)
                                .asBitmap()
                                .load(image)
                                .fitCenter()
                                .placeholder(R.color.grayPlaceHolder)
                                .into(imageView);
                    }
                    linearLayout.animate().translationX(0).setDuration(1000).start();
                    next.setEnabled(false);
                }
                else{
                    Toast.makeText(forgotpassword.this,"Username is not found",Toast.LENGTH_LONG).show();
                }

            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(forgotpassword.this,"An email has been sent!",Toast.LENGTH_LONG).show();
                                finish();
                            }
                        }
                    });
                }
        });
        incorrect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getBaseContext(),"Failed to reset password",Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }
    public static String generateToken(){
        byte[] randomBytes = new  byte[24];
        secureRandom.nextBytes(randomBytes);
        return base64Encoder.encodeToString(randomBytes);
    }
    private void loadData(){


        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_DATA, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray =  jsonObject.getJSONArray("users");
                    int count = jsonArray.length();
                    Log.i("FOOUUUNDD",String.valueOf(count));
                    for (int i = 0; i <jsonArray.length(); i++){
                        JSONObject o = jsonArray.getJSONObject(i);
                        users.add(o.getString("username"));
                        users.add(o.getString("email"));
                        users.add(o.getString("image"));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"Please check your internet connection", Toast.LENGTH_LONG);

            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(forgotpassword.this);
        requestQueue.add(stringRequest);
    }
}