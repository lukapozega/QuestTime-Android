package com.example.android.questtime;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class ResultActivity extends AppCompatActivity {

    static final int QUESTION_ANSWERED = 567;

    private String questionId;
    private String category;
    private Question question;
    private int points;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private LinearLayout answers;
    private TextView questionText;
    private TextView correct;
    private TextView score;
    private String myAnswer;
    private LinearLayout percentLayout;
    private int numberOfAnswers;
    private Map<String, Integer> answersMap = new HashMap<>();
    private int position;

    float percentage;
    int i;
    private String roomId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        questionId = getIntent().getStringExtra("key");
        category = getIntent().getStringExtra("category");
        points = Integer.parseInt(getIntent().getStringExtra("points"));
        roomId = getIntent().getStringExtra("roomId");
        myAnswer = getIntent().getStringExtra("myAnswer");
        numberOfAnswers = getIntent().getIntExtra("numberOfAnswers", 0);
        position = getIntent().getIntExtra("position", 0);

        answers = findViewById(R.id.answers_result);
        questionText = findViewById(R.id.questionText_result);
        correct = findViewById(R.id.correct_wrong);
        score = findViewById(R.id.score);
        percentLayout = findViewById(R.id.percentLayout);

        mDatabase.child("rooms").child(roomId).child("questions").child(questionId).child("answers").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot answer : dataSnapshot.getChildren()){
                    Integer count = answersMap.get(answer.getValue().toString());
                    if(count == null){
                        answersMap.put(answer.getValue().toString(), 1);
                    } else {
                        answersMap.put(answer.getValue().toString(), count +1);
                    }
                }
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

                if (points!=0) {
                    correct.setText("Correct!");
                    correct.setTextColor(Color.parseColor("#36B305"));
                    score.setTextColor(Color.parseColor("#36B305"));
                } else {
                    correct.setText("Wrong!");
                    correct.setTextColor(Color.parseColor("#C40606"));
                    score.setTextColor(Color.parseColor("#C40606"));
                }

                score.setText(String.valueOf(points));
                questionText.setText(question.getText());
                //random postavljanje odgovora da ne bude tocan odgovor na istom mjestu uvije
                Random random = new Random();
                int k = random.nextInt(4);
                TextView answer = (TextView) answers.getChildAt(k%4);
                TextView percent = (TextView) percentLayout.getChildAt(k%4);
                answer.setText(question.getCorrect());
                answer.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#36B305")));
                answer.setTextColor(Color.WHITE);
                try {
                    percentage = (float)answersMap.get(answer.getText()) / numberOfAnswers * 100.0f;
                } catch (NullPointerException e){
                    percentage = 0;
                }
                percent.setText(String.format(Locale.UK, "%.1f", percentage) + "%");

                for (i = 1; i < 4; ++i){
                    answer = (TextView) answers.getChildAt((k+i)%4);
                    percent = (TextView) percentLayout.getChildAt((k+i)%4);
                    answer.setText(wrongAnswers[i-1]);
                    try {
                        percentage = (float)answersMap.get(answer.getText())/ numberOfAnswers * 100.0f;
                    } catch (NullPointerException e){
                        percentage = 0;
                    }
                    percent.setText(String.format(Locale.UK, "%.1f", percentage) + "%");

                    if(myAnswer.equals(answer.getText())){
                        answer.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#C40606")));
                        answer.setTextColor(Color.WHITE);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        setFinishOnTouchOutside(true);

    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
