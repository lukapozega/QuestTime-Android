package com.example.android.questtime.ui.rooms;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.android.questtime.utils.recycler.ItemClickListenerInterface;
import com.example.android.questtime.R;
import com.example.android.questtime.data.models.Room;

import java.util.ArrayList;
import java.util.List;

public class RecyclerRoomAdapter extends RecyclerView.Adapter<RecyclerRoomAdapter.ViewHolder> {

    private ArrayList<Room> roomsList;
    private ItemClickListenerInterface clickListener;

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView roomName;
        TextView people;
        LinearLayout categories;
        ImageView difficulty;
        View indicator;
        ImageView underline;

        public ViewHolder(View view) {
            super(view);
            roomName = (TextView) view.findViewById(R.id.roomNameText);
            people = (TextView) view.findViewById(R.id.numberOfUsers);
            categories = (LinearLayout) view.findViewById(R.id.category_layout);
            difficulty = (ImageView) view.findViewById(R.id.difficulty_icon);
            indicator = (View) view.findViewById(R.id.questionIndicator);
            underline = (ImageView) view.findViewById(R.id.underline);
        }
    }

    public RecyclerRoomAdapter(ArrayList<Room> roomsList, ItemClickListenerInterface clickListener){
        this.roomsList = roomsList;
        this.clickListener = clickListener;
    }

    @Override
    public RecyclerRoomAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.clean_room_item, parent, false);
        final ViewHolder viewHolder = new ViewHolder(itemView);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickListener.onItemClick(view, viewHolder.getAdapterPosition());
            }
        });
        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                clickListener.onLongItemClick(view, viewHolder.getAdapterPosition());
                return true;
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerRoomAdapter.ViewHolder holder, final int position) {
                Room room = roomsList.get(position);
                if(room.getAnswered() != -1) {
                    holder.indicator.setVisibility(View.GONE);
                } else {
                    holder.indicator.setVisibility(View.VISIBLE);
                }
                holder.roomName.setText(room.getRoomName());
                holder.people.setText(String.valueOf(room.getNumberOfUsers()));
                switch (room.getDifficulty()) {
                    case "easy":
                        holder.difficulty.setImageResource(R.drawable.circle_green);
                        break;
                    case "medium":
                        holder.difficulty.setImageResource(R.drawable.circle_yellow);
                        break;
                    case "hard":
                        holder.difficulty.setImageResource(R.drawable.circle_red);
                }
                holder.underline.setBackgroundColor(Color.parseColor(keyToHex(room.getKey())));
                List<Integer> categoryIcons = new ArrayList<>();
                for (String category: room.getCategories()) {
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
                for (int i=0; i<3;++i) {
                    View v = holder.categories.getChildAt(2-i);
                    if (categoryIcons.size()>i) {
                        ((ImageView) v).setImageResource(categoryIcons.get(i));
                    } else {
                        ((ImageView) v).setImageResource(android.R.color.transparent);
                    }
                }
    }

    @Override
    public int getItemCount() {
        return roomsList.size();
    }

    public void removeItem(int position){
        roomsList.remove(position);
        notifyItemRemoved(position);
    }

    public String keyToHex(String i){
        int a,b,c;
        a=(((int) i.charAt(i.length()-1))*1234)%255;
        b=(((int) i.charAt(i.length()-2))*1234)%255;
        c=(((int) i.charAt(i.length()-3))*1234)%255;
        String hex = String.format("#%02x%02x%02x", a, b, c);
        return hex;
    }
}
