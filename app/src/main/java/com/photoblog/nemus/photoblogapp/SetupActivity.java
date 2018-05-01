package com.photoblog.nemus.photoblogapp;

import android.*;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetupActivity extends AppCompatActivity {

    private CircleImageView setupImage;
    private EditText setupName;
    private Button setupBtn;
    private Uri mainImageUri = null;
    private StorageReference storageRef;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private ProgressBar setupProgres;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        Toolbar setupToolbar = (Toolbar) findViewById(R.id.setupToolbar);
        setSupportActionBar(setupToolbar);
        getSupportActionBar().setTitle("Account Setup");

        firebaseFirestore = FirebaseFirestore.getInstance();

        setupImage = (CircleImageView) findViewById(R.id.setup_image);
        setupName = (EditText) findViewById(R.id.setup_name);
        setupBtn = (Button) findViewById(R.id.setup_btn);
        setupProgres = (ProgressBar) findViewById(R.id.setup_progress);



        mFirebaseAuth = FirebaseAuth.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();

        setupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String userName = setupName.getText().toString();

                String uName = mFirebaseAuth.getCurrentUser().getEmail().toString();
                Toast.makeText(SetupActivity.this, uName, Toast.LENGTH_SHORT).show();

                if(!TextUtils.isEmpty(userName) && mainImageUri != null){



                    final String userId = mFirebaseAuth.getCurrentUser().getUid();

                    setupProgres.setVisibility(View.VISIBLE);

                    StorageReference imagePath = storageRef.child("profile_images").child(userId + ".jpg");

                    imagePath.putFile(mainImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                            if(task.isSuccessful()){

                                Uri downloadUri = task.getResult().getDownloadUrl();

                                Map<String, String> userMap = new HashMap<String, String>();
                                userMap.put("name", userName);
                                userMap.put("image", downloadUri.toString());

                                firebaseFirestore.collection("Users")
                                        .document(userId)
                                        .set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if(task.isSuccessful()){

                                                Toast.makeText(SetupActivity.this, "The user settings are updated: ", Toast.LENGTH_SHORT).show();
                                                Intent mainIntent = new Intent(SetupActivity.this, MainActivity.class);
                                                startActivity(mainIntent);
                                                finish();

                                            }else{

                                                String error = task.getException().getMessage();
                                                Toast.makeText(SetupActivity.this, "FIRESTORE ERROR: " + error, Toast.LENGTH_SHORT).show();
                                            }

                                        }
                                });

                            }else{

                                String error = task.getException().getMessage();
                                Toast.makeText(SetupActivity.this, "IMAGE ERROR: " + error, Toast.LENGTH_SHORT).show();
                                setupProgres.setVisibility(View.INVISIBLE);
                            }

                        }
                    });
                }
            }
        });


        setupImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(Build.VERSION.SDK_INT > Build.VERSION_CODES.M){
                    if(ContextCompat.checkSelfPermission(SetupActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

                        Toast.makeText(SetupActivity.this, "Permision denied", Toast.LENGTH_SHORT).show();
                        ActivityCompat.requestPermissions(SetupActivity.this, new String[]{ Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

                    }else{

                        bringImagePicker();
                    }
                    
                }else{

                    bringImagePicker();

                }
            }
        });
    }

    private void bringImagePicker() {
        // start picker to get image for cropping and then use the image in cropping activity
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .start(SetupActivity.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if(resultCode == RESULT_OK){

                mainImageUri = result.getUri();

                setupImage.setImageURI(mainImageUri);

            }else if(resultCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){

                Exception error = result.getError();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
