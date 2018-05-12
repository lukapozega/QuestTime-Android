package com.example.android.questtime;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by fgrebenac on 3/18/18.
 */

public class CreateNewRoom extends AppCompatActivity {

    private String roomName;
    private String difficulty;
    private String type;
    private List<String> categories = new ArrayList();
    private LinearLayout difficultyLayout;
    private LinearLayout typeLayout;
    private LinearLayout cat1Layout;
    private LinearLayout cat2Layout;
    private EditText roomNameEditText;
    private TextView createRoomText;
    private Room room;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    int padding;

    StringBuilder privateKey = new StringBuilder();
    Random random = new Random();
    String alphaNumeric = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_new_room_activity);
        overridePendingTransition(R.anim.activity_in, R.anim.activity_out);

            difficultyLayout = (LinearLayout) findViewById(R.id.difficulty);
            typeLayout = (LinearLayout) findViewById(R.id.type);
            cat1Layout = (LinearLayout) findViewById(R.id.cat_1);
            cat2Layout = (LinearLayout) findViewById(R.id.cat_2);
            createRoomText = (TextView) findViewById(R.id.create);
            roomNameEditText = (EditText) findViewById(R.id.roomName);

            mAuth = FirebaseAuth.getInstance();
            mDatabase = FirebaseDatabase.getInstance().getReference();

            createRoomText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    roomName = roomNameEditText.getText().toString();

                    for (int i=0; i<8; ++i) {
                        privateKey.append(alphaNumeric.charAt(random.nextInt(62)));
                    }

                    if(roomName.matches("")) {
                        Toast.makeText(CreateNewRoom.this, "You have to choose room name!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(difficulty==null) {
                        Toast.makeText(CreateNewRoom.this, "You have to choose difficulty!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(type==null) {
                        Toast.makeText(CreateNewRoom.this, "You have to choose type!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(categories.isEmpty()){
                        Toast.makeText(CreateNewRoom.this, "You have to choose at least one category!", Toast.LENGTH_SHORT).show();
                        return;
                    }



                    room = new Room(roomName, difficulty, type, categories, privateKey.toString());
                    String key = mDatabase.child("rooms").push().getKey();
                    mDatabase.child("rooms").child(key).setValue(room);

                    mDatabase.child("rooms").child(key).child("members").child(mAuth.getUid()).setValue(System.currentTimeMillis()/1000);
                    mDatabase.child("users").child(mAuth.getUid()).child("rooms").child(key).setValue(true);
                    FirebaseMessaging.getInstance().subscribeToTopic(key);
                    Toast.makeText(CreateNewRoom.this, "Room successfully created!", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });

            for (int i=0; i<3;++i) {
                View v = difficultyLayout.getChildAt(i);
                if (i==0) {
                    padding=v.getPaddingBottom();
                }
                v.setOnClickListener(diffClick);
            }
            for (int i=0; i<2;++i) {
                View v = typeLayout.getChildAt(i);
                v.setOnClickListener(typeClick);
            }
            for (int i=0; i<5;++i) {
                View v = cat1Layout.getChildAt(i);
                v.setOnClickListener(catClick);
            }
            for (int i=0; i<4;++i) {
                View v = cat2Layout.getChildAt(i);
                v.setOnClickListener(catClick);
            }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
    }

    private View.OnClickListener diffClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            for (int i=0; i<3;++i) {
                View v = difficultyLayout.getChildAt(i);
                v.setPadding(padding,padding,padding,padding);
            }
            difficulty = (String) view.getTag();
            view.setPadding(padding-10 ,padding-10,padding-10,padding-10);
        }
    };

    private View.OnClickListener typeClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            for (int i=0; i<2;++i) {
                TextView v = (TextView) typeLayout.getChildAt(i);
                v.setTextColor(Color.parseColor("#3a86aa"));
            }
            type = ((String) ((TextView) view).getText()).toLowerCase();
            ((TextView) view).setTextColor(Color.parseColor("#ffffff"));
        }
    };

    private View.OnClickListener catClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view.getAlpha()==1) {
                categories.remove((String) view.getTag());
                view.setAlpha(0.4f);
            } else {
                if (categories.size() < 3) {
                    view.setAlpha(1f);
                    categories.add((String) view.getTag());
                }
            }
        }
    };

}
