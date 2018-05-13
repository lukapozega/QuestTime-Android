package com.example.android.questtime;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
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

    private MediaPlayer mp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        overridePendingTransition(R.anim.activity_in, R.anim.activity_out);

        mp = MediaPlayer.create(this, R.raw.sound);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        /*
        Checking if user is already signed in. If so, proceed to MainActivity
         */
        if(user != null){
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
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
                mp.start();
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

                mp.start();
                login();
            }
        });

        forgotPasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mp.start();
                Intent intent = new Intent(LoginActivity.this, PasswordResetActivity.class);
                startActivity(intent);
            }
        });

    }

    private void login(){
        try {
            mAuth.signInWithEmailAndPassword(emailLoginInput.getText().toString(),
                    passwordLoginInput.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(LoginActivity.this, "User login failed. Please try again.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }catch (IllegalArgumentException e){
                Toast.makeText(LoginActivity.this, "All fields are required.",
                        Toast.LENGTH_SHORT).show();
            }
        }

}
