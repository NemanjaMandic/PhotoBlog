package com.photoblog.nemus.photoblogapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

public class SetupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        Toolbar setupToolbar = (Toolbar) findViewById(R.id.setupToolbar);
        setSupportActionBar(setupToolbar);
        getSupportActionBar();
    }
}
