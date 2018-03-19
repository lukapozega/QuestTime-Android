package com.example.android.questtime;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * Created by fgrebenac on 3/19/18.
 */

public class PlusButtonActivity extends AppCompatActivity {

    private Button joinRoomButton;
    private Button createNewRoomButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_room_popup);

        joinRoomButton = (Button) findViewById(R.id.joinRoomBtn);
        createNewRoomButton = (Button) findViewById(R.id.createNewRoomBtn);

        createNewRoomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PlusButtonActivity.this, CreateNewRoom.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
