package com.example.photoguess.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.photoguess.R;

import java.util.ArrayList;

public class ListAdapter extends ArrayAdapter<User> {
    private final ArrayList<User> players;
    private final Context context;

    public ListAdapter(Context context, ArrayList<User> players) {
        super(context, R.layout.fragment_item, players);
        this.players = players;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        User user = players.get(position);

        if(convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.fragment_item, parent, false);
        }

            TextView name = convertView.findViewById(R.id.PLayerName);
            ImageView arrow = convertView.findViewById(R.id.arrow);

            name.setText(user.getName());
            arrow.setImageResource(R.drawable.leftarrow);

        return convertView;
    }
}
