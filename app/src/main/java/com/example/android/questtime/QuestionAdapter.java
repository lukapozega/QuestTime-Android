package com.example.android.questtime;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Luka on 06/04/2018.
 */

public class QuestionAdapter extends ArrayAdapter<Question> {

    TextView questionText;
    TextView timeText;
    TextView pointsText;

    public QuestionAdapter(Context context, ArrayList<Question> questions) {
        super(context,0, questions);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.question_item, parent, false);
        }

        Question currentQuestion = getItem(position);

        questionText = listItemView.findViewById(R.id.question_text);
        questionText.setText(currentQuestion.getText());

        timeText = listItemView.findViewById(R.id.questionDate);
        Date date = new Date(currentQuestion.getTimestamp()*1000);
        Format format = new SimpleDateFormat("dd. MM. yyyy HH:mm");
        timeText.setText(format.format(date));

        pointsText = listItemView.findViewById(R.id.points);
        if (currentQuestion.getPoints()==-1) {
            pointsText.setText("N/A");
        } else {
            pointsText.setText(Integer.toString(currentQuestion.getPoints()));
        }


        return listItemView;

    }

}
