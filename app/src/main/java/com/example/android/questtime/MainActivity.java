package com.example.android.questtime;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
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

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{

    final static int DELETE_REQUEST_CODE = 123;
    final static int ADD_NEW_ACTIVITY = 234;
    final static int PUBLIC_ROOM_ADDED = 987;
    final static int NEW_QUESTION = 345;
    final static int QUESTION_UNANSWERED = 456;

    private ImageView settingsBtn;
    private ImageView addRoomBtn;
    private TextView questionsLeftNumber;
    private TextView questionsLeftTodayTextView;
    private RecyclerView roomListView;
    private TextView noRoomsTxt;

    private RotateAnimation settingsRotateAnimation;
    private RotateAnimation addRotateAnimation;

    private ArrayList<Room> userRooms = new ArrayList<>();
    private RecyclerRoomAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private int brojPitanja = 0;
    private Room addRoom;

    private double joined;
    private double created;
    private int answered;
    private int bodovi;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private MediaPlayer mp;
    private SwipeRefreshLayout swipeRefreshLayout;

    private SharedPreferences sharedPreferences;
    private boolean sound;

    private Parcelable recyclerViewState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        overridePendingTransition(R.anim.activity_in, R.anim.activity_out);

        mp = MediaPlayer.create(this, R.raw.sound);

        sharedPreferences = getSharedPreferences("com.example.android.questtime", MODE_PRIVATE);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeLayout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(Color.GRAY, Color.GREEN, Color.BLUE,
                Color.RED, Color.CYAN);
        swipeRefreshLayout.setDistanceToTriggerSync(20);// in dips
        swipeRefreshLayout.setSize(SwipeRefreshLayout.DEFAULT);// LARGE also can be used

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        settingsBtn = (ImageView) findViewById(R.id.settingsBtn);
        addRoomBtn = (ImageView) findViewById(R.id.addRoomBtn);
        questionsLeftNumber = (TextView) findViewById(R.id.questionsLeftNumber);
        questionsLeftTodayTextView = (TextView) findViewById(R.id.questionsLeftTodayTextView);
        noRoomsTxt = (TextView) findViewById(R.id.no_rooms_txt);

        roomListView = findViewById(R.id.roomListView);
        roomListView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        roomListView.setLayoutManager(layoutManager);
        adapter = new RecyclerRoomAdapter(this, userRooms, new ItemClickListenerInterface() {
            @Override
            public void onItemClick(View v, int position) {
                mp.start();
                Room room = (Room) userRooms.get(position);
                Intent intent = new Intent(MainActivity.this, RoomActivity.class);
                intent.putExtra("key", room.getKey());
                intent.putExtra("name", room.getRoomName());
                if(room.getType().equals("private")){
                    intent.putExtra("type", room.getType());
                    intent.putExtra("privateKey", room.getPrivateKey());
                } else {
                    intent.putExtra("type", room.getType());
                }
                startActivityForResult(intent, NEW_QUESTION);
            }

            @Override
            public void onLongItemClick(View v, int position) {
                mp.start();
                Room room = (Room) userRooms.get(position);
                Intent intent = new Intent(MainActivity.this, ExitRoomActivity.class);
                intent.putExtra("key", room.getKey());
                intent.putExtra("position", position);
                startActivityForResult(intent, DELETE_REQUEST_CODE);
            }
        });

        roomListView.addItemDecoration(new VerticalSpaceItemDecoration(20));

        roomListView.setAdapter(adapter);

        ucitajSobe();

        settingsRotateAnimation = new RotateAnimation(0f, 180f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        settingsRotateAnimation.setRepeatCount(0);
        settingsRotateAnimation.setDuration(700);

        addRotateAnimation = new RotateAnimation(0f, 180f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        addRotateAnimation.setRepeatCount(0);
        addRotateAnimation.setDuration(700);



        addRoomBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mp.start();
                addRoomBtn.startAnimation(addRotateAnimation);
                Intent intent = new Intent(MainActivity.this, PlusButtonActivity.class);
                startActivityForResult(intent, ADD_NEW_ACTIVITY);
            }
        });

        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mp.start();
                settingsBtn.startAnimation(settingsRotateAnimation);
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        refresh();
    }

    public void refresh() {
        layoutManager.removeAllViews();
        ucitajSobe();
        swipeRefreshLayout.setRefreshing(false);
    }

    public void ucitajSobe() {
        brojPitanja = 0;
        questionsLeftNumber.setText("0");
        userRooms.clear();
        mDatabase.child("users").child(mAuth.getUid()).child("rooms").addListenerForSingleValueEvent(new ValueEventListener() {
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
                            answered = 1;
                            for (DataSnapshot questions : dataSnapshot2.child("questions").getChildren()){
                                created = Double.parseDouble(questions.child("timestamp").getValue().toString());
                                try{
                                    bodovi = Integer.parseInt(questions.child("points").child(mAuth.getUid()).getValue().toString());
                                } catch (NullPointerException e){
                                    if(created > joined) {
                                        if(created*1000 < System.currentTimeMillis()){
                                            answered = -1;
                                        }
                                        brojPitanja++;
                                        questionsLeftNumber.setText(String.valueOf(brojPitanja));
                                        if(brojPitanja == 1){
                                            questionsLeftTodayTextView.setText("QUESTION LEFT TODAY");
                                        } else {
                                            questionsLeftTodayTextView.setText("QUESTIONS LEFT TODAY");
                                        }
                                    }
                                }
                            }
                            try{
                                addRoom = new Room(dataSnapshot2.child("roomName").getValue().toString(),
                                        dataSnapshot2.child("difficulty").getValue().toString(),
                                        categories,
                                        (int) dataSnapshot2.child("members").getChildrenCount(),
                                        snapshot.getKey(),
                                        dataSnapshot2.child("privateKey").getValue().toString(),
                                        dataSnapshot2.child("type").getValue().toString(),
                                        answered);
                            } catch (NullPointerException e){
                                addRoom = new Room(dataSnapshot2.child("roomName").getValue().toString(),
                                        dataSnapshot2.child("difficulty").getValue().toString(),
                                        categories,
                                        (int) dataSnapshot2.child("members").getChildrenCount(),
                                        snapshot.getKey(),
                                        dataSnapshot2.child("type").getValue().toString(),
                                        answered);
                            }
                            if(addRoom.getZastavica() == -1){
                                userRooms.add(0, addRoom);
                                adapter.notifyItemInserted(0);
                            } else {
                                userRooms.add(addRoom);
                                adapter.notifyItemInserted(userRooms.size() - 1);
                            }
                            if(userRooms.isEmpty()){
                                noRoomsTxt.setVisibility(View.VISIBLE);
                            } else {
                                noRoomsTxt.setVisibility(View.GONE);
                            }
                            layoutManager.scrollToPosition(0);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }

                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        if(requestCode == DELETE_REQUEST_CODE){
            if(resultCode == Activity.RESULT_OK){
                adapter.removeItem(data.getIntExtra("position", 0));
            }
        }
        if(requestCode == ADD_NEW_ACTIVITY) {
            if (resultCode == Activity.RESULT_OK) {
                final String roomId = data.getStringExtra("roomId");
                mDatabase.child("rooms").child(roomId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        List<String> categories = new ArrayList<>();
                        for (DataSnapshot snapshot : dataSnapshot.child("categories").getChildren()) {
                            categories.add(snapshot.getValue().toString());
                        }
                        try {
                            addRoom = new Room(dataSnapshot.child("roomName").getValue().toString(),
                                    dataSnapshot.child("difficulty").getValue().toString(),
                                    categories,
                                    (int) dataSnapshot.child("members").getChildrenCount(),
                                    roomId,
                                    dataSnapshot.child("privateKey").getValue().toString(),
                                    dataSnapshot.child("type").getValue().toString(),
                                    answered);
                        } catch (NullPointerException e) {
                            addRoom = new Room(dataSnapshot.child("roomName").getValue().toString(),
                                    dataSnapshot.child("difficulty").getValue().toString(),
                                    categories,
                                    (int) dataSnapshot.child("members").getChildrenCount(),
                                    roomId,
                                    dataSnapshot.child("type").getValue().toString(),
                                    answered);
                        }
                        userRooms.add(addRoom);
                        adapter.notifyItemInserted(userRooms.size() - 1);
                        layoutManager.scrollToPosition(userRooms.indexOf(addRoom));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
            if (resultCode == PUBLIC_ROOM_ADDED) {
                layoutManager.removeAllViews();
                ucitajSobe();
            }
        }
        if(requestCode == NEW_QUESTION){
            if(resultCode == QUESTION_UNANSWERED){
                layoutManager.removeAllViews();
                ucitajSobe();
            }
        }
    }
}
