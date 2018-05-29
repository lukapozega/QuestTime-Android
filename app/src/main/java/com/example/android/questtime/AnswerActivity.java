package com.example.android.questtime;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.nio.charset.StandardCharsets;
import java.util.Random;

public class AnswerActivity extends AppCompatActivity {
    static final int QUESTION_ANSWERED = 567;

    private String questionId;
    private String category;
    private Question question;
    private int points;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private LinearLayout answers;
    private TextView questionText;
    int i;
    private String roomId;
    int nextPoints;
    int saljiBodove;
    int position;

    private MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer);

        mp = MediaPlayer.create(this, R.raw.sound);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        questionId = getIntent().getStringExtra("key");
        category = getIntent().getStringExtra("category");
        points = Integer.parseInt(getIntent().getStringExtra("points"));
        roomId = getIntent().getStringExtra("roomId");
        position = getIntent().getIntExtra("position", 0);

        answers = findViewById(R.id.answers);
        questionText = findViewById(R.id.quesText);

        mDatabase.child("questions").child(category).child(questionId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DataSnapshot wrong = dataSnapshot.child("incorrect_answers");
                String wrongAnswers[] = {wrong.child("0").getValue().toString(),wrong.child("1").getValue().toString(),wrong.child("2").getValue().toString()};
                question = new Question(dataSnapshot.child("question").getValue().toString(),
                        points,
                        questionId,
                        dataSnapshot.child("correct_answer").getValue().toString(),
                        wrongAnswers);
                questionText.setText(question.getText());
                Random random = new Random();
                int k = random.nextInt(4);
                TextView answer = (TextView) answers.getChildAt(k%4);
                answer.setText(question.getCorrect());
                answer.setOnClickListener(submitAnswer);
                for (i = 1; i < 4; ++i){
                    answer = (TextView) answers.getChildAt((k+i)%4);
                    answer.setText(wrongAnswers[i-1]);
                    answer.setOnClickListener(submitAnswer);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        setFinishOnTouchOutside(true);

    }

    private View.OnClickListener submitAnswer = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mp.start();
            final TextView answer = (TextView) view;
            final DatabaseReference dbReference = mDatabase.child("rooms").child(roomId).child("questions").child(questionId);
            dbReference.child("answers").child(mAuth.getUid()).setValue(answer.getText());
            mDatabase.child("rooms").child(roomId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try {
                        nextPoints = Integer.parseInt( dataSnapshot.child("questions").child(questionId).child("next_points").getValue().toString());
                    } catch (NullPointerException e){
                        nextPoints = (int)dataSnapshot.child("members").getChildrenCount();
                    }
                    if(answer.getText().equals(question.getCorrect())) {
                        saljiBodove = nextPoints;
                        dbReference.child("points").child(mAuth.getUid()).setValue(nextPoints);
                        dbReference.child("next_points").setValue(--nextPoints);
                    } else {
                        dbReference.child("points").child(mAuth.getUid()).setValue(0);
                        saljiBodove = 0;
                    }

                    mDatabase.child("rooms").child(roomId).child("questions").child(question.getId()).child("answers")
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    final String myAnswer = dataSnapshot.child(mAuth.getUid()).getValue().toString();
                                    final int numberOfAnswers = (int) dataSnapshot.getChildrenCount();
                                    Intent intentResult = new Intent(AnswerActivity.this, ResultActivity.class);
                                    intentResult.putExtra("key", questionId);
                                    intentResult.putExtra("points", String.valueOf(saljiBodove));
                                    intentResult.putExtra("category", category);
                                    intentResult.putExtra("roomId", roomId);
                                    intentResult.putExtra("myAnswer", myAnswer);
                                    intentResult.putExtra("numberOfAnswers", numberOfAnswers);
                                    intentResult.putExtra("position", position);
                                    intentResult.setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
                                    startActivity(intentResult);
                                    finish();
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            finish();
        }
    };
}
