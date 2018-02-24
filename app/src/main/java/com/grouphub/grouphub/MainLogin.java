package com.grouphub.grouphub;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;

import static com.grouphub.grouphub.R.id.progressBar;

public class MainLogin extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private FirebaseAuth auth;
    private EditText signupInputEmail, signupInputPassword;
    private TextInputLayout signupInputLayoutEmail, signupInputLayoutPassword;
    private Button btnSignUp, btnLinkToLogin;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        auth = FirebaseAuth.getInstance();
        signupInputEmail = (EditText) findViewById(R.id.signup_input_email);
        signupInputPassword = (EditText) findViewById(R.id.signup_input_password);
        signupInputLayoutEmail = (TextInputLayout) findViewById(R.id.signup_input_layout_email);
        signupInputLayoutPassword = (TextInputLayout) findViewById(R.id.signup_input_layout_password);
        btnSignUp = (Button) findViewById(R.id.btn_signup);
        btnLinkToLogin = (Button) findViewById(R.id.btn_link_login);
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitForm();

            }
        });

        btnLinkToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }
    private void submitForm () {
        String email = signupInputEmail.getText().toString().trim();
        String password = signupInputPassword.getText().toString().trim();
        if (!checkEmail() || !checkPassword()) {
            return;
        }
        signupInputLayoutEmail.setErrorEnabled(false);
        signupInputLayoutPassword.setErrorEnabled(false);
        progressBar.setVisibility(View.VISIBLE);
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<com.google.firebase.auth.AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());
                        progressBar.setProgress(View.GONE);
                        if (!task.isSuccessful()) {
                            Log.d(TAG, "Authentication failed." + task.getException());
                        } else {
                            startActivity (new Intent(SignupActivity.this, UserActivity.class));
                            finish();
                        }
                    }
                });
        Toast.makeText(getApplicationContext(), "You are successfully Registered!", Toast.LENGTH_SHORT);
    }

    private boolean checkEmail() {
        String email = signupInputEmail.getText().toString().trim();
        if (email.isEmpty() || !isEmailValid(email)) {
            signupInputLayoutEmail.setErrorEnabled(true);
            signupInputLayoutEmail.setError(getString(R.string.err_msg_email));
            signupInputEmail.setError(getString(R.string.err_msg_required));
            requestFocus(signupInputEmail);
            return false;
        }
        signupInputLayoutEmail.setErrorEnabled(false);
        return true;
    }
    private boolean checkPassword() {
        String password = signupInputPassword.getText().toString().trim();
        if (password.isEmpty() || !isPasswordValid(password)) {
            signupInputLayoutPassword.setError(getString(R.string.err_msg_password));
            signupInputPassword.setError(getString(R.string.err_msg_required));
            requestFocus(signupInputPassword);
            return false;
        }
        signupInputLayoutPassword.setErrorEnabled(false);
        return true;
    }

    private static boolean isEmailValid(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    private static boolean isPasswordValid(String password) {
        return (password.length() >= 6);
    }
    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }
}
