package com.example.android.questtime.ui.question_interaction;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.android.questtime.R;
import com.example.android.questtime.data.models.Question;
import com.example.android.questtime.utils.media.MediaUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;

public class AnswerActivity extends AppCompatActivity {

    int i;
    int nextPoints;
    int sendingPoints;
    int position;
    private String questionId;
    private String category;
    private Question question;
    private int points;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private LinearLayout answers;
    private TextView questionText;
    private String roomId;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer);

        context = this;
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
                questionText.setText(Html.fromHtml(question.getText()).toString());
                Random random = new Random();
                int k = random.nextInt(4);
                TextView answer = (TextView) answers.getChildAt(k%4);
                answer.setText(Html.fromHtml(question.getCorrect()).toString());
                answer.setOnClickListener(submitAnswer);
                for (i = 1; i < 4; ++i){
                    answer = (TextView) answers.getChildAt((k+i)%4);
                    answer.setText(Html.fromHtml(wrongAnswers[i-1]).toString());
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
            MediaUtils.playButtonClick(context);
            final TextView answer = (TextView) view;
            final DatabaseReference dbReference = mDatabase.child("rooms").child(roomId).child("questions").child(questionId);
            dbReference.child("answers").child(mAuth.getUid()).setValue(TextUtils.htmlEncode(answer.getText().toString()));
            mDatabase.child("rooms").child(roomId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child("questions").child(questionId).hasChild("next_points")) {
                        nextPoints = Integer.parseInt( dataSnapshot.child("questions").child(questionId).child("next_points").getValue().toString());
                    } else {
                        nextPoints = (int)dataSnapshot.child("members").getChildrenCount();
                    }
                    if(answer.getText().toString().equals(Html.fromHtml(question.getCorrect()).toString())) {
                        sendingPoints = nextPoints;
                        dbReference.child("points").child(mAuth.getUid()).setValue(nextPoints);
                        dbReference.child("next_points").setValue(--nextPoints);
                    } else {
                        dbReference.child("points").child(mAuth.getUid()).setValue(0);
                        sendingPoints = 0;
                    }

                    mDatabase.child("rooms").child(roomId).child("questions").child(question.getId()).child("answers")
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    final String myAnswer = dataSnapshot.child(mAuth.getUid()).getValue().toString();
                                    final int numberOfAnswers = (int) dataSnapshot.getChildrenCount();
                                    Intent intentResult = new Intent(AnswerActivity.this, ResultActivity.class);
                                    intentResult.putExtra("key", questionId);
                                    intentResult.putExtra("points", String.valueOf(sendingPoints));
                                    intentResult.putExtra("category", category);
                                    intentResult.putExtra("roomId", roomId);
                                    intentResult.putExtra("myAnswer", myAnswer);
                                    intentResult.putExtra("numberOfAnswers", numberOfAnswers);
                                    intentResult.putExtra("position", position);
                                    Intent resultIntent = new Intent();
                                    resultIntent.putExtra("position", position);
                                    resultIntent.putExtra("points", sendingPoints);
                                    setResult(Activity.RESULT_OK, resultIntent);
                                    finish();
                                    startActivity(intentResult);
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
        }
    };

}
