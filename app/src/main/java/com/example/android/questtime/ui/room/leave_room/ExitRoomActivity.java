package com.example.android.questtime.ui.room.leave_room;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.android.questtime.R;
import com.example.android.questtime.utils.media.MediaUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

public class ExitRoomActivity extends AppCompatActivity {

    private TextView leaveTxt;
    private Button leaveBtn;
    private Button stayBtn;

    private String roomKey;
    private int position;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private Context context;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exit_room_activity);

        context = this;
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        leaveTxt = (TextView) findViewById(R.id.leaveTxt);
        leaveBtn = (Button) findViewById(R.id.leaveBtn);
        stayBtn = (Button) findViewById(R.id.stayBtn);

        roomKey = getIntent().getStringExtra("key");
        position = getIntent().getIntExtra("position", 0);

        leaveTxt.setPaintFlags(leaveTxt.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        leaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MediaUtils.playButtonClick(context);
                mDatabase.child("rooms").child(roomKey).child("members").child(mAuth.getUid()).removeValue();
                mDatabase.child("users").child(mAuth.getUid()).child("rooms").child(roomKey).removeValue();
                FirebaseMessaging.getInstance().unsubscribeFromTopic(roomKey);
                mDatabase.child("rooms").child(roomKey).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(!dataSnapshot.hasChild("members")){
                            mDatabase.child("rooms").child(roomKey).removeValue();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                Intent resultIntent = new Intent();
                resultIntent.putExtra("position", position);
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }
        });

        stayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MediaUtils.playButtonClick(context);
                Intent resultIntent = new Intent();
                setResult(Activity.RESULT_CANCELED, resultIntent);
                finish();
            }
        });

        setFinishOnTouchOutside(true);
    }
}
