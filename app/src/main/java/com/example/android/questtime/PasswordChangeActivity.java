package com.example.android.questtime;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by fgrebenac on 3/19/18.
 */

public class PasswordChangeActivity extends AppCompatActivity {

    private EditText passwordChangeEmail;
    private Button passwordChangeSendBtn;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.password_change);
        overridePendingTransition(R.anim.activity_in, R.anim.activity_out);

        passwordChangeEmail = (EditText) findViewById(R.id.passwordChangeEmail);
        passwordChangeSendBtn = (Button) findViewById(R.id.sendPasswordChangeEmailBtn);

        mAuth = FirebaseAuth.getInstance();

        passwordChangeSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendEmail();
            }
        });
    }

    private void sendEmail(){
        try{
            mAuth.sendPasswordResetEmail(passwordChangeEmail.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(PasswordChangeActivity.this, "E-mail sent",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } catch (IllegalArgumentException e){
            Toast.makeText(PasswordChangeActivity.this, "Please enter a valid e-mail address",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
    }
}

