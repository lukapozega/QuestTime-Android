package com.example.android.questtime;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class RecyclerQuestionAdapter extends RecyclerView.Adapter<RecyclerQuestionAdapter.ViewHolder> {

    private ArrayList<Question> questions;
    private ItemClickListenerInterface clickListener;
    private Context context;

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView questionText;
        TextView timeText;
        TextView pointsText;
        TextView indicator;

        public ViewHolder(View view) {
            super(view);
            questionText = (TextView) view.findViewById(R.id.question_text);
            timeText = (TextView) view.findViewById(R.id.questionDate);
            pointsText = (TextView) view.findViewById(R.id.points);
            indicator = (TextView) view.findViewById(R.id.roomQuestionIndicator);
        }
    }

    public RecyclerQuestionAdapter(Context context, ArrayList<Question> questions, ItemClickListenerInterface clickListener){
        this.context = context;
        this.questions = questions;
        this.clickListener = clickListener;
    }

    @Override
    public RecyclerQuestionAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.question_item, parent, false);
        final RecyclerQuestionAdapter.ViewHolder viewHolder = new RecyclerQuestionAdapter.ViewHolder(itemView);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickListener.onItemClick(view, viewHolder.getAdapterPosition());
            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerQuestionAdapter.ViewHolder holder, int position) {
        Question question = questions.get(position);
        if(question.getPoints() != -1){
            holder.indicator.setVisibility(View.GONE);
        }
        holder.questionText.setText(question.getText());
        Date date = new Date((long)question.getTimestamp()*1000);
        Format format = new SimpleDateFormat("dd. MM. yyyy HH:mm");
        holder.timeText.setText(format.format(date));

        if(question.getPoints() != -1) {
            holder.pointsText.setText(String.valueOf(question.getPoints()));
        } else {
            holder.pointsText.setText("");
        }

    }

    @Override
    public int getItemCount() {
        return questions.size();
    }
}
