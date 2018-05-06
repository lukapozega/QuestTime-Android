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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.room_item, parent, false);
        }

        currentRoom = getItem(position);

        TextView nameTextView = (TextView) listItemView.findViewById(R.id.roomNameText);
        nameTextView.setText(currentRoom.getRoomName());

        TextView peopleTextView = (TextView) listItemView.findViewById(R.id.numberOfUsers);
        peopleTextView.setText("People: " + currentRoom.getNumberOfUsers());

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
                case "art":
                    categoryIcons.add(R.drawable.paint_icon);
                    break;
                case "sport":
                    categoryIcons.add(R.drawable.ball_icon);
                    break;
                case "science":
                    categoryIcons.add(R.drawable.physics_icon);
                    break;
                case "movie":
                    categoryIcons.add(R.drawable.movie_icon);
                    break;
                case "music":
                    categoryIcons.add(R.drawable.music_icon);
                    break;
                case "bulb":
                    categoryIcons.add(R.drawable.think_icon);
                    break;
                case "math":
                    categoryIcons.add(R.drawable.plus_icon);
                    break;
                case "hat":
                    categoryIcons.add(R.drawable.hat_icon);
                    break;
                case "geography":
                    categoryIcons.add(R.drawable.globe_icon);
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
