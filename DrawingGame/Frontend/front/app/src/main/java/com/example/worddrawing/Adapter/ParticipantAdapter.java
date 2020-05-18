package com.example.worddrawing.Adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.TextView;

import androidx.appcompat.widget.PopupMenu;

import com.example.worddrawing.R;

import java.util.ArrayList;

public class ParticipantAdapter extends BaseAdapter implements ListAdapter {
    private ArrayList<String> list;
    private Context context;
    private ProgressDialog pDialog;
    TextView listItemText;
    ImageButton menu_btn;

    public ParticipantAdapter(ArrayList<String> list, Context context) {
        this.list = list;
        this.context = context;
        pDialog =new ProgressDialog(context);
    }

    @Override
    public int getCount() {
        return list.size();
    }
    @Override
    public Object getItem(int pos) {
        return list.get(pos);
    }
    @Override
    public long getItemId(int pos) { return 0; }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.activity_player_adapter, null);
        }

        listItemText = view.findViewById(R.id.list);
        listItemText.setText(list.get(position));

        menu_btn = view.findViewById(R.id.menu_btn);
        menu_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(context, menu_btn);
                popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());
                popup.show();
            }
        });
        return view;
    }

    public void updateData(ArrayList<String> updatedData) {
        list = updatedData;
        this.notifyDataSetChanged();
    }
}
