package com.example.android.questtime.ui.register;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.questtime.utils.media.ClickSound;
import com.example.android.questtime.ui.rooms.RoomsActivity;
import com.example.android.questtime.R;
import com.example.android.questtime.data.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

/**
 * RegisterActivity is used for registering users to an application.
 */

public class RegisterActivity extends AppCompatActivity{

    private EditText registerUsername;
    private EditText registerEmail;
    private EditText registerPassword;
    private EditText repeatRegisterPassword;
    private Button registerBtn;
    private Bundle extras;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private ClickSound cs;

    User user;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);
        overridePendingTransition(R.anim.activity_in, R.anim.activity_out);

        initViews();

        extras = getIntent().getExtras();
        cs = new ClickSound(this);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();


        if(extras != null){
            String loginEmail = extras.getString("email");
            registerEmail.setText(loginEmail);
        }

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cs.start();
                if(validateInput()) {
                    register();
                    user = new User(registerUsername.getText().toString(), registerEmail.getText().toString());
                }
            }
        });

    }

    private void initViews() {
        registerUsername = findViewById(R.id.registerUsername);
        registerEmail = findViewById(R.id.registerEmail);
        registerPassword = findViewById(R.id.registerPassword);
        repeatRegisterPassword = findViewById(R.id.repeatRegisterPassword);
        registerBtn = findViewById(R.id.registerBtn);
    }

    private boolean validateInput() {
        boolean flag = true;
        if(registerPassword.getText().toString().length() < 6) {
            flag = false;
            registerPassword.setError("Password must be at least 6 characters.");
        }
        if(!repeatRegisterPassword.getText().toString().equals(registerPassword.getText().toString())) {
            flag = false;
            repeatRegisterPassword.setError("Passwords do not match.");
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(registerEmail.getText()).matches()) {
            flag = false;
            registerEmail.setError("Please enter valid email address.");
        }
        if(registerUsername.getText().toString().isEmpty()) {
            flag = false;
            registerUsername.setError("Please enter your username.");
        }
        return flag;
    }

    /**
     * Method used to start the user registration. Triggers on "registerBtn"button click.
     */
    private void register(){
        mAuth.createUserWithEmailAndPassword(registerEmail.getText().toString(), registerPassword.getText().toString())
                .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success and add user values to database
                            mDatabase.child("users").child(mAuth.getUid()).setValue(user);
                            mDatabase.child("users").child(mAuth.getUid()).child("registrationToken").setValue(FirebaseInstanceId.getInstance().getToken());
                            Toast.makeText(RegisterActivity.this,"User registration successfull",Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(RegisterActivity.this, RoomsActivity.class);
                            startActivity(intent);
                        } else {
                            showError();
                        }
                    }
                });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
    }

    private void showError() {
        new AlertDialog.Builder(this)
                .setTitle("Registration")
                .setMessage("User registration failed! Please try again.")
                .setPositiveButton("OK", null)
                .create()
                .show();
    }
}
