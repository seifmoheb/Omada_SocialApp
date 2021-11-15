package com.app.omada;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import static com.app.omada.Settings.isValidEmail;


public class sign_up_omada extends AppCompatActivity {

    Window window;
    Button signup;
    EditText email,pass;
    static Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_omada);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.background2));
        signup = findViewById(R.id.button20);
        email = findViewById(R.id.email);
        pass = findViewById(R.id.confirmpassword);
        context = this;
        email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (email.getText().toString().equals("")) {

                    email.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_fiber_manual_record_24_normal, 0);
                    email.setError("Please fill this field");

                } else {
                    if (isValidEmail(email.getText().toString())) {
                        email.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                        //email.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_done_24, 0);
                    } else {
                        email.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_fiber_manual_record_24_normal, 0);
                        email.setError("Wrong email format");
                    }
                }
            }
        });
        pass.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (pass.getText().toString().equals("")) {
                    pass.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_fiber_manual_record_24_normal, 0);
                    pass.setError("Please fill empty fields");
                } else {
                    pass.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                    //pass.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_done_24, 0);
                }
            }
        });
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                if(email.getText().toString().length() != 0 && pass.getText().toString().length() != 0){
                    if(pass.getText().toString().length() < 6){
                        pass.setError("Password should be at least 6 characters long");
                    }else{
                    Intent intent = new Intent(sign_up_omada.this,Signup_next.class);
                    intent.putExtra("email",email.getText().toString());
                    intent.putExtra("pass",pass.getText().toString());
                    startActivity(intent);}
                }
            }
        });
    }

}