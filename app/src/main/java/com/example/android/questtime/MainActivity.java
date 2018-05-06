package com.example.android.questtime;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
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

        roomListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Room room = (Room) roomListView.getItemAtPosition(i);

                Intent intent = new Intent(MainActivity.this, RoomActivity.class);
                intent.putExtra("roomName", room.getRoomName());
                startActivity(intent);
            }
        });

        settingsRotateAnimation = new RotateAnimation(0f, 180f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        settingsRotateAnimation.setRepeatCount(0);
        settingsRotateAnimation.setDuration(700);

        addRotateAnimation = new RotateAnimation(0f, 180f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        addRotateAnimation.setRepeatCount(0);
        addRotateAnimation.setDuration(700);

        mDatabase.child("users").child(mAuth.getUid()).child("rooms").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userRooms.clear();
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    mDatabase.child("rooms").child(snapshot.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot2) {
                            List<String> categories = new ArrayList<>();
                            for (DataSnapshot snapshot1: dataSnapshot2.child("categories").getChildren()) {
                                categories.add(snapshot1.getValue().toString());
                            }
                            Room addRoom = new Room("Proba", dataSnapshot2.child("difficulty").getValue().toString(), categories, Integer.parseInt(dataSnapshot2.child("numberOfUsers").getValue().toString()));
                            userRooms.add(addRoom);
                            adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
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
