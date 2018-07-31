package com.example.android.questtime.utils.recycler;

import android.view.View;

public interface ItemClickListenerInterface {

     void onItemClick(View v, int position);

     void onLongItemClick(View v, int position);
}
