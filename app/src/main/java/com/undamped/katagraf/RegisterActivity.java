package com.undamped.katagraf;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RegisterActivity extends AppCompatActivity {

    @BindView(R.id.userName) EditText userName;
    @BindView(R.id.emailREditText) EditText emailREditText;
    @BindView(R.id.passwordREditText) EditText passwordREditText;
    @BindView(R.id.confirmPassEditText) EditText confirmPassEditText;
    @BindView(R.id.registerBtn) Button registerBtn;
    @BindView(R.id.clickLogin) TextView clickLogin;
    @BindView(R.id.regProgressBar) ProgressBar regProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ButterKnife.bind(this);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        clickLogin.setOnClickListener(view -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });

        registerBtn.setOnClickListener(v -> {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            View focusedView = RegisterActivity.this.getCurrentFocus();
            if (focusedView != null) {
                imm.hideSoftInputFromWindow(focusedView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }

            String email = emailREditText.getText().toString();
            String password = passwordREditText.getText().toString();
            String confPassword = confirmPassEditText.getText().toString();

            if (email.isEmpty() || password.isEmpty() || confPassword.isEmpty())
                Snackbar.make(v, "Please fill all the fields", Snackbar.LENGTH_LONG).show();
            else if (password.length() < 8)
                Snackbar.make(v, "Password should contain at least 8 characters", Snackbar.LENGTH_LONG).show();
            else if (!confPassword.equals(password))
                Snackbar.make(v, "Passwords don't match", Snackbar.LENGTH_LONG).show();
            else {
                regProgressBar.setVisibility(View.VISIBLE);
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(RegisterActivity.this, task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(RegisterActivity.this, "Registration successful. Logging in", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                                //TODO : storeUserDataToDb();
                            } else {
                                regProgressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(RegisterActivity.this, "Registration failed. Try again", Toast.LENGTH_LONG).show();
                                Log.e("Error: Register", "Error while adding data to database\n" + task.getException());
                            }
                        });
            }
        });
    }
}