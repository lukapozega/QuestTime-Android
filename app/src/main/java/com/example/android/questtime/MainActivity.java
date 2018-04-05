package com.example.android.questtime;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ImageView settingsBtn;
    private ImageView addRoomBtn;
    private TextView questionsLeftNumber;
    private TextView questionsLeftTodayTextView;
    private ListView roomListView;

    private RotateAnimation settingsRotateAnimation;
    private RotateAnimation addRotateAnimation;

    private ArrayList<Room> userRooms = new ArrayList<>();
    private RoomAdapter adapter;


    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        overridePendingTransition(R.anim.activity_in, R.anim.activity_out);

        settingsBtn = (ImageView) findViewById(R.id.settingsBtn);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        addRoomBtn = (ImageView) findViewById(R.id.addRoomBtn);
        questionsLeftNumber = (TextView) findViewById(R.id.questionsLeftNumber);
        questionsLeftTodayTextView = (TextView) findViewById(R.id.questionsLeftTodayTextView);

        roomListView = findViewById(R.id.roomListView);
        adapter = new RoomAdapter(this, userRooms );
        roomListView.setAdapter(adapter);

        settingsRotateAnimation = new RotateAnimation(0f, 180f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        settingsRotateAnimation.setRepeatCount(0);
        settingsRotateAnimation.setDuration(1500);

        addRotateAnimation = new RotateAnimation(0f, 180f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        addRotateAnimation.setRepeatCount(0);
        addRotateAnimation.setDuration(1000);

        mDatabase.child("rooms").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userRooms.clear();
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    List<String> categories = new ArrayList<>();
                    for (DataSnapshot snapshot1: snapshot.child("categories").getChildren()) {
                        //Log.i("test", snapshot1.getValue().toString());
                        categories.add(snapshot1.getValue().toString());
                    }
//                    for(String s: categories) {
//                        Log.i("test", s);
//                    }
                    Room addRoom = new Room(snapshot.child("roomName").getValue().toString(), snapshot.child("difficulty").getValue().toString(), categories, 6);
                    userRooms.add(addRoom);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        addRoomBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addRoomBtn.startAnimation(addRotateAnimation);
                Intent intent = new Intent(MainActivity.this, PlusButtonActivity.class);
                startActivity(intent);

            }
        });

        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settingsBtn.startAnimation(settingsRotateAnimation);
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
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
