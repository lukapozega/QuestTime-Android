package com.example.android.questtime;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class RoomActivity extends AppCompatActivity {

    ListView questionsList;
    String randomQuestion = "jbdfbsjvjsdh sjdh svdjsdvsdjvs dkdsd";
    ArrayList<String> questions = new ArrayList<>();
    ArrayAdapter<String> adapter;
    TextView roomNameTitle;
    TextView roomKeyTextView;
    String roomName;
    String roomKey;

    ImageView peopleButton;

    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.room_activity);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        peopleButton = (ImageView) findViewById(R.id.peopleBtn);
        roomNameTitle = (TextView) findViewById(R.id.roomNameTitle);
        roomKeyTextView = (TextView) findViewById(R.id.roomKey);

        questions.add(randomQuestion);
        questions.add(randomQuestion);
        questions.add(randomQuestion);
        questions.add(randomQuestion);

        questionsList = findViewById(R.id.questions_list_view);
        adapter = new QuestionAdapter(this, questions);
        questionsList.setAdapter(adapter);

        roomName = getIntent().getStringExtra("roomName");
        roomKey = getIntent().getStringExtra("privateKey");

        roomNameTitle.setText(roomName);
        roomKeyTextView.setText(roomKey);

        peopleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RoomActivity.this, PeopleActivity.class);
                startActivity(intent);
            }
        });
    }


}
