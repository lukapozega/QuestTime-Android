package com.example.android.questtime;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private LinearLayout linearLayout;
    private ImageView profileBtn;
    private ImageView addRoomBtn;
    private TextView questionsLeftNumber;
    private TextView questionsLeftTodayTextView;
    private ListView roomListView;
    private Button joinRoom;
    private Button createRoom;
    private ArrayList<Room> userRooms = new ArrayList<>();
    private RoomAdapter adapter;

    private PopupWindow addRoomPopup;
    private LayoutInflater layoutInflater;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        overridePendingTransition(R.anim.activity_in, R.anim.activity_out);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        linearLayout = (LinearLayout) findViewById(R.id.linear);
        profileBtn = (ImageView) findViewById(R.id.profileBtn);
        addRoomBtn = (ImageView) findViewById(R.id.addRoomBtn);
        questionsLeftNumber = (TextView) findViewById(R.id.questionsLeftNumber);
        questionsLeftTodayTextView = (TextView) findViewById(R.id.questionsLeftTodayTextView);

        roomListView = findViewById(R.id.roomListView);
        adapter = new RoomAdapter(this, userRooms );
        roomListView.setAdapter(adapter);

        mDatabase.child("rooms").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userRooms.clear();
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    Room addRoom = new Room(snapshot.child("roomName").getValue().toString(), snapshot.child("difficulty").getValue().toString(), Arrays.asList("Art", "Math"), 6);
                    userRooms.add(addRoom);
                }
                Log.i("Test", Integer.toString(userRooms.size()));
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        addRoomBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this, PlusButtonActivity.class);
                startActivity(intent);

            }
        });

        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
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
