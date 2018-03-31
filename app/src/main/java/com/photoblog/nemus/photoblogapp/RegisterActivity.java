package com.photoblog.nemus.photoblogapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {

    private EditText regEmailField, regPasswordField, regConfirmPassField;
    private Button regButton, regLoginBtn;
    private ProgressBar regProgressbar;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        regEmailField = (EditText) findViewById(R.id.reg_email);
        regPasswordField = (EditText) findViewById(R.id.reg_password);
        regConfirmPassField = (EditText) findViewById(R.id.reg_confirm_password);
        regButton = (Button) findViewById(R.id.reg_btn);
        regLoginBtn = (Button) findViewById(R.id.reg_login_btn);
        regProgressbar = (ProgressBar) findViewById(R.id.reg_progressbar);


        regLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });

        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = regEmailField.getText().toString();
                String pass = regPasswordField.getText().toString();
                String confirmPass = regConfirmPassField.getText().toString();

                if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(pass) && !TextUtils.isEmpty(confirmPass)){

                    if(TextUtils.equals(pass, confirmPass)){

                        regProgressbar.setVisibility(View.VISIBLE);

                        mAuth.createUserWithEmailAndPassword(email, pass)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if(task.isSuccessful()){

                                    Intent intent = new Intent(RegisterActivity.this, SetupActivity.class);
                                    startActivity(intent);
                                    finish();

                                }else{

                                    String errorMsg = task.getException().getMessage();
                                    Toast.makeText(RegisterActivity.this, errorMsg, Toast.LENGTH_SHORT).show();

                                }
                            }
                        });

                    }else{

                        Toast.makeText(RegisterActivity.this, "Passwords doesn't match", Toast.LENGTH_SHORT).show();

                    }
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser != null){
            sendToMain();
        }
    }

    private void sendToMain() {
        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
