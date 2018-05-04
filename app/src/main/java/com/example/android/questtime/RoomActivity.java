package com.example.android.questtime;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class RoomActivity extends AppCompatActivity {

    ListView questionsList;
    String randomQuestion = "jbdfbsjvjsdh sjdh svdjsdvsdjvs dkdsd";
    ArrayList<String> questions = new ArrayList<>();
    ArrayAdapter<String> adapter;
    TextView roomNameTitle;
    String roomName;

    ImageView peopleButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.room_activity);

        peopleButton = (ImageView) findViewById(R.id.peopleBtn);
        roomNameTitle = (TextView) findViewById(R.id.roomNameTitle);

        questions.add(randomQuestion);
        questions.add(randomQuestion);
        questions.add(randomQuestion);
        questions.add(randomQuestion);

        questionsList = findViewById(R.id.questions_list_view);
        adapter = new QuestionAdapter(this, questions);
        questionsList.setAdapter(adapter);

        roomName = getIntent().getStringExtra("roomName");

        roomNameTitle.setText(roomName);

        peopleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RoomActivity.this, PeopleActivity.class);
                startActivity(intent);
            }
        });
    }


}
