package com.app.omada;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.view.View.*;

public class log extends AppCompatActivity {
    private FirebaseAuth mAuth;

    TextInputEditText editText;
    TextInputEditText passText;
    TextView textView;
    Button button, buttonSignUp;
    private static final String URL_DATA = "https://localdata.000webhostapp.com/db.php";
    String username, phone, gender, skills, image;
    int posts, totalposts;
    Window window;
    ArrayList<String> users = new ArrayList<>();
    TextView textView3;
    AlertDialog.Builder alertDialog;
    ProgressDialog progressDialog;
    SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log);

        ActionBar actionBar = getSupportActionBar();
        alertDialog = new AlertDialog.Builder(log.this, R.style.Theme_MaterialComponents_Dialog);
        actionBar.hide();
        mAuth = FirebaseAuth.getInstance();
        editText = findViewById(R.id.editText);
        passText = findViewById(R.id.editText2);
        button = findViewById(R.id.button);
        buttonSignUp = findViewById(R.id.button2);
        textView = findViewById(R.id.textView);
        window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.background2));
        textView3 = findViewById(R.id.button3);
        textView3.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(log.this, forgotpassword.class);
                startActivity(intent);
            }
        });


        database = openOrCreateDatabase("Users",MODE_PRIVATE,null);
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    boolean found = false;

                    if (editText.getText().toString().length() == 0 || passText.getText().toString().length() == 0) {
                        Toast.makeText(getApplicationContext(), "Please fill empty fields!", Toast.LENGTH_LONG).show();
                    } else {
                        progressDialog = new ProgressDialog(log.this, android.app.AlertDialog.THEME_DEVICE_DEFAULT_DARK);
                        progressDialog.setMessage("Loading...");
                        progressDialog.setTitle("Logging you in");
                        progressDialog.setIndeterminate(false);
                        progressDialog.setCancelable(false);
                        progressDialog.show();

                        signin(editText.getText().toString(), passText.getText().toString());
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        buttonSignUp.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(log.this, sign_up_omada.class);
                log.this.startActivity(myIntent);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void signin(final String email, final String pass) {

        mAuth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "signInWithEmail:success");
                            username = "";
                            phone = "";
                            gender = "";
                            skills = "";
                            image = "";
                            posts = 0;
                            totalposts = 0;
                            final FirebaseUser user = mAuth.getCurrentUser();

                            if (user.isEmailVerified()) {
                                StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_DATA, new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        try {
                                            JSONObject jsonObject = new JSONObject(response);
                                            JSONArray jsonArray = jsonObject.getJSONArray("users");
                                            int count = jsonArray.length();
                                            Log.i("FOOUUUNDD", String.valueOf(count));
                                            for (int i = 0; i < jsonArray.length(); i++) {
                                                JSONObject o = jsonArray.getJSONObject(i);
                                                if (o.getString("email").equals(editText.getText().toString())) {
                                                    username = o.getString("username");
                                                    phone = o.getString("phone");
                                                    gender = o.getString("gender");
                                                    skills = o.getString("skills");
                                                    image = o.getString("image");
                                                    posts = o.getInt("posts");
                                                    totalposts = Integer.parseInt(o.getString("totalposts"));

                                                        UsersInfo.setUsername(username);
                                                        UsersInfo.setEmail(email);
                                                        UsersInfo.setPhone(phone);
                                                        UsersInfo.setGender(gender);
                                                        UsersInfo.setSkills(skills);
                                                        UsersInfo.setImage(image);
                                                        UsersInfo.setPosts(posts);
                                                        UsersInfo.setTotalposts(totalposts);
                                                    database.execSQL("DROP TABLE IF EXISTS usersData");
                                                    database.execSQL("CREATE TABLE IF NOT EXISTS usersData(name VARCHAR,email VARCHAR, phone VARCHAR, status VARCHAR, skills VARCHAR,image BLOB,posts INT,totalposts INT)");
                                                    database.execSQL("INSERT INTO usersData (name,email,phone,status,skills,image,posts,totalposts)VALUES('"+username+"','"+email+"','"+phone+"','"+gender+"','"+skills+"','"+image+"','"+posts+"','"+totalposts+"')");
                                                        Intent myIntent = new Intent(log.this, MainActivity.class);
                                                        myIntent.putExtra("email",editText.getText().toString());
                                                    log.this.startActivity(myIntent);
                                                    log.this.finish();

                                                    break;
                                                }
                                            }


                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Toast.makeText(log.this, "Failed to log in", Toast.LENGTH_LONG).show();
                                        progressDialog.dismiss();
                                        FirebaseAuth.getInstance().signOut();
                                    }
                                });

                                RequestQueue requestQueue = Volley.newRequestQueue(log.this);
                                requestQueue.add(stringRequest);


                            } else {
                                Toast.makeText(log.this, "Please verify your email address to log in", Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                                FirebaseAuth.getInstance().signOut();
                            }


                        } else {
                            task.addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.dismiss();
                                    alertDialog.setMessage(e.getMessage());
                                    alertDialog.setTitle("Failure");
                                    alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                        }
                                    });
                                    AlertDialog alertDialog1 = alertDialog.create();
                                    alertDialog1.show();
                                }
                            });
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthInvalidUserException e) {

                            } catch (Exception e) {
                                progressDialog.dismiss();
                                alertDialog.setMessage(e.getMessage());
                                alertDialog.setTitle("Failure");
                                alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                });
                                AlertDialog alertDialog1 = alertDialog.create();
                                alertDialog1.show();
                                e.printStackTrace();
                            }
                        }


                    }
                });
    }
}
