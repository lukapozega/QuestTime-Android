package com.example.android.questtime;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
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
import java.util.Collections;
import java.util.Set;

public class RoomActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{

    private static final String TAG = "tag";
    ListView questionsList;
    ArrayList<Question> questions = new ArrayList<>();
    QuestionAdapter adapter;
    TextView roomNameTitle;
    TextView roomKeyTextView;
    String roomKey;
    String roomName;
    String roomPrivateKey;

    String roomType;
    int points;
    double joined;
    double created;
    int bodovi;

    TextView noQuestionsTxt;

    ImageView lock;
    ImageView peopleButton;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    private MediaPlayer mp;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.room_activity);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.questionSwipeLayout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(Color.GRAY, Color.GREEN, Color.BLUE,
                Color.RED, Color.CYAN);
        swipeRefreshLayout.setDistanceToTriggerSync(20);// in dips
        swipeRefreshLayout.setSize(SwipeRefreshLayout.DEFAULT);// LARGE also can be used

        mp = MediaPlayer.create(this, R.raw.sound);

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
                mp.start();
                final Question question = (Question) questionsList.getItemAtPosition(i);
                if (question.getPoints() == -1) {
                    Intent intent = new Intent(RoomActivity.this, AnswerActivity.class);
                    intent.putExtra("key", question.getId());
                    intent.putExtra("points", String.valueOf(question.getPoints()));
                    intent.putExtra("category", question.getCategory());
                    intent.putExtra("roomId", roomKey);
                    startActivity(intent);
                } else {
                    mDatabase.child("rooms").child(roomKey).child("questions").child(question.getId()).child("answers")
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            final String myAnswer = dataSnapshot.child(mAuth.getUid()).getValue().toString();
                            final int numberOfAnswers = (int) dataSnapshot.getChildrenCount();
                            Intent intentResult = new Intent(RoomActivity.this, ResultActivity.class);
                            intentResult.putExtra("key", question.getId());
                            intentResult.putExtra("points", String.valueOf(question.getPoints()));
                            intentResult.putExtra("category", question.getCategory());
                            intentResult.putExtra("roomId", roomKey);
                            intentResult.putExtra("myAnswer", myAnswer);
                            intentResult.putExtra("numberOfAnswers", numberOfAnswers);
                            startActivity(intentResult);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
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
                mp.start();
                Intent intent = new Intent(RoomActivity.this, PeopleActivity.class);
                intent.putExtra("roomKey", roomKey);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        questions.clear();
        mDatabase.child("rooms").child(roomKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                for (final DataSnapshot snapshot: dataSnapshot.child("questions").getChildren()) {
                    joined = Double.parseDouble(dataSnapshot.child("members").child(mAuth.getUid()).getValue().toString());
                    mDatabase.child("questions")
                            .child(snapshot.child("category").getValue().toString())
                            .child(snapshot.getKey().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot1) {

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
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        onResume();
    }
}
