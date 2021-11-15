package com.app.omada;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.hbb20.CountryCodePicker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Signup_next extends AppCompatActivity {

    private static final String URL_DATA = "HOST_LINK_GOES_HERE";
    private static final String URL_LOAD_DATA = "HOST_LINK_GOES_HERE";

    Window window;
    private FirebaseAuth mAuth;
    private String email, pass;

    EditText username, phone;
    CountryCodePicker ccp;
    RadioButton startup, seeker;
    Button submit;
    FloatingActionButton floatingActionButton;
    String mobile;
    String skill;
    String user, E;
    String status;
    AlertDialog.Builder alertDialog;
    String code;
    boolean check, checkNumber;

    TextInputLayout textInputLayout;
    FloatingActionButton facebook;
    public static boolean connected;
    static String FacebookLink;
    EditText autoCompleteTextView;
    TextView textView, link;
    ArrayList<String> users = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_next);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.background2));
        autoCompleteTextView = findViewById(R.id.skills);
        loadData();
        link = findViewById(R.id.textView9);
        username = findViewById(R.id.username);
        phone = findViewById(R.id.phone);
        startup = findViewById(R.id.radioButton);
        seeker = findViewById(R.id.radioButton2);
        ccp = (CountryCodePicker) findViewById(R.id.ccp);
        floatingActionButton = findViewById(R.id.floatingActionButton);
        ccp.registerCarrierNumberEditText(phone);
        textInputLayout = findViewById(R.id.textInputLayout6);
        facebook = findViewById(R.id.facebook);
        submit = findViewById(R.id.button20);
        code = "20";
        alertDialog = new AlertDialog.Builder(Signup_next.this, R.style.Theme_MaterialComponents_Dialog);
        status = "";
        check = false;
        checkNumber = false;
        textView = findViewById(R.id.textView16);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                email = "";
                pass = "";
            } else {
                email = extras.getString("email");
                pass = extras.getString("pass");
            }
        } else {
            email = (String) savedInstanceState.getSerializable("email");
            pass = (String) savedInstanceState.getSerializable("pass");
        }
        link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Signup_next.this, legal_policies.class);
                startActivity(intent);
            }
        });
        ccp.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                //Toast.makeText(getContext(), "Updated " + ccp.getSelectedCountryName(), Toast.LENGTH_SHORT).show();
                code = ccp.getSelectedCountryCode();

            }
        });
        autoCompleteTextView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (autoCompleteTextView.getText().toString().equals("")) {
                    autoCompleteTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_fiber_manual_record_24_normal, 0);
                    autoCompleteTextView.setError("Please fill empty fields");
                } else {
                    autoCompleteTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                    autoCompleteTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_done_24, 0);
                }
            }
        });
        username.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (username.getText().toString().equals("")) {
                    username.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_fiber_manual_record_24_normal, 0);
                    username.setError("Please fill empty fields");
                } else {
                    if (b == true) {
                        Log.i("focus:", "true");

                    } else {
                        try {
                            for (int j = 0; j < users.size(); j++) {
                                //Log.i("user:",users[i+1]);
                                if (users.get(j) != null) {
                                    if (users.get(j).equals(username.getText().toString())) {
                                        username.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_fiber_manual_record_24_normal, 0);
                                        username.setError("Already taken");

                                        //Toast.makeText(SignUp.this, "Username already taken!", Toast.LENGTH_LONG).show();
                                        Log.i("NoNo:", "Already here");
                                        check = true;
                                        break;
                                    } else {
                                        username.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

                                        username.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_done_24, 0);
                                    }


                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    if (!b) {

                    }
                }
            }
        });


        startup.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b == true) {

                    status = "Male";

                }
            }
        });
        seeker.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b == true) {

                    status = "Female";


                }
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user = username.getText().toString();
                mobile = phone.getText().toString();

                boolean stop = false;

                if (autoCompleteTextView.length() == 0) {
                    stop = true;
                    Toast.makeText(Signup_next.this, "Please specify your Bio to help others know you!", Toast.LENGTH_LONG).show();
                } else
                    stop = false;
                if (user.length() == 0 || mobile.length() == 0 || status.length() == 0) {
                    Toast.makeText(Signup_next.this, "Fill the empty fields to proceed!", Toast.LENGTH_LONG).show();
                } else if (stop == true) {
                    Toast.makeText(Signup_next.this, "Please specify your Facebook page for verification!", Toast.LENGTH_LONG).show();

                } else {
                    if (check == false && checkNumber == true) {

                        signup();
                    }
                }
            }
        });
        phone.addTextChangedListener(mTextWatcher);


    }

    private void signup() {
        mAuth = FirebaseAuth.getInstance();
        final ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(Signup_next.this, android.app.AlertDialog.THEME_DEVICE_DEFAULT_DARK);
        progressDialog.setMessage("Loading...");
        progressDialog.setTitle("Signing you up");
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(false);
        progressDialog.show();
        mAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            user.sendEmailVerification();
                            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_DATA, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        if (response.equals("Success")) {
                                            progressDialog.dismiss();
                                            alertDialog.setMessage("Now please sign in with your new account");
                                        } else {
                                            progressDialog.dismiss();
                                            alertDialog.setMessage("An error occurred please check your connection");

                                        }
                                        alertDialog.setTitle(response);
                                        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                ((sign_up_omada) sign_up_omada.context).finish();
                                                Signup_next.this.finish();
                                            }
                                        });
                                        AlertDialog alertDialog1 = alertDialog.create();
                                        alertDialog1.show();

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    alertDialog.setMessage("An error occurred please check your connection");
                                    alertDialog.setTitle("Failure");
                                    alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                            Signup_next.this.finish();
                                        }
                                    });
                                    AlertDialog alertDialog1 = alertDialog.create();
                                    alertDialog1.show();
                                }
                            }) {
                                @Override
                                protected Map<String, String> getParams() throws AuthFailureError {
                                    Map<String, String> params = new HashMap<>();
                                    params.put("name", username.getText().toString());
                                    params.put("password", "0");
                                    params.put("phone", code + "_" + mobile);
                                    System.out.println("hereeeee" + status);
                                    params.put("email", email);
                                    params.put("gender", status);
                                    System.out.println("hereeeee" + skill);
                                    if (status.equals("startup")) {
                                        if (FacebookLink != null)
                                            params.put("skills", FacebookLink);
                                    } else {
                                        params.put("skills", autoCompleteTextView.getText().toString());
                                    }

                                    params.put("posts", "0");
                                    return params;
                                }
                            };
                            RequestQueue requestQueue = Volley.newRequestQueue(Signup_next.this);
                            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                                    1000,
                                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                            requestQueue.add(stringRequest);

                            //updateUI(user);
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

                            // If sign in fails, display a message to the user.
                            /*Log.w("TAG", "signInWithEmail:failure", task.getException());
                            Toast.makeText(log.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();*/
                            //updateUI(null);
                        }
                    }
                });


    }
    private final TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (ccp.isValidFullNumber() != true) {
                phone.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_fiber_manual_record_24_normal, 0);
                phone.setError("Wrong number format");
                checkNumber = false;
            } else {
                phone.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                phone.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_done_24, 0);
                checkNumber = true;

            }

        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };
    @Override
    protected void onResume() {
        super.onResume();

    }

    private void loadData() {


        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_LOAD_DATA, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("users");
                    int count = jsonArray.length();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject o = jsonArray.getJSONObject(i);
                        users.add(o.getString("username"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Please check your internet connection", Toast.LENGTH_LONG).show();

            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(Signup_next.this);
        requestQueue.add(stringRequest);
    }
}