package com.example.android.questtime;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Luka on 31/03/2018.
 */

public class RoomAdapter extends ArrayAdapter<Room> {

    private Room currentRoom;

    public RoomAdapter(Context context, ArrayList<Room> rooms) {
        super(context, 0, rooms);
    }

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    private double joined;
    private double created;

    private int bodovi;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        currentRoom = getItem(position);

        if(currentRoom.getZastavica() != -1) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.clean_room_item, parent, false);
        } else if(currentRoom.getZastavica() == -1){
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.room_item, parent, false);
        }

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
