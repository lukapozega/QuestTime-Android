package com.example.android.questtime;

import android.content.Intent;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private LinearLayout linearLayout;
    private ImageView profileBtn;
    private ImageView addRoomBtn;
    private TextView questionsLeftNumber;
    private TextView questionsLeftTodayTextView;
    private ListView roomListView;
    private Button joinRoom;
    private Button createRoom;

    private PopupWindow addRoomPopup;
    private LayoutInflater layoutInflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        overridePendingTransition(R.anim.activity_in, R.anim.activity_out);

        linearLayout = (LinearLayout) findViewById(R.id.linear);
        profileBtn = (ImageView) findViewById(R.id.profileBtn);
        addRoomBtn = (ImageView) findViewById(R.id.addRoomBtn);
        questionsLeftNumber = (TextView) findViewById(R.id.questionsLeftNumber);
        questionsLeftTodayTextView = (TextView) findViewById(R.id.questionsLeftTodayTextView);



        addRoomBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this, PlusButtonActivity.class);
                startActivity(intent);

            }
        });

        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();

        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
    }
}
