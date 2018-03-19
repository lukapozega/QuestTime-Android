package com.example.android.questtime;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by fgrebenac on 3/18/18.
 */

public class CreateNewRoom extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_new_room);
        overridePendingTransition(R.anim.activity_in, R.anim.activity_out);


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
    }
}