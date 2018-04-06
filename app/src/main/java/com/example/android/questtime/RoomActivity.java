package com.example.android.questtime;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class RoomActivity extends AppCompatActivity {

    ListView questionsList;
    String randomQuestion = "jbdfbsjvjsdh sjdh svdjsdvsdjvs dkdsd";
    ArrayList<String> questions = new ArrayList<>();
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.room_activity);

        questions.add(randomQuestion);
        questions.add(randomQuestion);
        questions.add(randomQuestion);
        questions.add(randomQuestion);

        questionsList = findViewById(R.id.questions_list_view);
        adapter = new QuestionAdapter(this, questions);
        questionsList.setAdapter(adapter);
    }


}
