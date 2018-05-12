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
    private TextView noRoomsTxt;

    private RotateAnimation settingsRotateAnimation;
    private RotateAnimation addRotateAnimation;

    private ArrayList<Room> userRooms = new ArrayList<>();
    private RoomAdapter adapter;

    private int brojPitanja = 0;
    private Room addRoom;

    private double joined;
    private double created;
    private int zastavica;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        overridePendingTransition(R.anim.activity_in, R.anim.activity_out);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        settingsBtn = (ImageView) findViewById(R.id.settingsBtn);
        addRoomBtn = (ImageView) findViewById(R.id.addRoomBtn);
        questionsLeftNumber = (TextView) findViewById(R.id.questionsLeftNumber);
        questionsLeftTodayTextView = (TextView) findViewById(R.id.questionsLeftTodayTextView);
        noRoomsTxt = (TextView) findViewById(R.id.no_rooms_txt);

        roomListView = findViewById(R.id.roomListView);
        adapter = new RoomAdapter(this, userRooms );
        roomListView.setAdapter(adapter);

        roomListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Room room = (Room) roomListView.getItemAtPosition(i);
                Intent intent = new Intent(MainActivity.this, RoomActivity.class);
                intent.putExtra("key", room.getKey());
                intent.putExtra("name", room.getRoomName());
                if(room.getType().equals("private")){
                    intent.putExtra("type", room.getType());
                    intent.putExtra("privateKey", room.getPrivateKey());
                } else {
                    intent.putExtra("type", room.getType());
                }
                startActivity(intent);
            }
        });

        roomListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Room room = (Room) roomListView.getItemAtPosition(i);
                Intent intent = new Intent(MainActivity.this, ExitRoomActivity.class);
                intent.putExtra("key", room.getKey());
                startActivity(intent);
                return true;
            }
        });

        settingsRotateAnimation = new RotateAnimation(0f, 180f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        settingsRotateAnimation.setRepeatCount(0);
        settingsRotateAnimation.setDuration(700);

        addRotateAnimation = new RotateAnimation(0f, 180f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        addRotateAnimation.setRepeatCount(0);
        addRotateAnimation.setDuration(700);



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
    protected void onResume() {
        super.onResume();
        brojPitanja = 0;
        userRooms.clear();
        mDatabase.child("users").child(mAuth.getUid()).child("rooms").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                for (final DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    mDatabase.child("rooms").child(snapshot.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot2) {
                            joined = Double.parseDouble(dataSnapshot2.child("members").child(mAuth.getUid()).getValue().toString());
                            List<String> categories = new ArrayList<>();
                            for (DataSnapshot snapshot1: dataSnapshot2.child("categories").getChildren()) {
                                categories.add(snapshot1.getValue().toString());
                            }
                            for (DataSnapshot questions : dataSnapshot2.child("questions").getChildren()){
                                created = Double.parseDouble(questions.child("timestamp").getValue().toString());
                                try{
                                    zastavica = Integer.parseInt(questions.child("points").child(mAuth.getUid()).getValue().toString());
                                } catch (NullPointerException e){
                                    if(created > joined && created < System.currentTimeMillis()/1000 && !questions.child("points").hasChild(mAuth.getUid())) {
                                        zastavica = -1;
                                        brojPitanja++;
                                        questionsLeftNumber.setText(String.valueOf(brojPitanja));
                                        if(brojPitanja == 1){
                                            questionsLeftTodayTextView.setText("QUESTION LEFT TODAY");
                                        }
                                    }
                                }
                            }
                            try{
                                addRoom = new Room(dataSnapshot2.child("roomName").getValue().toString(),
                                        dataSnapshot2.child("difficulty").getValue().toString(),
                                        categories,
                                        Integer.parseInt(dataSnapshot2.child("numberOfUsers").getValue().toString()),
                                        snapshot.getKey(),
                                        dataSnapshot2.child("privateKey").getValue().toString(),
                                        dataSnapshot2.child("type").getValue().toString(),
                                        zastavica);
                            } catch (NullPointerException e){
                                addRoom = new Room(dataSnapshot2.child("roomName").getValue().toString(),
                                        dataSnapshot2.child("difficulty").getValue().toString(),
                                        categories,
                                        snapshot.getKey(),
                                        dataSnapshot2.child("type").getValue().toString(),
                                        zastavica);
                            }

                            userRooms.add(addRoom);
                            adapter.notifyDataSetChanged();
                            noRoomsTxt.setVisibility(View.GONE);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
                if(userRooms.isEmpty()){
                    noRoomsTxt.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
