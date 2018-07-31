package com.example.android.questtime.ui.settings;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;

import com.example.android.questtime.R;
import com.example.android.questtime.ui.login.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;

public class SettingsActivity extends AppCompatActivity {

    private Switch soundSwitch;
    private Switch notifSwitch;
    private Button logoutBtn;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_popup);

        sharedPreferences = getSharedPreferences("com.example.android.questtime", MODE_PRIVATE);
        AudioManager audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);

        logoutBtn = (Button) findViewById(R.id.logoutBtn);
        soundSwitch = (Switch) findViewById(R.id.soundSwitch);
        notifSwitch = (Switch) findViewById(R.id.notifSwitch);

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        soundSwitch.setChecked(sharedPreferences.getBoolean("Sound", true));

        soundSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(soundSwitch.isChecked()) {
                    SharedPreferences.Editor editor = getSharedPreferences("com.example.android.questtime", MODE_PRIVATE).edit();
                    editor.putBoolean("Sound", true);
                    editor.commit();
                } else {
                    SharedPreferences.Editor editor = getSharedPreferences("com.example.android.questtime", MODE_PRIVATE).edit();
                    editor.putBoolean("Sound", false);
                    editor.commit();
                }
            }
        });


        setFinishOnTouchOutside(true);

    }
}
