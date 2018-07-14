package com.example.android.questtime;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class ResultActivity extends AppCompatActivity {

    private String questionId;
    private String category;
    private Question question;
    private int points;
    private DatabaseReference mDatabase;
    private LinearLayout answers;
    private TextView questionText;
    private TextView correct_wrong;
    private TextView score;
    private String myAnswer;
    private int numberOfAnswers;
    private Map<String, Integer> answersMap = new HashMap<>();

    private float percentage;
    private int i;
    private String roomId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        questionId = getIntent().getStringExtra("key");
        category = getIntent().getStringExtra("category");
        points = Integer.parseInt(getIntent().getStringExtra("points"));
        roomId = getIntent().getStringExtra("roomId");
        myAnswer = getIntent().getStringExtra("myAnswer");
        numberOfAnswers = getIntent().getIntExtra("numberOfAnswers", 0);

        answers = findViewById(R.id.answers_result);
        questionText = findViewById(R.id.questionText_result);
        correct_wrong = findViewById(R.id.correct_wrong);
        score = findViewById(R.id.score);

        mDatabase.child("rooms").child(roomId).child("questions").child(questionId).child("answers").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot answer : dataSnapshot.getChildren()){
                    Integer count = answersMap.get(Html.fromHtml(answer.getValue().toString()).toString());
                    if(count == null){
                        answersMap.put(Html.fromHtml(answer.getValue().toString()).toString(), 1);
                    } else {
                        answersMap.put(Html.fromHtml(answer.getValue().toString()).toString(), count +1);
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
                    correct_wrong.setText("Correct!");
                    correct_wrong.setTextColor(Color.parseColor("#36B305"));
                    score.setTextColor(Color.parseColor("#36B305"));
                } else {
                    correct_wrong.setText("Wrong!");
                    correct_wrong.setTextColor(Color.parseColor("#C40606"));
                    score.setTextColor(Color.parseColor("#C40606"));
                }

                score.setText(String.valueOf(points));
                questionText.setText(Html.fromHtml(question.getText()).toString());

                Random random = new Random();
                int k = random.nextInt(4);
                LinearLayout answer = (LinearLayout) answers.getChildAt(k%4);
                TextView answerText = (TextView) answer.getChildAt(0);
                TextView percent = (TextView) answer.getChildAt(1);
                answerText.setText(Html.fromHtml(question.getCorrect()).toString());
                answerText.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#36B305")));
                answerText.setTextColor(Color.WHITE);
                try {
                    percentage = (float)answersMap.get(answerText.getText()) / numberOfAnswers * 100.0f;
                } catch (NullPointerException e){
                    percentage = 0;
                }
                percent.setText(String.format(Locale.UK, "%.1f", percentage) + "%");

                for (i = 1; i < 4; ++i){
                    answer = (LinearLayout) answers.getChildAt((k+i)%4);
                    answerText = (TextView) answer.getChildAt(0);
                    percent = (TextView) answer.getChildAt(1);
                    answerText.setText(Html.fromHtml(wrongAnswers[i-1]).toString());
                    try {
                        percentage = (float)answersMap.get(answerText.getText())/ numberOfAnswers * 100.0f;
                    } catch (NullPointerException e){
                        percentage = 0;
                    }
                    percent.setText(String.format(Locale.UK, "%.1f", percentage) + "%");

                    if(myAnswer.equals(answerText.getText())){
                        answerText.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#C40606")));
                        answerText.setTextColor(Color.WHITE);
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
