package com.example.android.questtime;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * RegisterActivity is used for registering users to an application.
 */

public class RegisterActivity extends AppCompatActivity{

    private ImageView profilePictureBtn;
    private EditText registerUsername;
    private EditText registerEmail;
    private EditText registerPassword;
    private EditText repeatRegisterPassword;
    private Button registerBtn;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    User user;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);
        overridePendingTransition(R.anim.activity_in, R.anim.activity_out);


        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        profilePictureBtn = (ImageView) findViewById(R.id.profilePictureBtn);
        registerUsername = (EditText) findViewById(R.id.registerUsername);
        registerEmail = (EditText) findViewById(R.id.registerEmail);
        registerPassword = (EditText) findViewById(R.id.registerPassword);
        repeatRegisterPassword = (EditText) findViewById(R.id.repeatRegisterPassword);
        registerBtn = (Button) findViewById(R.id.registerBtn);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (registerPassword.getText().toString().equals(repeatRegisterPassword.getText().toString())) {
                        register();
                        user = new User(registerUsername.getText().toString(), registerEmail.getText().toString());
                    } else {
                        Toast.makeText(RegisterActivity.this, "Passwords do not match.",
                                Toast.LENGTH_SHORT).show();
                    }
                } catch (IllegalArgumentException e){
                    Toast.makeText(RegisterActivity.this, "All fields are required.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

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
                            Toast.makeText(RegisterActivity.this,"User registration successfull",Toast.LENGTH_SHORT).show();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(RegisterActivity.this, "User registration failed. Please try again.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
    }
}
