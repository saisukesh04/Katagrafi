package com.undamped.katagraf;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
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
    @BindView(R.id.allergyText) EditText allergyText;
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
            Intent intent = new Intent(RegisterActivity.this, StartActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        registerBtn.setOnClickListener(v -> {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            View focusedView = RegisterActivity.this.getCurrentFocus();
            if (focusedView != null) {
                imm.hideSoftInputFromWindow(focusedView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }

            String name = userName.getText().toString();
            String allergy = allergyText.getText().toString();
            String email = emailREditText.getText().toString();
            String password = passwordREditText.getText().toString();
            String confPassword = confirmPassEditText.getText().toString();

            if (email.isEmpty() || password.isEmpty() || confPassword.isEmpty() || name.isEmpty() || allergy.isEmpty())
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
                                SharedPreferences prefs = getSharedPreferences("ProfilePrefs", MODE_PRIVATE);
                                SharedPreferences.Editor Ed = prefs.edit();
                                Ed.putString("Name", name);
                                Ed.putString("Allergy", allergy);
                                Ed.apply();
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