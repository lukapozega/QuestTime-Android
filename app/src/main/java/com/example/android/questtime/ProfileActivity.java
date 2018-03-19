package com.example.android.questtime;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by fgrebenac on 3/19/18.
 */

public class ProfileActivity extends AppCompatActivity{

    private ImageView profilePictureBtn;
    private TextView username;
    private TextView email;
    private Button changePasswordBtn;
    private Button logoutBtn;
    private LayoutInflater layoutInflater;
    private PopupWindow logoutPopup;
    private LinearLayout logoutLayout;
    private Button confirmLogoutBtn;
    private Button cancelLogoutBtn;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);
        overridePendingTransition(R.anim.activity_in, R.anim.activity_out);


        profilePictureBtn = (ImageView) findViewById(R.id.editProfilePicture);
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
