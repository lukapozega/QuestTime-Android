package com.example.android.questtime.ui.rooms;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.questtime.R;
import com.example.android.questtime.data.models.Room;
import com.example.android.questtime.ui.join_room.CreateOrJoinActivity;
import com.example.android.questtime.ui.room.RoomActivity;
import com.example.android.questtime.ui.room.leave_room.ExitRoomActivity;
import com.example.android.questtime.ui.settings.SettingsActivity;
import com.example.android.questtime.utils.media.MediaUtils;
import com.example.android.questtime.utils.recycler.ItemClickListenerInterface;
import com.example.android.questtime.utils.recycler.VerticalSpaceItemDecoration;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class RoomsActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{

    final static int DELETE_REQUEST_CODE = 123;
    final static int ADD_NEW_ACTIVITY = 234;
    final static int PUBLIC_ROOM_ADDED = 987;
    final static int NEW_QUESTION = 345;
    final static int QUESTION_UNANSWERED = 456;
    static final int QUESTION_ANSWERED = 567;

    private ImageView settingsBtn;
    private ImageView addRoomBtn;
    private TextView questionsLeftNumber;
    private TextView questionsLeftTodayTextView;
    private RecyclerView roomListView;
    private TextView noRoomsTxt;
    private LinearLayout noConnection;

    private RotateAnimation rotateAnimation;

    private ArrayList<Room> userRooms = new ArrayList<>();
    private HashMap<String,String> questionsLeft = new HashMap<>();
    private RecyclerRoomAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private int numberOfQuestions = 0;
    private Room addRoom;

    private double joined;
    private double created;
    private int answered;
    private String left;
    private boolean connection = true;
    private Iterator<Map.Entry<String,String>> iterator;
    private long lastUpdated;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        overridePendingTransition(R.anim.activity_in, R.anim.activity_out);

        context = this;
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeLayout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(Color.CYAN, Color.BLUE, Color.GREEN,
                Color.RED);
        swipeRefreshLayout.setDistanceToTriggerSync(20);
        swipeRefreshLayout.setSize(SwipeRefreshLayout.DEFAULT);
        swipeRefreshLayout.setEnabled(false);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        noConnection = (LinearLayout) findViewById(R.id.no_connection);
        settingsBtn = (ImageView) findViewById(R.id.settingsBtn);
        addRoomBtn = (ImageView) findViewById(R.id.addRoomBtn);
        questionsLeftNumber = (TextView) findViewById(R.id.questionsLeftNumber);
        questionsLeftTodayTextView = (TextView) findViewById(R.id.questionsLeftTodayTextView);
        noRoomsTxt = (TextView) findViewById(R.id.no_rooms_txt);

        roomListView = findViewById(R.id.roomListView);
        roomListView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        roomListView.setLayoutManager(layoutManager);
        adapter = new RecyclerRoomAdapter(userRooms, new ItemClickListenerInterface() {
            @Override
            public void onItemClick(View v, int position) {
                MediaUtils.playButtonClick(context);
                Room room = (Room) userRooms.get(position);
                Intent intent = new Intent(RoomsActivity.this, RoomActivity.class);
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
                MediaUtils.playButtonClick(context);
                Room room = (Room) userRooms.get(position);
                Intent intent = new Intent(RoomsActivity.this, ExitRoomActivity.class);
                intent.putExtra("key", room.getKey());
                intent.putExtra("position", position);
                startActivityForResult(intent, DELETE_REQUEST_CODE);
            }
        });

        roomListView.addItemDecoration(new VerticalSpaceItemDecoration(20));

        roomListView.setAdapter(adapter);

        if (isNetworkConnected()) {
            swipeRefreshLayout.setRefreshing(true);
            loadRooms();
        } else {
            noConnection.setVisibility(View.VISIBLE);
            connection=false;
            swipeRefreshLayout.setEnabled(true);
        }

        rotateAnimation = new RotateAnimation(0f, 180f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setRepeatCount(0);
        rotateAnimation.setDuration(700);

        addRoomBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MediaUtils.playButtonClick(context);
                addRoomBtn.startAnimation(rotateAnimation);
                Intent intent = new Intent(RoomsActivity.this, CreateOrJoinActivity.class);
                startActivityForResult(intent, ADD_NEW_ACTIVITY);
            }
        });

        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MediaUtils.playButtonClick(context);
                settingsBtn.startAnimation(rotateAnimation);
                Intent intent = new Intent(RoomsActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        Log.i("1","1");
        if (isNetworkConnected()) {
            Log.i("1","2");
            if (!connection) {
                Log.i("1","3");
                swipeRefreshLayout.setEnabled(false);
                loadRooms();
                noConnection.setVisibility(View.GONE);
                connection=true;
            } else {
                refresh();
            }
        } else {
            swipeRefreshLayout.setRefreshing(false);
            if (noConnection.getVisibility()==View.GONE) {
                Toast.makeText(RoomsActivity.this, R.string.no_connection, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void refresh() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(lastUpdated);
        Calendar cal1 = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        if (cal.get(Calendar.DAY_OF_WEEK)!=cal1.get(Calendar.DAY_OF_WEEK)) {
            loadRooms();
        } else {
            refreshRooms();
        }
        lastUpdated = System.currentTimeMillis();
    }

    public void loadRooms() {
        numberOfQuestions = 0;
        questionsLeftNumber.setText("0");
        lastUpdated = System.currentTimeMillis();
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
                            left = "false";
                            for (DataSnapshot questions : dataSnapshot2.child("questions").getChildren()){
                                created = Double.parseDouble(questions.child("timestamp").getValue().toString());
                                if (!questions.child("points").hasChild(mAuth.getUid())) {
                                    if(created > joined) {
                                        if(created*1000 < System.currentTimeMillis()){
                                            answered = -1;
                                        } else {
                                            numberOfQuestions++;
                                            left = questions.getKey();
                                        }
                                        questionsLeftNumber.setText(String.valueOf(numberOfQuestions));
                                        if(numberOfQuestions == 1){
                                            questionsLeftTodayTextView.setText("QUESTION LEFT TODAY");
                                        } else {
                                            questionsLeftTodayTextView.setText("QUESTIONS LEFT TODAY");
                                        }
                                    }
                                }
                            }
                            if (answered==1 && !left.equals("false")) {
                                questionsLeft.put(snapshot.getKey(),left);
                            }
                            if (dataSnapshot2.hasChild("privateKey")) {
                                addRoom = new Room(dataSnapshot2.child("roomName").getValue().toString(),
                                        dataSnapshot2.child("difficulty").getValue().toString(),
                                        categories,
                                        (int) dataSnapshot2.child("members").getChildrenCount(),
                                        snapshot.getKey(),
                                        dataSnapshot2.child("privateKey").getValue().toString(),
                                        dataSnapshot2.child("type").getValue().toString(),
                                        answered);
                            } else {
                                addRoom = new Room(dataSnapshot2.child("roomName").getValue().toString(),
                                        dataSnapshot2.child("difficulty").getValue().toString(),
                                        categories,
                                        (int) dataSnapshot2.child("members").getChildrenCount(),
                                        snapshot.getKey(),
                                        dataSnapshot2.child("type").getValue().toString(),
                                        answered);
                            }
                            if(userRooms.contains(addRoom)) {
                                int position = userRooms.indexOf(addRoom);
                                if (addRoom.getAnswered()==-1 && userRooms.get(position).getAnswered()!=-1 ) {
                                    userRooms.get(position).setAnswered(-1);
                                    userRooms.remove(position);
                                    userRooms.add(0, addRoom);
                                    adapter.notifyItemMoved(position,0);
                                    layoutManager.scrollToPosition(0);
                                }
                            } else {
                                if (addRoom.getAnswered()==-1) {
                                    userRooms.add(0, addRoom);
                                    adapter.notifyItemInserted(0);
                                    layoutManager.scrollToPosition(0);
                                } else {
                                    userRooms.add(addRoom);
                                    adapter.notifyItemInserted(userRooms.size() - 1);
                                }
                            }
                            if(userRooms.isEmpty()){
                                noRoomsTxt.setVisibility(View.VISIBLE);
                            } else {
                                noRoomsTxt.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
                swipeRefreshLayout.setRefreshing(false);
                swipeRefreshLayout.setEnabled(true);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void refreshRooms() {
        iterator = questionsLeft.entrySet().iterator();
        while (iterator.hasNext()) {
            final Map.Entry<String,String> entry = iterator.next();
            mDatabase.child("rooms").child(entry.getKey()).child("questions").child(entry.getValue()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot3) {
                    created = Double.parseDouble(dataSnapshot3.child("timestamp").getValue().toString());
                    if (created*1000 < System.currentTimeMillis()) {
                        int position = 0;
                        for (Room userRoom : userRooms) {
                            if (userRoom.getKey().equals(entry.getKey())) {
                                position = userRooms.indexOf(userRoom);
                            }
                        }
                        Room roomToMove = userRooms.get(position);
                        roomToMove.setAnswered(-1);
                        userRooms.remove(position);
                        userRooms.add(0, roomToMove);
                        adapter.notifyItemMoved(position, 0);
                        adapter.notifyItemChanged(0);
                        layoutManager.scrollToPosition(0);
                        numberOfQuestions--;
                        iterator.remove();
                        questionsLeftNumber.setText(String.valueOf(numberOfQuestions));
                        if (numberOfQuestions == 1) {
                            questionsLeftTodayTextView.setText("QUESTION LEFT TODAY");
                        } else {
                            questionsLeftTodayTextView.setText("QUESTIONS LEFT TODAY");
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        swipeRefreshLayout.setRefreshing(false);
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
                        if (dataSnapshot.hasChild("privateKey")) {
                            addRoom = new Room(dataSnapshot.child("roomName").getValue().toString(),
                                    dataSnapshot.child("difficulty").getValue().toString(),
                                    categories,
                                    (int) dataSnapshot.child("members").getChildrenCount(),
                                    dataSnapshot.getKey(),
                                    dataSnapshot.child("privateKey").getValue().toString(),
                                    dataSnapshot.child("type").getValue().toString(),
                                    1);
                        } else {
                            addRoom = new Room(dataSnapshot.child("roomName").getValue().toString(),
                                    dataSnapshot.child("difficulty").getValue().toString(),
                                    categories,
                                    (int) dataSnapshot.child("members").getChildrenCount(),
                                    dataSnapshot.getKey(),
                                    dataSnapshot.child("type").getValue().toString(),
                                    1);
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
                loadRooms();
            }
        }
        if(requestCode == NEW_QUESTION){
            int selectedPosition = 0;
            String selectedRoom = data.getStringExtra("selectedRoom");
            for (Room i: userRooms) {
                if (i.getKey().equals(selectedRoom)) {
                    selectedPosition = userRooms.indexOf(i);
                }
            }
            Room r = userRooms.get(selectedPosition);
            if(resultCode == QUESTION_UNANSWERED && r.getAnswered()==1){
                r.setAnswered(-1);
                userRooms.remove(selectedPosition);
                userRooms.add(0, r);
                adapter.notifyItemMoved(selectedPosition,0);
                adapter.notifyItemChanged(0);
            } else if (resultCode==QUESTION_ANSWERED && r.getAnswered()==-1) {
                r.setAnswered(1);
                userRooms.remove(selectedPosition);
                userRooms.add(r);
                adapter.notifyItemMoved(selectedPosition,userRooms.size()-1);
                adapter.notifyItemChanged(userRooms.size()-1);
            }
            layoutManager.scrollToPosition(0);
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }

}
