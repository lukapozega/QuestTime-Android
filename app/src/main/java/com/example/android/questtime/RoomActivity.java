package com.example.android.questtime;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
    int points;

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
                intent.putExtra("points", String.valueOf(question.getPoints()));
                intent.putExtra("category", question.getCategory());
                intent.putExtra("roomId", roomKey);
                Intent intentResult = new Intent(RoomActivity.this, AnswerActivity.class);
                intentResult.putExtra("key", question.getId());
                intentResult.putExtra("points", String.valueOf(question.getPoints()));
                intentResult.putExtra("category", question.getCategory());
                intentResult.putExtra("roomId", roomKey);
                if (question.getPoints()==-1) {
                    startActivity(intent);
                } else {
                    startActivity(intentResult);
                }
            }
        });

        roomKey = getIntent().getStringExtra("key");
        roomName = getIntent().getStringExtra("name");
        roomPrivateKey = getIntent().getStringExtra("privateKey");

        roomNameTitle.setText(roomName);
        roomKeyTextView.setText(roomPrivateKey);

        mDatabase.child("rooms").child(roomKey).addListenerForSingleValueEvent(new ValueEventListener() {
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
                                if (snapshot.child("points").hasChild(mAuth.getUid())) {
                                    points = Integer.parseInt(snapshot.child("points").child(mAuth.getUid()).getValue().toString());
                                } else {
                                    points = -1;
                                }
                                Question addQuestion = new Question(dataSnapshot1.child("question").getValue().toString(),
                                        created,
                                        points,
                                        snapshot.getKey().toString(),
                                        snapshot.child("category").getValue().toString());
                                if (created>joined) {
                                    if(!questions.contains(addQuestion)) {
                                        questions.add(addQuestion);
                                    }
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
