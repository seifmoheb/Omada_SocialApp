package com.app.omada;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Password extends AppCompatActivity {

    Window window;
    EditText oldPass,pass,cPass;
    Button button;
    boolean stop1 = false;
    boolean stop2 = false;
    boolean stop3 = false;
    AlertDialog.Builder alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.background));
        oldPass = findViewById(R.id.oldpass);
        pass = findViewById(R.id.pass);
        cPass = findViewById(R.id.cPass);
        button = findViewById(R.id.button16);
        alertDialog = new AlertDialog.Builder(Password.this,R.style.Theme_MaterialComponents_Dialog);
        ;



        oldPass.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(oldPass.getText().toString().equals("")){
                    oldPass.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_baseline_fiber_manual_record_24xxx,0);
                    oldPass.setError("Please fill empty fields");
                    stop1 = true;
                }

                else{
                    oldPass.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
                   // oldPass.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_baseline_done_24,0);
                    stop1 = false;
                }
            }
        });
        pass.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(pass.getText().toString().equals("")){
                    pass.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_baseline_fiber_manual_record_24xxx,0);
                    pass.setError("Please fill empty fields");
                    stop2 = true;
                }
                else if(pass.getText().toString().length() < 6){
                    pass.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_baseline_fiber_manual_record_24xxx,0);
                    pass.setError("Password shouldn't be less than 6 characters long!");
                    stop2 = true;
                }
                else{
                    pass.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
                    pass.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_baseline_done_24,0);
                    stop2 = false;
                }
            }
        });
        cPass.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(cPass.getText().toString().equals("")){
                    cPass.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_baseline_fiber_manual_record_24xxx,0);
                    cPass.setError("Please fill empty fields");
                    stop3 = true;
                }
                else if(!cPass.getText().toString().equals(pass.getText().toString())){
                    cPass.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_baseline_fiber_manual_record_24xxx,0);
                    cPass.setError("Passwords don't match");
                    stop3 = true;

                }
                else{
                    cPass.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
                    //cPass.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_baseline_done_24,0);
                    stop3 = false;
                }
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(oldPass.getText().toString().equals(pass.getText().toString())){
                    Toast.makeText(Password.this,"You didn't change the password",Toast.LENGTH_LONG).show();
                }
                else if(!stop1 && !stop2 && pass.getText().toString().equals(cPass.getText().toString())){
                    final ProgressDialog progressDialog;
                    progressDialog = new ProgressDialog(Password.this, android.app.AlertDialog.THEME_DEVICE_DEFAULT_DARK);
                    progressDialog.setMessage("Loading...");
                    progressDialog.setTitle("Updating Password");
                    progressDialog.setIndeterminate(false);
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    AuthCredential credential = EmailAuthProvider.getCredential(UsersInfo.getEmail(),oldPass.getText().toString());
                    user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            user.updatePassword(cPass.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        progressDialog.dismiss();
                                        alertDialog.setMessage("Updated successfully");
                                    }else{

                                        alertDialog.setMessage(task.getException().getMessage());

                                    }
                                    alertDialog.setTitle("Password update");
                                    alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            Password.this.finish();
                                        }
                                    });
                                    AlertDialog alertDialog1 = alertDialog.create();
                                    alertDialog1.show();
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            alertDialog.setMessage(e.getMessage());
                        }
                    });


                }
            }
        });
    }
}
