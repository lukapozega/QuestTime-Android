package com.example.android.questtime.ui.room;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.questtime.R;
import com.example.android.questtime.data.models.Question;
import com.example.android.questtime.ui.people.PeopleActivity;
import com.example.android.questtime.ui.question_interaction.AnswerActivity;
import com.example.android.questtime.ui.question_interaction.ResultActivity;
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
import java.util.Collections;
import java.util.Iterator;

public class RoomActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{

    static final int QUESTION_UNANSWERED = 456;
    static final int QUESTION_ANSWERED = 567;

    private RecyclerView questionsList;

    private ArrayList<Question> questions = new ArrayList<>();
    private ArrayList<Question> questionsLeft = new ArrayList<>();
    private RecyclerQuestionAdapter adapter;
    private RecyclerView.LayoutManager manager;

    private TextView roomNameTitle;
    private TextView roomKeyTextView;
    private String roomKey;
    private String roomName;
    private String roomPrivateKey;

    private String roomType;
    private double joined;
    private double created;
    private int points;
    private Question question;
    private int selectedPosition;
    private Iterator<Question> iterator;

    private TextView noQuestionsTxt;

    private ImageView lock;
    private ImageView peopleButton;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    private SwipeRefreshLayout swipeRefreshLayout;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.room_activity);
        overridePendingTransition(R.anim.activity_in, R.anim.activity_out);

        context = this;
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.questionSwipeLayout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(Color.CYAN, Color.BLUE, Color.GREEN,
                Color.RED);
        swipeRefreshLayout.setDistanceToTriggerSync(20);// in dips
        swipeRefreshLayout.setSize(SwipeRefreshLayout.DEFAULT);// LARGE also can be used

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        peopleButton = (ImageView) findViewById(R.id.peopleButton);
        roomNameTitle = (TextView) findViewById(R.id.roomNameTitle);
        roomKeyTextView = (TextView) findViewById(R.id.roomKey);
        noQuestionsTxt = (TextView) findViewById(R.id.no_questions_txt);
        lock = (ImageView) findViewById(R.id.lock);

        roomKey = getIntent().getStringExtra("key");
        roomName = getIntent().getStringExtra("name");
        roomType = getIntent().getStringExtra("type");
        selectedPosition = getIntent().getIntExtra("selectedPosition",0);

        roomNameTitle.setText(roomName);
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

        peopleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MediaUtils.playButtonClick(context);
                Intent intent = new Intent(RoomActivity.this, PeopleActivity.class);
                intent.putExtra("roomKey", roomKey);
                startActivity(intent);
            }
        });

        swipeRefreshLayout.setRefreshing(true);
        loadQuestions();

        manager = new LinearLayoutManager(this);
        questionsList = findViewById(R.id.questions_list_view);
        questionsList.setHasFixedSize(true);
        questionsList.setLayoutManager(manager);
        questionsList.addItemDecoration(new VerticalSpaceItemDecoration(20));

        adapter = new RecyclerQuestionAdapter(this, questions, new ItemClickListenerInterface() {
            @Override
            public void onItemClick(View v, int position) {
                MediaUtils.playButtonClick(context);
                question = (Question) questions.get(position);
                if (question.getPoints() == -1) {
                    Intent intent = new Intent(RoomActivity.this, AnswerActivity.class);
                    intent.putExtra("key", question.getId());
                    intent.putExtra("points", String.valueOf(question.getPoints()));
                    intent.putExtra("category", question.getCategory());
                    intent.putExtra("roomId", roomKey);
                    intent.putExtra("position", position);
                    startActivityForResult(intent, QUESTION_ANSWERED);
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

            @Override
            public void onLongItemClick(View v, int position) {

            }
        });

        questionsList.setAdapter(adapter);

    }

    public void loadQuestions(){
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
                                points = Integer.parseInt(snapshot.child("points").child(mAuth.getUid()).getValue().toString());
                            } catch (NullPointerException e){
                                points = -1;
                            }
                            Question addQuestion = new Question(dataSnapshot1.child("question").getValue().toString(),
                                    created,
                                    points,
                                    snapshot.getKey(),
                                    snapshot.child("category").getValue().toString());
                            if (created > joined && created < System.currentTimeMillis()/1000) {
                                questions.add(addQuestion);
                                Collections.sort(questions);
                                noQuestionsTxt.setVisibility(View.GONE);
                                manager.scrollToPosition(0);
                            } else if (created > joined) {
                                questionsLeft.add(addQuestion);
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
                adapter.notifyDataSetChanged();
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
        refreshQuestions();
    }

    public void refreshQuestions () {
        iterator = questionsLeft.iterator();
        while (iterator.hasNext()) {
            final Question q = iterator.next();
            mDatabase.child("rooms").child(roomKey).child("questions").child(q.getId()).child("timestamp").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    created = Double.parseDouble(dataSnapshot.getValue().toString());
                    if (created<System.currentTimeMillis()/1000) {
                        questions.add(0,q);
                        adapter.notifyItemInserted(0);
                        manager.scrollToPosition(0);
                        iterator.remove();
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
    public void onBackPressed() {
        boolean answeredAll = true;
        for (Question q: questions) {
            if (q.getPoints() == -1) {
                answeredAll = false;
            }
        }
        Intent resultIntent = new Intent();
        resultIntent.putExtra("selectedPosition", selectedPosition);
        if(answeredAll) {
            setResult(QUESTION_ANSWERED,resultIntent);
        } else {
            setResult(QUESTION_UNANSWERED,resultIntent);
        }
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == QUESTION_ANSWERED){
            if(resultCode == RESULT_OK){
                int position = data.getIntExtra("position", 0);
                int points = data.getIntExtra("points", 0);
                Question question = questions.get(position);
                question.setPoints(points);
                adapter.notifyItemChanged(position, question);
            }
        }
    }
}
