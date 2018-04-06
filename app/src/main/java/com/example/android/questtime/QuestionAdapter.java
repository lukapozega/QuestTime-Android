package com.example.android.questtime;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Luka on 06/04/2018.
 */

public class QuestionAdapter extends ArrayAdapter<String> {

    TextView questionText;

    public QuestionAdapter(Context context, ArrayList<String> questions) {
        super(context,0, questions);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.question_item, parent, false);
        }

        questionText = listItemView.findViewById(R.id.question_text);
        questionText.setText(getItem(position));

        return listItemView;

    }

}
