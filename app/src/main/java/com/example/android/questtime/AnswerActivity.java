package com.example.android.questtime;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        questionId = getIntent().getStringExtra("key");
        category = getIntent().getStringExtra("category");
        points = Integer.parseInt(getIntent().getStringExtra("points"));

        answers.findViewById(R.id.answers);

        mDatabase.child("questions").child(category).child(questionId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DataSnapshot wrong = dataSnapshot.child("wrong_answers");
                String wrongAnswers[] = {wrong.child("0").getValue().toString(),wrong.child("0").getValue().toString(),wrong.child("0").getValue().toString()};
                question = new Question(dataSnapshot.child("question").getValue().toString(),
                        points,
                        questionId,
                        dataSnapshot.child("correct_answer").getValue().toString(),
                        wrongAnswers);
                Random random 

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        setFinishOnTouchOutside(true);

    }
}
