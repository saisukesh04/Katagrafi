package com.undamped.katagraf;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.google.android.material.snackbar.Snackbar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StartActivity extends AppCompatActivity {

    @BindView(R.id.apple_btn) Button apple_btn;
    @BindView(R.id.google_btn) Button google_btn;
    @BindView(R.id.email_btn) Button email_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        ButterKnife.bind(this);

        email_btn.setOnClickListener(view -> startActivity(new Intent(StartActivity.this, LoginActivity.class)));

        apple_btn.setOnClickListener(view -> Snackbar.make(view, "Feature coming soon", Snackbar.LENGTH_LONG).show());

        google_btn.setOnClickListener(view -> Snackbar.make(view, "Feature coming soon", Snackbar.LENGTH_LONG).show());
    }
}