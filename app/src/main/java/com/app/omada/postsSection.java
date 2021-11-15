package com.app.omada;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RequiresApi(api = Build.VERSION_CODES.O)
public class postsSection extends AppCompatActivity {
    TextView textView;
    Button addPhoto, addPost, deleteImage;
    FloatingActionButton back;
    private final int IMG_REQUEST = 1;
    Bitmap bitmap;
    ImageView imageView, imageView2;
    EditText editText;
    String imageStr, nameStr, timeStr;
    static int postsNum, totalpostsNum;
    String image;
    private static final String URL_DATA = "HOST_LINK_GOES_HERE";
    private static final String URL_DATA_2 = "HOST_LINK_GOES_HERE";
    AlertDialog.Builder alertDialog;
    static int charCount = 0;
    ProgressBar progressBar;
    int newPostId;
    Window window;
    private static final SecureRandom secureRandom = new SecureRandom();
    private static final java.util.Base64.Encoder base64Encoder = java.util.Base64.getUrlEncoder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posts_section);
        ActionBar actionbar = getSupportActionBar();
        actionbar.hide();
        alertDialog = new AlertDialog.Builder(postsSection.this, R.style.Theme_MaterialComponents_Dialog);

        progressBar = findViewById(R.id.progressBar);
        editText = findViewById(R.id.editText4);
        blink();

        addPhoto = findViewById(R.id.button8);
        back = findViewById(R.id.button10);
        addPost = findViewById(R.id.button11);
        imageView = findViewById(R.id.imageView13);
        deleteImage = findViewById(R.id.deleteImage);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        addPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, IMG_REQUEST);


            }
        });

        deleteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageView.setImageBitmap(null);
                bitmap = null;
                deleteImage.setVisibility(View.INVISIBLE);
                addPhoto.setVisibility(View.VISIBLE);
            }
        });
        window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.background));

        totalpostsNum = 0;
        nameStr = UsersInfo.getUsername();
        imageView2 = findViewById(R.id.imageView14);
        imageStr = UsersInfo.getImage();
        postsNum = UsersInfo.getPosts();
        if (!imageStr.equals("")) {
            RequestOptions requestOptions = new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true);
            Glide.with(this)
                    .asBitmap()
                    .load(imageStr)
                    .fitCenter()
                    //.apply(requestOptions)
                    .override(150, 50)
                    .placeholder(R.color.grayPlaceHolder)
                    .into(imageView2);
        }

        addPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editText.length() == 0) {
                } else {

                    Date currentTime = Calendar.getInstance().getTime();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                    timeStr = dateFormat.format(currentTime);
                    final ProgressDialog progressDialog;
                    progressDialog = new ProgressDialog(postsSection.this, android.app.AlertDialog.THEME_DEVICE_DEFAULT_DARK);
                    progressDialog.setMessage("Loading...");
                    progressDialog.setTitle("Adding your post");
                    progressDialog.setIndeterminate(false);
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_DATA, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                progressDialog.dismiss();
                                UsersInfo.setPosts(postsNum);
                                UsersInfo.setTotalposts(totalpostsNum);

                                update(String.valueOf(postsNum), String.valueOf(totalpostsNum));
                                alertDialog.setMessage("Your post has been successfully added");

                                alertDialog.setTitle(response);
                                alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        finish();
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

                                    finish();
                                }
                            });
                            AlertDialog alertDialog1 = alertDialog.create();
                            alertDialog1.show();
                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> params = new HashMap<>();
                            params.put("username", nameStr);
                            params.put("descr", editText.getText().toString());
                            params.put("userImage", imageStr);
                            params.put("time", timeStr);
                            if (bitmap != null) {
                                image = getStringImage(bitmap);
                                Log.d("upload:", image);
                                params.put("photo", image);
                            } else {
                                params.put("photo", "");
                            }

                            postsNum = UsersInfo.getPosts();
                            postsNum++;
                            totalpostsNum = UsersInfo.getTotalposts();
                            totalpostsNum++;
                            params.put("totalposts", String.valueOf(totalpostsNum));
                            return params;
                        }
                    };
                    RequestQueue requestQueue = Volley.newRequestQueue(postsSection.this);
                    stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                            10000,
                            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                    requestQueue.add(stringRequest);
                }
            }
        });
        editText.addTextChangedListener(mTextWatcher);
    }

    private final TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            charCount = charSequence.length();
            progressBar.setProgress(charCount);
            if (charCount > 0) {
                addPost.setTextColor(Color.WHITE);
            } else {
                addPost.setTextColor(Color.GRAY);

            }

        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMG_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri path = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(path);
                bitmap = BitmapFactory.decodeStream(inputStream);

                imageView.setImageBitmap(bitmap);
                deleteImage.setVisibility(View.VISIBLE);
                addPhoto.setVisibility(View.INVISIBLE);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getStringImage(Bitmap bmp) {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);

        return encodedImage;
    }

    public void update(final String p, final String p2) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_DATA_2, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    if (response.equals("Success")) {

                    } else {
                        Toast.makeText(getApplicationContext(), "An Error occurred", Toast.LENGTH_SHORT).show();
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Error occurred", Toast.LENGTH_SHORT).show();
            }
        }) {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("name", nameStr);
                params.put("posts", p);
                params.put("totalposts", p2);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(postsSection.this);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);


    }

    private void blink() {
        if (shouldBlink()) {
            editText.setText(editText.getText());
            editText.setPressed(true);
            editText.setSelection(editText.getText().length());
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    blink();
                }
            }, 1000);
        }
    }

    private boolean shouldBlink() {
        if (!editText.isCursorVisible() || !editText.isFocused())
            return false;
        else
            return true;
    }

}
