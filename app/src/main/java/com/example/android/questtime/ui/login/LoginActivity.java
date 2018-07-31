package com.example.android.questtime.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.android.questtime.utils.media.ClickSound;
import com.example.android.questtime.ui.rooms.RoomsActivity;
import com.example.android.questtime.ui.password_reset.PasswordResetActivity;
import com.example.android.questtime.R;
import com.example.android.questtime.ui.register.RegisterActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by fgrebenac on 3/8/18.
 */

public class LoginActivity extends AppCompatActivity{

    private FirebaseAuth mAuth;
    private Button registerNowBtn;
    private Button forgotPasswordBtn;
    private Button loginBtn;
    private EditText emailLoginInput;
    private EditText passwordLoginInput;
    private ClickSound cs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        overridePendingTransition(R.anim.activity_in, R.anim.activity_out);



        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        cs = new ClickSound(this);

        /*
        Checking if user is already signed in. If so, proceed to RoomsActivity
         */
        if(user != null){
            Intent intent = new Intent(LoginActivity.this, RoomsActivity.class);
            startActivity(intent);
            finish();
        }

        registerNowBtn = (Button) findViewById(R.id.registerNowBtn);
        forgotPasswordBtn = (Button) findViewById(R.id.forgotPasswordBtn);
        loginBtn = (Button) findViewById(R.id.loginBtn);
        emailLoginInput = (EditText) findViewById(R.id.emailLoginInput);
        passwordLoginInput = (EditText) findViewById(R.id.passwordLoginInput);

        registerNowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cs.start();
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                try{
                    intent.putExtra("email", emailLoginInput.getText().toString());
                }catch (IllegalArgumentException e){

                }
                startActivity(intent);
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cs.start();
                login();
            }
        });

        forgotPasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cs.start();
                Intent intent = new Intent(LoginActivity.this, PasswordResetActivity.class);
                startActivity(intent);
            }
        });

    }

    private void login(){
        if(validateInput()) {
            mAuth.signInWithEmailAndPassword(emailLoginInput.getText().toString(),
                    passwordLoginInput.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Intent intent = new Intent(LoginActivity.this, RoomsActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                showError();
                            }
                        }
                    });
        }
    }

    private boolean validateInput() {
        boolean flag = true;
        if(!Patterns.EMAIL_ADDRESS.matcher(emailLoginInput.getText()).matches()) {
            flag = false;
            emailLoginInput.setError("Please enter valid email address.");
        }
        if(passwordLoginInput.getText().toString().length() < 6) {
            flag = false;
            passwordLoginInput.setError("Password must be at least 6 characters.");
        }
        return flag;
    }

    private void showError() {
        new AlertDialog.Builder(this)
                .setTitle("Login")
                .setMessage("User login failed! Please try again.")
                .setPositiveButton("OK", null)
                .create()
                .show();
    }

}
