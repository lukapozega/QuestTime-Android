package com.example.android.questtime;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fgrebenac on 3/19/18.
 */

public class PlusButtonActivity extends AppCompatActivity {

    private Button joinPrivateRoomButton;
    private Button createNewRoomButton;
    private Button joinPublicRoomButton;
    private EditText privateKeyEnter;
    private Button joinRoom;
    private String privateKey;
    private EditText searchRoomsEditText;
    private Button searchBtn;

    private ArrayList<String> categories = new ArrayList();


    private LinearLayout cat1Layout;
    private LinearLayout cat2Layout;
    private LinearLayout cat3Layout;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    private MediaPlayer mp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_room_popup);

        mp = MediaPlayer.create(this, R.raw.sound);

        joinPrivateRoomButton = (Button) findViewById(R.id.joinPrivateRoomBtn);
        joinPublicRoomButton = (Button) findViewById(R.id.joinPublicRoomBtn);
        createNewRoomButton = (Button) findViewById(R.id.createNewRoomBtn);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        createNewRoomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mp.start();
                Intent intent = new Intent(PlusButtonActivity.this, CreateNewRoom.class);
                startActivity(intent);
                finish();
            }
        });

        joinPrivateRoomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mp.start();
                setContentView(R.layout.join_room_popup);
                privateKeyEnter = (EditText) findViewById(R.id.privateKeyEnter);
                joinRoom = (Button) findViewById(R.id.joinButton);

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

        joinPublicRoomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mp.start();
                setContentView(R.layout.join_public_room_popup);
                searchBtn = (Button) findViewById(R.id.searchBtn);
                searchRoomsEditText = (EditText) findViewById(R.id.searchEditText);
                cat1Layout = (LinearLayout) findViewById(R.id.cat_1);
                cat2Layout = (LinearLayout) findViewById(R.id.cat_2);
                cat3Layout = (LinearLayout) findViewById(R.id.cat_3);

                for (int i=0; i<4;++i) {
                    View v = cat1Layout.getChildAt(i);
                    v.setOnClickListener(catClick);
                }
                for (int i=0; i<4;++i) {
                    View v = cat2Layout.getChildAt(i);
                    v.setOnClickListener(catClick);
                }
                for (int i=0; i<4; ++i){
                    View v = cat3Layout.getChildAt(i);
                    v.setOnClickListener(catClick);
                }

                searchBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mp.start();
                        Intent intent = new Intent(PlusButtonActivity.this, SearchResultsActivity.class);
                        intent.putExtra("searchText", searchRoomsEditText.getText().toString());
                        intent.putStringArrayListExtra("categories", categories);

                        startActivity(intent);
                    }
                });

            }
        });

        setFinishOnTouchOutside(true);

    }

    View.OnClickListener catClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mp.start();
            if (view.getAlpha()==1) {
                categories.remove((String) view.getTag());
                view.setAlpha(0.4f);
            } else {
                view.setAlpha(1f);
                categories.add((String) view.getTag());
            }
        }
    };
}
