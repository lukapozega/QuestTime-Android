package com.example.android.questtime;

import android.content.Intent;
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

/**
 * Created by fgrebenac on 3/19/18.
 */

public class PlusButtonActivity extends AppCompatActivity {

    private Button joinRoomButton;
    private Button createNewRoomButton;
    private Button joinPrivateRoom;
    private Button joinPublicRoom;
    private EditText privateKeyEnter;
    private Button joinRoom;
    private String privateKey;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_room_popup);

        joinRoomButton = (Button) findViewById(R.id.joinRoomBtn);
        createNewRoomButton = (Button) findViewById(R.id.createNewRoomBtn);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        createNewRoomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PlusButtonActivity.this, CreateNewRoom.class);
                startActivity(intent);
                finish();
            }
        });

        joinRoomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setContentView(R.layout.join_room_popup);
                joinPrivateRoom = (Button) findViewById(R.id.joinPrivateRoom);
                joinPublicRoom = (Button) findViewById(R.id.joinPublicRoom);
                privateKeyEnter = (EditText) findViewById(R.id.privateKeyEnter);
                joinRoom = (Button) findViewById(R.id.joinButton);

                joinPrivateRoom.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        joinPrivateRoom.setVisibility(View.GONE);
                        joinPublicRoom.setVisibility(View.GONE);
                        privateKeyEnter.setVisibility(View.VISIBLE);
                        joinRoom.setVisibility(View.VISIBLE);
                    }
                });

                joinRoom.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
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
                                            numberOfUsers = Integer.parseInt(snapshot.child("numberOfUsers").getValue().toString());
                                            if(!snapshot.child("members").hasChild(mAuth.getUid())) {
                                                mDatabase.child("rooms").child(snapshot.getKey()).child("numberOfUsers").setValue(++numberOfUsers);
                                            }
                                            mDatabase.child("users").child(mAuth.getUid()).child("rooms").child(snapshot.getKey()).setValue(true);
                                            added = true;
                                        }
                                    }
                                }
                                if(added) {
                                    Toast.makeText(PlusButtonActivity.this, "You have successfully joined room!", Toast.LENGTH_SHORT).show();
                                    finish();
                                } else {
                                    Toast.makeText(PlusButtonActivity.this, "No room with such private key!", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                });

            }
        });

        setFinishOnTouchOutside(true);

    }
}
