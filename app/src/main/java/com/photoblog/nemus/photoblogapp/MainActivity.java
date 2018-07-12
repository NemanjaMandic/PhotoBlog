package com.photoblog.nemus.photoblogapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    //private FirebaseAuth.AuthStateListener mAuthListener;

    private Toolbar mainToolbar;
    private FloatingActionButton addPostFab;

    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        mainToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        addPostFab = (FloatingActionButton) findViewById(R.id.add_post_btn);

        setSupportActionBar(mainToolbar);
        getSupportActionBar().setTitle("Foto Blog");

        addPostFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, NewPostActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser == null){

            sendToLogin();

        }else{

            currentUserId = mAuth.getCurrentUser().getUid();
            firebaseFirestore.collection("Users")
                    .document(currentUserId)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                            if(task.isSuccessful()){

                                if( ! task.getResult().exists()){

                                    Intent intent = new Intent(MainActivity.this, SetupActivity.class);
                                    startActivity(intent);
                                    finish();

                                }

                            }else{

                                String errorMessage = task.getException().getMessage();
                                Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){
            case R.id.action_logout_btn:
                logout();

                return true;

            case R.id.action_settings_btn:
                Intent intent = new Intent(MainActivity.this, SetupActivity.class);
                startActivity(intent);

                return true;
        }
        return false;
    }

    private void logout() {

        mAuth.signOut();

        sendToLogin();
    }

    private void sendToLogin() {
        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }
}
