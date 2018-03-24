package com.example.android.questtime;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


/**
 * Created by fgrebenac on 3/19/18.
 */

public class ProfileActivity extends AppCompatActivity{

    private TextView username;
    private TextView email;
    private Button changePasswordBtn;
    private Button logoutBtn;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);
        overridePendingTransition(R.anim.activity_in, R.anim.activity_out);

        username = (TextView) findViewById(R.id.profileUsername);
        email = (TextView) findViewById(R.id.profileEmail);
        changePasswordBtn = (Button) findViewById(R.id.changePasswordBtn);
        logoutBtn = (Button) findViewById(R.id.logoutBtn);

        changePasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, PasswordChangeActivity.class);
                startActivity(intent);
            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(ProfileActivity.this, LogoutActivity.class);
                startActivity(intent);

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
    }
}
