package com.example.android.questtime;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class PeopleAdapter extends ArrayAdapter<PeoplePoints> {

    TextView playerPosition;
    TextView username;
    TextView points;
    ArrayList<PeoplePoints> people;

    public PeopleAdapter(@NonNull Context context, ArrayList<PeoplePoints> people) {
        super(context, 0, people);
        this.people = people;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.people_item, parent, false);
        }
        if(people.size() >= 3){
            position += 3;
        }else{

        }

        playerPosition = listItemView.findViewById(R.id.playerPosition);
        playerPosition.setText(position+1 + ".");

        username = listItemView.findViewById(R.id.username);
        username.setText(getItem(position).getUsername());

        points = listItemView.findViewById(R.id.points);
        points.setText(String.valueOf(getItem(position).getPoints()));


        return listItemView;
    }

    @Override
    public int getCount() {
        return people.size() - 3;
    }
}
