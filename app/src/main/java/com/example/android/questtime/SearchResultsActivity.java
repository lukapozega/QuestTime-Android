package com.example.android.questtime;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SearchResultsActivity extends AppCompatActivity {

    private ListView roomList;
    private SearchRoomAdapter adapter;
    private ArrayList<Room> searchResultRooms = new ArrayList<>();

    private ArrayList<String> categories = new ArrayList<>();
    private ArrayList<String> userIds = new ArrayList<>();
    private String searchString;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_results_activity);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        roomList = (ListView) findViewById(R.id.searchResultsList);
        adapter = new SearchRoomAdapter(this, searchResultRooms);
        roomList.setAdapter(adapter);

        categories = getIntent().getStringArrayListExtra("categories");
        searchString = getIntent().getStringExtra("searchText");

        mDatabase.child("rooms").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                searchResultRooms.clear();

                for(final DataSnapshot snapshot : dataSnapshot.getChildren()){
                    if(snapshot.child("type").getValue().toString().equals("public")){
                        ArrayList<String> roomCategories = new ArrayList<>();
                        userIds.clear();
                        for (DataSnapshot snapshot1: snapshot.child("categories").getChildren()) {
                            roomCategories.add(snapshot1.getValue().toString());
                        }
                        for(DataSnapshot snapshot2: snapshot.child("members").getChildren()){
                            userIds.add(snapshot2.getKey());
                        }

                        Room addRoom = new Room(snapshot.child("roomName").getValue().toString(),
                                                snapshot.child("difficulty").getValue().toString(),
                                                roomCategories,
                                                snapshot.getKey(), userIds);

                        if(addRoom.getRoomName().contains(searchString) && (checkCategories(categories, roomCategories) || categories.isEmpty()) && !userIds.contains(mAuth.getUid())){
                            searchResultRooms.add(addRoom);
                            adapter.notifyDataSetChanged();
                        }

                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    

    public boolean checkCategories(ArrayList<String> categories, ArrayList<String> roomCategories){

        for(String category : categories){
            if(roomCategories.contains(category)) return true;
        }

        return false;
    }
}
