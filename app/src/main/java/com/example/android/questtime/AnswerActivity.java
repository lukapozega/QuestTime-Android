package com.example.android.questtime;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;

public class AnswerActivity extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        questionId = getIntent().getStringExtra("key");
        category = getIntent().getStringExtra("category");
        points = Integer.parseInt(getIntent().getStringExtra("points"));
        roomId = getIntent().getStringExtra("roomId");

        answers = findViewById(R.id.answers);
        questionText = findViewById(R.id.questionText);

        mDatabase.child("rooms").child(roomId).child("questions").child(questionId).child("next_points").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                nextPoints = Integer.parseInt(dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

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
            TextView answer = (TextView) view;
            DatabaseReference dbReference = mDatabase.child("rooms").child(roomId).child("questions").child(questionId);
            dbReference.child("answers").child(mAuth.getUid()).setValue(answer.getText());
            if(answer.getText().equals(question.getCorrect())) {
                dbReference.child("points").child(mAuth.getUid()).setValue(nextPoints);
                dbReference.child("next_points").setValue(--nextPoints);
            } else {
                dbReference.child("points").child(mAuth.getUid()).setValue(0);
            }
        }
    };
}
