package com.example.android.questtime.ui.join_room;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.android.questtime.R;
import com.example.android.questtime.ui.join_room.search.SearchResultsActivity;
import com.example.android.questtime.utils.media.MediaUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * Created by fgrebenac on 3/19/18.
 */

public class CreateOrJoinActivity extends AppCompatActivity {
    final static int CREATE_NEW_ROOM = 345;

    private Button joinPrivateRoomButton;
    private Button createNewRoomButton;
    private Button joinPublicRoomButton;
    private EditText privateKeyEnter;
    private Button joinRoom;
    private String privateKey;
    private EditText searchRoomsEditText;
    private Button searchBtn;
    private TextView categoryNames;
    private StringBuilder categoryNamesString;

    private ArrayList<String> categories = new ArrayList();

    private LinearLayout cat1Layout;
    private LinearLayout cat2Layout;
    private LinearLayout cat3Layout;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    private Context context;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_room_popup);

        context = this;
        joinPrivateRoomButton = (Button) findViewById(R.id.joinPrivateRoomBtn);
        joinPublicRoomButton = (Button) findViewById(R.id.joinPublicRoomBtn);
        createNewRoomButton = (Button) findViewById(R.id.createNewRoomBtn);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        createNewRoomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MediaUtils.playButtonClick(context);
                Intent intent = new Intent(CreateOrJoinActivity.this, CreateNewRoom.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
                startActivity(intent);
                finish();
            }
        });

        joinPrivateRoomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MediaUtils.playButtonClick(context);
                Intent intent = new Intent(CreateOrJoinActivity.this, JoinPrivateRoom.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
                startActivity(intent);
                finish();
            }
        });

        joinPublicRoomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MediaUtils.playButtonClick(context);
                setContentView(R.layout.join_public_room_popup);
                searchBtn = (Button) findViewById(R.id.searchBtn);
                searchRoomsEditText = (EditText) findViewById(R.id.searchEditText);
                cat1Layout = (LinearLayout) findViewById(R.id.cat_1);
                cat2Layout = (LinearLayout) findViewById(R.id.cat_2);
                cat3Layout = (LinearLayout) findViewById(R.id.cat_3);
                categoryNames = (TextView) findViewById(R.id.search_category_names);
                categoryNamesString = new StringBuilder();


                for (int i=0; i<4;++i) {
                    View v = cat1Layout.getChildAt(i);
                    v.setOnClickListener(catClick);
                }
                for (int i=0; i<4;++i) {
                    View v = cat2Layout.getChildAt(i);
                    v.setOnClickListener(catClick);
                }
                for (int i=0; i<4; ++i){
                    View v = cat3Layout.getChildAt(i);
                    v.setOnClickListener(catClick);
                }

                searchBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MediaUtils.playButtonClick(context);
                        Intent intent = new Intent(CreateOrJoinActivity.this, SearchResultsActivity.class);
                        intent.putExtra("searchText", searchRoomsEditText.getText().toString());
                        intent.putStringArrayListExtra("categories", categories);
                        intent.setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
                        startActivity(intent);
                        finish();
                    }
                });

            }
        });

        setFinishOnTouchOutside(true);

    }

    View.OnClickListener catClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            MediaUtils.playButtonClick(context);
            if (view.getAlpha()==1) {
                categories.remove((String) view.getTag());
                view.setAlpha(0.4f);
                categoryNames.setText("");
                for(String category : categories){
                    categoryNamesString.append(category + "," + " ");
                }
                try {
                    categoryNamesString.setLength(categoryNamesString.length() - 2);
                } catch (StringIndexOutOfBoundsException e){
                    categoryNamesString.setLength(0);
                }
                categoryNames.setText(categoryNamesString);
                categoryNamesString.setLength(0);
            } else {
                view.setAlpha(1f);
                categories.add((String) view.getTag());
                if(categories.size() != 1) {
                    categoryNames.append(",");
                    categoryNames.append(" ");
                }
                categoryNames.append((String) view.getTag());
            }
        }
    };
}
