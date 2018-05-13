package com.example.android.questtime;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PeopleActivity extends AppCompatActivity{

    TextView goldName;
    TextView goldPoints;
    TextView silverName;
    TextView silverPoints;
    TextView bronzeName;
    TextView bronzePoints;

    ArrayList<PeoplePoints> lista = new ArrayList<>();

    ListView peopleListView;

    PeopleAdapter adapter;

    DatabaseReference mDatabase;

    String roomKey;

    Map<String, Integer> map = new HashMap<>();

    PeoplePoints pp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.people_activity);

        lista.clear();

        mDatabase = FirebaseDatabase.getInstance().getReference();

        roomKey = getIntent().getStringExtra("roomKey");

        goldName = (TextView) findViewById(R.id.gold_name);
        goldPoints = (TextView) findViewById(R.id.gold_points);
        silverName = (TextView) findViewById(R.id.silver_name);
        silverPoints = (TextView) findViewById(R.id.silver_points);
        bronzeName = (TextView) findViewById(R.id.bronze_name);
        bronzePoints = (TextView) findViewById(R.id.bronze_points);

        peopleListView = (ListView) findViewById(R.id.peopleListView);

        adapter = new PeopleAdapter(this, lista);

        peopleListView.setAdapter(adapter);

        mDatabase.child("rooms").child(roomKey).child("members").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {

                for (final DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    map.put(snapshot.getKey(), 0);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mDatabase.child("rooms").child(roomKey).child("questions").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot question : dataSnapshot.getChildren()){
                    for(DataSnapshot points : question.child("points").getChildren()){
                        map.put(points.getKey(), map.get(points.getKey()) + Integer.parseInt(points.getValue().toString()));
                    }
                }
                for(final Map.Entry<String, Integer> entry : map.entrySet()){

                    mDatabase.child("users").child(entry.getKey()).child("username").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            try {
                                pp = new PeoplePoints(dataSnapshot.getValue().toString(), entry.getValue());
                                lista.add(pp);
                                Collections.sort(lista);
                            }catch (NullPointerException e){

                            }

                            try {
                                goldName.setText(lista.get(0).getUsername());
                                goldPoints.setText(String.valueOf(lista.get(0).getPoints()));

                                silverName.setText(lista.get(1).getUsername());
                                silverPoints.setText(String.valueOf(lista.get(1).getPoints()));

                                bronzeName.setText(lista.get(2).getUsername());
                                bronzePoints.setText(String.valueOf(lista.get(2).getPoints()));
                            }catch (IndexOutOfBoundsException e){

                            }catch (NullPointerException e){

                            }

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

    }
}
