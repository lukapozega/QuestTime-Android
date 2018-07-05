package com.example.android.questtime;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Luka on 31/03/2018.
 */

public class SearchRoomAdapter extends ArrayAdapter<Room> {

    private Room currentRoom;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private MediaPlayer mp;

    public SearchRoomAdapter(Context context, ArrayList<Room> rooms) {
        super(context, 0, rooms);
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.search_room_item, parent, false);
        }
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mp = MediaPlayer.create(getContext(), R.raw.sound);

        currentRoom = getItem(position);

        final Button joinBtn = (Button) listItemView.findViewById(R.id.joinBtn);
        final TextView peopleTextView = (TextView) listItemView.findViewById(R.id.numberOfUsers);

        mDatabase.child("rooms").child(currentRoom.getKey()).child("members").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                peopleTextView.setText("People: " + dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        joinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mp.start();
                mDatabase.child("rooms").child(getItem(position).getKey()).child("members").child(mAuth.getUid()).setValue(System.currentTimeMillis()/1000);
                mDatabase.child("users").child(mAuth.getUid()).child("rooms").child(getItem(position).getKey()).setValue(true);
                FirebaseMessaging.getInstance().subscribeToTopic(getItem(position).getKey());
                joinBtn.setBackgroundTintList(ColorStateList.valueOf(Color.GRAY));
                joinBtn.setText("Joined");
            }
        });

        TextView nameTextView = (TextView) listItemView.findViewById(R.id.roomNameText);
        nameTextView.setText(currentRoom.getRoomName());

        ImageView difficultyIW = (ImageView) listItemView.findViewById(R.id.difficulty_icon);
        switch (currentRoom.getDifficulty()) {
            case "easy":
                difficultyIW.setImageResource(R.drawable.circle_green);
                break;
            case "medium":
                difficultyIW.setImageResource(R.drawable.circle_yellow);
                break;
            case "hard":
                difficultyIW.setImageResource(R.drawable.circle_red);
        }

        List<Integer> categoryIcons = new ArrayList<>();
        for (String category: currentRoom.getCategories()) {
            switch (category) {
                case "Art":
                    categoryIcons.add(R.drawable.art);
                    break;
                case "Animals":
                    categoryIcons.add(R.drawable.animals);
                    break;
                case "Celebrities":
                    categoryIcons.add(R.drawable.celebrities);
                    break;
                case "Entertainment":
                    categoryIcons.add(R.drawable.entertainment);
                    break;
                case "General Knowledge":
                    categoryIcons.add(R.drawable.general);
                    break;
                case "Geography":
                    categoryIcons.add(R.drawable.geography);
                    break;
                case "History":
                    categoryIcons.add(R.drawable.history);
                    break;
                case "Mythology":
                    categoryIcons.add(R.drawable.mythology);
                    break;
                case "Politics":
                    categoryIcons.add(R.drawable.politics);
                    break;
                case "Science":
                    categoryIcons.add(R.drawable.science);
                    break;
                case "Sports":
                    categoryIcons.add(R.drawable.sports);
                    break;
                case "Vehicles":
                    categoryIcons.add(R.drawable.vehicles);
                    break;
            }
        }



        LinearLayout categoryLayout = (LinearLayout) listItemView.findViewById(R.id.category_layout);
        for (int i=0; i<3;++i) {
            View v = categoryLayout.getChildAt(2-i);
            if (categoryIcons.size()>i) {
                ((ImageView) v).setImageResource(categoryIcons.get(i));
            } else {
                ((ImageView) v).setImageResource(android.R.color.transparent);
            }
        }

        ImageView underline = (ImageView) listItemView.findViewById(R.id.underline);
        GradientDrawable roomColor = (GradientDrawable) underline.getBackground();
        Random rnd = new Random();
        roomColor.setColor(android.graphics.Color.argb(255, rnd.nextInt(256),rnd.nextInt(256),rnd.nextInt(256)));

        return listItemView;

    }
}
