package com.example.android.questtime;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
<<<<<<< HEAD
=======
import java.util.Collections;
import java.util.Set;
>>>>>>> 3efb2513904c541d960b434ee6859602a82ecc8e

public class RoomActivity extends AppCompatActivity {

    private static final String TAG = "tag";
    ListView questionsList;
    ArrayList<Question> questions = new ArrayList<>();
    QuestionAdapter adapter;
    TextView roomNameTitle;
    TextView roomKeyTextView;
    String roomKey;
    String roomName;
    String roomPrivateKey;
<<<<<<< HEAD
    Long joined;
    Long created;
    int points;
=======
    String roomType;
    double joined;
    double created;
    Set<Question> helpSet;
    int bodovi;

    TextView noQuestionsTxt;
>>>>>>> 3efb2513904c541d960b434ee6859602a82ecc8e

    ImageView lock;
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
        noQuestionsTxt = (TextView) findViewById(R.id.no_questions_txt);

        lock = (ImageView) findViewById(R.id.lock);

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
        roomType = getIntent().getStringExtra("type");

        if(roomType.equals("public")){
            roomKeyTextView.setVisibility(View.GONE);
            lock.setVisibility(View.GONE);
        } else {
            roomPrivateKey = getIntent().getStringExtra("privateKey");
            roomKeyTextView.setText(roomPrivateKey);

            roomKeyTextView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    ClipboardManager cm = (ClipboardManager)getApplicationContext().getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData cd = ClipData.newPlainText("Key", roomKeyTextView.getText());
                    cm.setPrimaryClip(cd);
                    Toast.makeText(getApplicationContext(), "Copied to clipboard", Toast.LENGTH_SHORT).show();

                    return true;
                }
            });
        }

        roomNameTitle.setText(roomName);

        peopleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RoomActivity.this, PeopleActivity.class);
                intent.putExtra("roomKey", roomKey);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mDatabase.child("rooms").child(roomKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                questions.clear();
                for (final DataSnapshot snapshot: dataSnapshot.child("questions").getChildren()) {
                    joined = Double.parseDouble(dataSnapshot.child("members").child(mAuth.getUid()).getValue().toString());
                    mDatabase.child("questions")
                            .child(snapshot.child("category").getValue().toString())
                            .child(snapshot.getKey().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot1) {
<<<<<<< HEAD
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
=======
                            created = Double.parseDouble(snapshot.child("timestamp").getValue().toString());
                            try{
                                bodovi = Integer.parseInt(snapshot.child("points").child(mAuth.getUid()).getValue().toString());
                            } catch (NullPointerException e){
                                bodovi = -1;
                            }
                            Question addQuestion = new Question(dataSnapshot1.child("question").getValue().toString(),
                                    created,
                                    bodovi,
                                    snapshot.getKey(),
                                    snapshot.child("category").getValue().toString());

                            if (created > joined && created < System.currentTimeMillis()/1000) {
                                if(!questions.contains(addQuestion)) {
                                    questions.add(addQuestion);
>>>>>>> 3efb2513904c541d960b434ee6859602a82ecc8e
                                }
                                Collections.sort(questions);
                                adapter.notifyDataSetChanged();
                                noQuestionsTxt.setVisibility(View.GONE);

                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
                if(questions.isEmpty()){
                    noQuestionsTxt.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
