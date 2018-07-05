package com.example.android.questtime;

import android.content.SharedPreferences;
import android.media.MediaPlayer;
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
 * Created by fgrebenac on 3/15/18.
 */

public class PasswordResetActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText emailInput;
    private Button sendEmailBtn;

    private SharedPreferences sharedPreferences;

    private MediaPlayer mp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_password_activity);
        overridePendingTransition(R.anim.activity_in, R.anim.activity_out);

        mp = MediaPlayer.create(this, R.raw.sound);

        sharedPreferences = getSharedPreferences("com.example.android.questtime", MODE_PRIVATE);

        emailInput = (EditText) findViewById(R.id.passwordResetEmail);
        sendEmailBtn = (Button) findViewById(R.id.sendEmailBtn);

        mAuth = FirebaseAuth.getInstance();


        sendEmailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sharedPreferences.getBoolean("Sound", true)) {
                    mp.start();
                }
                sendEmail();
            }
        });
    }

    private void sendEmail(){
        try{
            mAuth.sendPasswordResetEmail(emailInput.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(PasswordResetActivity.this, "E-mail sent",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } catch (IllegalArgumentException e){
            Toast.makeText(PasswordResetActivity.this, "Please enter a valid e-mail address",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
    }
}
