package com.example.android.questtime;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
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

public class RoomActivity extends AppCompatActivity {

    ListView questionsList;
    ArrayList<Question> questions = new ArrayList<>();
    QuestionAdapter adapter;
    TextView roomNameTitle;
    TextView roomKeyTextView;
    String roomKey;
    String roomName;
    String roomPrivateKey;
    Long joined;
    Long created;

    ImageView peopleButton;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.room_activity);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        peopleButton = (ImageView) findViewById(R.id.peopleBtn);
        roomNameTitle = (TextView) findViewById(R.id.roomNameTitle);
        roomKeyTextView = (TextView) findViewById(R.id.roomKey);


        questionsList = findViewById(R.id.questions_list_view);
        adapter = new QuestionAdapter(this, questions);
        questionsList.setAdapter(adapter);

        questionsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Question question = (Question) questionsList.getItemAtPosition(i);
                Intent intent = new Intent(RoomActivity.this, AnswerActivity.class);
                intent.putExtra("key", question.getId());
                intent.putExtra("points", question.getPoints());
                intent.putExtra("category", question.getCategory());
                startActivity(intent);
            }
        });

        roomKey = getIntent().getStringExtra("key");
        roomName = getIntent().getStringExtra("name");
        roomPrivateKey = getIntent().getStringExtra("privateKey");

        roomNameTitle.setText(roomName);
        roomKeyTextView.setText(roomPrivateKey);

        mDatabase.child("rooms").child(roomKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                questions.clear();
                joined = Long.parseLong(dataSnapshot.child("members").child(mAuth.getUid()).getValue().toString());
                for (final DataSnapshot snapshot: dataSnapshot.child("questions").getChildren()) {
                    mDatabase.child("questions")
                            .child(snapshot.child("category").getValue().toString())
                            .child(snapshot.getKey().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot1) {
                            try {
                                created = Long.parseLong(snapshot.child("timestamp").getValue().toString());
                                Question addQuestion = new Question(dataSnapshot1.child("question").getValue().toString(),
                                        created,
                                        Integer.parseInt(snapshot.child("points").child(mAuth.getUid()).getValue().toString()),
                                        snapshot.getKey().toString(),
                                        snapshot.child("category").getValue().toString());
                                Log.i("test",created.toString());
                                if (created>joined) {
                                    Log.i(created.toString(), joined.toString());
                                    questions.add(addQuestion);
                                    adapter.notifyDataSetChanged();
                                }
                            }catch (NullPointerException e){
                                
                            }
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

        peopleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RoomActivity.this, PeopleActivity.class);
                intent.putExtra("roomKey", roomKey);
                startActivity(intent);
            }
        });
    }


}
