package com.app.omada;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.hbb20.CountryCodePicker;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class Settings extends AppCompatActivity {

    Window window;
    private static final String URL_DATA = "HOST_LINK_GOES_HERE";
    String image;
    boolean checkNumber;
    EditText username,phone,password2,skills;
    CountryCodePicker ccp;
    Button submit;
    static ImageView photo;
    FloatingActionButton floatingActionButton,facebook;
    static Bitmap bitmap;
    private final int IMG_REQUEST = 1;
    EditText autoCompleteTextView;
    String code;
    AlertDialog.Builder alertDialog;
    String email;
    static String FacebookLink;
    TextInputLayout textInputLayout;
    String[] info;
    String status;
    private static final String URL_LOAD_DATA = "HOST_LINK_GOES_HERE";
    ArrayList<String> users = new ArrayList<>();
    boolean check = false;
    int updates = 0;
    boolean removed = false;
    @SuppressLint({"RestrictedApi", "WrongThread"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.background));
        autoCompleteTextView = findViewById(R.id.skills);
        textInputLayout = findViewById(R.id.textInputLayout6);
        photo = findViewById(R.id.imageView34);
        username = findViewById(R.id.username);
        phone = findViewById(R.id.phone);
        submit = findViewById(R.id.button14);
        ccp = (CountryCodePicker) findViewById(R.id.ccp);
        floatingActionButton = findViewById(R.id.floatingActionButton22);
        ccp.registerCarrierNumberEditText(phone);
        alertDialog = new AlertDialog.Builder(Settings.this,R.style.Theme_MaterialComponents_Dialog);
        facebook = findViewById(R.id.facebook);
        checkNumber = true;

        status = UsersInfo.getGender();
        email = UsersInfo.getEmail();
        loadUpdates();
        username.setText(UsersInfo.getUsername());
        String[] number= UsersInfo.getPhone().split("_");
        code = number[0];
        phone.setText(number[1]);
        autoCompleteTextView.setText(UsersInfo.getSkills());
        String str = UsersInfo.getImage();
        loadData();
        if(!str.equals("")){
            RequestOptions requestOptions = new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true);
            Glide.with(this)
                    .asBitmap()
                    .load(str)
                    .fitCenter()
                    .apply(requestOptions)
                    .placeholder(R.color.grayPlaceHolder)
                    .into(photo);
            new network(this).execute(str);
        }
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(bitmap != null) {
                    final PopupMenu menu = new PopupMenu(Settings.this,view);

                    menu.getMenu().add(1,1,1,"Upload photo");
                    menu.getMenu().add(1,2,2,"Remove photo");
                    menu.show();
                    System.out.println("1"+menu.getMenu().getItem(0).getItemId());
                    System.out.println("0"+menu.getMenu().getItem(1).getItemId());

                    menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            if (menuItem.getItemId() == 1) {
                                CropImage.activity()
                                        .setGuidelines(CropImageView.Guidelines.ON)
                                        .setAspectRatio(1, 1)
                                        .start(Settings.this);
                                return true;
                            }
                            if(menuItem.getItemId() == 2){
                                bitmap = null;
                                photo.setImageResource(R.mipmap.add_round);
                                menu.dismiss();
                                removed = true;
                                return true;
                            }
                            else
                                return false;
                        }
                    });
                }else if(bitmap == null){
                    CropImage.activity()
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .setAspectRatio(1, 1)
                            .start(Settings.this);
                }

            }
        });
        username.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (username.getText().toString().equals("")) {
                    username.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_fiber_manual_record_24_normal, 0);
                    username.setError("Please fill empty fields");
                }
                else {
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
                                        check = false;
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
        phone.addTextChangedListener(mTextWatcher);
        autoCompleteTextView.addTextChangedListener(mTextWatcherSkills);


        ccp.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                //Toast.makeText(getContext(), "Updated " + ccp.getSelectedCountryName(), Toast.LENGTH_SHORT).show();
                code = ccp.getSelectedCountryCode();

            }
        });


        /*if(UsersInfo.getGender().equals("startup")){
            facebook.setVisibility(View.VISIBLE);
            FacebookLink = UsersInfo.getSkills();
            if(enter_link.getLink()!=null)
                FacebookLink = enter_link.getLink();

        }else{*/
            autoCompleteTextView.setVisibility(View.VISIBLE);
            textInputLayout.setVisibility(View.VISIBLE);
            autoCompleteTextView.setText(UsersInfo.getSkills());

        //}
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean stop = false;
                if(status.equals("seeker")){
                    if(autoCompleteTextView.length() == 0){
                        stop = true;
                        Toast.makeText(Settings.this,"Please specify your skills to help others know you!",Toast.LENGTH_LONG).show();
                    }
                    else
                        stop = false;
                }
                if(phone.getText().toString().length() == 0){
                    Toast.makeText(Settings.this,"Fill the empty fields to proceed!",Toast.LENGTH_LONG).show();
                }

                else if(stop == true){
                    Toast.makeText(Settings.this,"Please specify your Facebook page for verification!",Toast.LENGTH_LONG).show();
                }
                else if(check){
                    Toast.makeText(Settings.this,"Username already taken!",Toast.LENGTH_LONG).show();
                }
                else{
                    if (checkNumber == true) {
                        final ProgressDialog progressDialog;
                        progressDialog = new ProgressDialog(Settings.this, android.app.AlertDialog.THEME_DEVICE_DEFAULT_DARK);
                        progressDialog.setMessage("Loading...");
                        progressDialog.setTitle("Updating");
                        progressDialog.setIndeterminate(false);
                        progressDialog.setCancelable(false);
                        progressDialog.show();
                        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_DATA, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    if (response.equals("Success")) {
                                        SQLiteDatabase myDatabase = openOrCreateDatabase("Users",MODE_PRIVATE,null);

                                        progressDialog.dismiss();
                                        alertDialog.setMessage("Updated successfully");

                                        /*if(status.equals("startup")) {
                                            UsersInfo.setPhone(code + "_" + phone.getText().toString());
                                            UsersInfo.setSkills(FacebookLink);
                                        }
                                        else{*/
                                            UsersInfo.setPhone(code + "_" + phone.getText().toString());
                                            UsersInfo.setSkills(autoCompleteTextView.getText().toString());
                                        //}

                                        if(bitmap == null ){
                                            UsersInfo.setImage("");
                                        }
                                        else{
                                            UsersInfo.setImage("https://localdata.000webhostapp.com/upload/images/"+UsersInfo.getUsername()+updates+".jpg");
                                        }
                                        myDatabase.execSQL("DROP TABLE IF EXISTS usersData");
                                        myDatabase.execSQL("INSERT INTO usersData (name,email,phone,status,skills,image,posts,totalposts)VALUES('"+UsersInfo.getUsername()+"','"+UsersInfo.getEmail()+"','"+UsersInfo.getPhone()+"','"+UsersInfo.getGender()+"','"+UsersInfo.getSkills()+"','"+UsersInfo.getImage()+"','"+UsersInfo.getPosts()+"','"+UsersInfo.getTotalposts()+"')");

                                    } else {
                                        progressDialog.dismiss();
                                        alertDialog.setMessage("An error occurred please check your connection");

                                    }
                                    alertDialog.setTitle(response);
                                    alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            Settings.this.finish();
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
                                        Settings.this.finish();
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
                                params.put("email", UsersInfo.getEmail());
                                params.put("phone", code + "_" + phone.getText().toString());
                                System.out.println("hereeeee" + status);
                                if (status.equals("startup")) {
                                    FacebookLink = UsersInfo.getSkills();

                                    params.put("skills", FacebookLink);
                                } else {
                                    params.put("skills", autoCompleteTextView.getText().toString());
                                }
                                if (bitmap != null) {
                                    String image = getStringImage(bitmap);
                                    Log.d("image", image);
                                    //passing the image to volley
                                    params.put("image", image);
                                }
                                if(bitmap == null && removed){
                                    System.out.println("entered here now");
                                    params.put("image", "removed");
                                }
                                updates++;
                                params.put("updates", String.valueOf(updates));


                                return params;
                            }
                        };
                        RequestQueue requestQueue = Volley.newRequestQueue(Settings.this);
                        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                                10000,
                                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                        requestQueue.add(stringRequest);

                    }

                }

            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == IMG_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();

        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(resultCode == RESULT_OK){
                Uri resultUri = result.getUri();
                try{
                    InputStream inputStream = getContentResolver().openInputStream(resultUri);
                    bitmap = BitmapFactory.decodeStream(inputStream);
                    photo.setImageBitmap(bitmap);
                    removed = false;
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
            /*try {

                InputStream inputStream = getContentResolver().openInputStream(path);
                bitmap = BitmapFactory.decodeStream(inputStream);
                photo.setImageBitmap(bitmap);
                G.imageDrawable = photo.getDrawable();
                Intent myIntent = new Intent(SignUp.this, fullScreenImage.class);
                SignUp.this.startActivity(myIntent);


            } catch (IOException e) {
                e.printStackTrace();
            }*/
    }
    private final TextWatcher mTextWatcherSkills = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if(autoCompleteTextView.getText().toString().equals("")){
                autoCompleteTextView.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
                autoCompleteTextView.setError("Please fill empty fields");
            }
            else{
                autoCompleteTextView.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
                autoCompleteTextView.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_baseline_done_24,0);
            }

        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };
    private final TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if(ccp.isValidFullNumber() != true){
                phone.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
                phone.setError("Wrong number format");
                checkNumber = false;
            }
            else{
                phone.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
                phone.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_baseline_done_24,0);
                checkNumber = true;

            }

        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };
    private void loadData(){


        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_LOAD_DATA, new Response.Listener<String>() {
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
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"Please check your internet connection", Toast.LENGTH_LONG).show();

            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(Settings.this);
        requestQueue.add(stringRequest);
    }
    private void loadUpdates(){


        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_LOAD_DATA, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray =  jsonObject.getJSONArray("users");
                    int count = jsonArray.length();
                    Log.i("FOOUUUNDD",String.valueOf(count));

                    for (int i = 0; i <jsonArray.length(); i++){
                        JSONObject o = jsonArray.getJSONObject(i);
                        if(o.getString("email").equals(email)) {
                            updates += Integer.parseInt(o.getString("updates"));
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
                Toast.makeText(getApplicationContext(),"Please check your internet connection", Toast.LENGTH_LONG).show();

            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(Settings.this);
        requestQueue.add(stringRequest);
    }
    public String getStringImage(Bitmap bmp) {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 15, byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;

    }
    public static boolean isValidEmail(CharSequence target){
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
class network extends AsyncTask<String , Void, Bitmap>{
    ProgressDialog progressDialog;
    Context context;
    public network(Context context){
        this.context = context;
    }
    @Override
    protected Bitmap doInBackground(String... strings) {
            try{
                URL url = new URL(strings[0]);
                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream inputStream = new  java.net.URL(strings[0]).openStream();
                Settings.bitmap = BitmapFactory.decodeStream(inputStream);
                return Settings.bitmap;

            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = new ProgressDialog(context, android.app.AlertDialog.THEME_DEVICE_DEFAULT_DARK);
        progressDialog.setMessage("Loading your data...");
        progressDialog.setTitle("Update");
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        progressDialog.dismiss();
        Settings.bitmap = bitmap;
    }
}
