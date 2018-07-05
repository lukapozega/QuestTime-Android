package com.example.android.questtime;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

public class ExitRoomActivity extends AppCompatActivity {

    private TextView leaveTxt;
    private Button leaveBtn;
    private Button stayBtn;

    private String roomKey;
    private int position;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private SharedPreferences sharedPreferences;

    private MediaPlayer mp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exit_room_activity);

        mp = MediaPlayer.create(this, R.raw.sound);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        sharedPreferences = getSharedPreferences("com.example.android.questtime", MODE_PRIVATE);

        leaveTxt = (TextView) findViewById(R.id.leaveTxt);
        leaveBtn = (Button) findViewById(R.id.leaveBtn);
        stayBtn = (Button) findViewById(R.id.stayBtn);

        roomKey = getIntent().getStringExtra("key");
        position = getIntent().getIntExtra("position", 0);

        leaveTxt.setPaintFlags(leaveTxt.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        leaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sharedPreferences.getBoolean("Sound", true)) {
                    mp.start();
                }
                mDatabase.child("rooms").child(roomKey).child("members").child(mAuth.getUid()).removeValue();
                mDatabase.child("users").child(mAuth.getUid()).child("rooms").child(roomKey).removeValue();
                FirebaseMessaging.getInstance().unsubscribeFromTopic(roomKey);
                mDatabase.child("rooms").child(roomKey).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(!dataSnapshot.hasChild("members")){
                            mDatabase.child("rooms").child(roomKey).removeValue();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                Intent resultIntent = new Intent();
                resultIntent.putExtra("position", position);
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }
        });

        stayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sharedPreferences.getBoolean("Sound", true)) {
                    mp.start();
                }
                Intent resultIntent = new Intent();
                setResult(Activity.RESULT_CANCELED, resultIntent);
                finish();
            }
        });

        setFinishOnTouchOutside(true);
    }
}
