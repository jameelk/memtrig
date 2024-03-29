package com.memtrig.account;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.memtrig.R;
import com.memtrig.activities.MainActivity;
import com.memtrig.globals.App;
import com.memtrig.globals.Helpers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUp extends AppCompatActivity {

    private static SignUp instance;
    private TextInputEditText email;
    private TextInputEditText password;
    private AppCompatButton login;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;


    public static SignUp getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        if (MainActivity.getInstance() != null) {
            MainActivity.getInstance().finish();
        }
        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.signup_login);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        progressBar = findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);
        login = findViewById(R.id.login);
        login.setText("SignUp");
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mail = email.getText().toString();
                String pwd = password.getText().toString();
                if (mail == null || mail.trim().isEmpty()) {
                    Snackbar.make(findViewById(android.R.id.content), "please enter your email",
                            Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if (pwd == null || pwd.trim().isEmpty()) {
                    Snackbar.make(findViewById(android.R.id.content), "please enter your password",
                            Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if (!isEmailValid(mail)) {
                    Snackbar.make(findViewById(android.R.id.content), "please enter valid email",
                            Snackbar.LENGTH_SHORT).show();
                    return;

                }
                if (mail != null && !mail.isEmpty() && pwd != null && !pwd.isEmpty()) {
                    progressBar.setVisibility(View.VISIBLE);
                    proceedToLogin(mail, pwd);
                }

            }
        });

    }

    private void proceedToLogin(final String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Helpers.hideKeyboard(SignUp.this);
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            App.saveLogin(true);
                            App.saveString(App.EMAIL, email);
                            progressBar.setVisibility(View.GONE);
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "createUserWithEmail:failure"+ task.getException().getLocalizedMessage().toString());
                            Snackbar.make(findViewById(android.R.id.content), task.getException().getLocalizedMessage(),
                                    Snackbar.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }

    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
