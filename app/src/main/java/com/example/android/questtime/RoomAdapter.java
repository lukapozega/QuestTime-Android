package com.example.android.questtime;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

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

        return listItemView;

    }
}
