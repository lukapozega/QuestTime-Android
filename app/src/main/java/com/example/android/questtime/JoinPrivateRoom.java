package com.example.android.questtime;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

public class JoinPrivateRoom extends AppCompatActivity{

    private EditText privateKeyEnter;
    private Button joinRoom;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private MediaPlayer mp;

    private String privateKey;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.join_room_popup);

        privateKeyEnter = (EditText) findViewById(R.id.privateKeyEnter);
        joinRoom = (Button) findViewById(R.id.joinButton);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        mp = MediaPlayer.create(this, R.raw.sound);

        joinRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mp.start();
                privateKey = privateKeyEnter.getText().toString();
                mDatabase.child("rooms").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        boolean added = false;
                        int numberOfUsers;
                        for(DataSnapshot snapshot: dataSnapshot.getChildren()) {
                            if (snapshot.hasChild("privateKey")) {
                                if (privateKey.equals(snapshot.child("privateKey").getValue().toString())) {
                                    mDatabase.child("rooms").child(snapshot.getKey()).child("members").child(mAuth.getUid()).setValue(System.currentTimeMillis()/1000);
                                    mDatabase.child("users").child(mAuth.getUid()).child("rooms").child(snapshot.getKey()).setValue(true);
                                    FirebaseMessaging.getInstance().subscribeToTopic(snapshot.getKey());
                                    added = true;
                                    Intent resultIntent = new Intent();
                                    resultIntent.putExtra("roomId", snapshot.getKey());
                                    setResult(Activity.RESULT_OK, resultIntent);
                                }
                            }
                        }
                        if(added) {
                            Toast.makeText(JoinPrivateRoom.this, "You have successfully joined room!", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(JoinPrivateRoom.this, "No room with such private key!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

        setFinishOnTouchOutside(true);
    }
}
