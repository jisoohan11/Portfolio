package com.example.worddrawing.Adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.StringRequest;
import com.example.worddrawing.Activity.RoomActivity;
import com.example.worddrawing.R;

import static com.example.worddrawing.Activity.MainActivity.currRoomID;
import static com.example.worddrawing.Activity.MainActivity.prevRoomID;

import java.util.ArrayList;
import java.util.UUID;

public class RoomAdapter extends BaseAdapter implements ListAdapter {
    private ArrayList<String[]> list;
    private Context context;
    private ProgressDialog pDialog;
    private StringRequest strReq;
    TextView listItemText;
    TextView listItemText2;

    public RoomAdapter(ArrayList<String[]> list, Context context) {
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
            view = inflater.inflate(R.layout.activity_room_adapter, null);
        }

        listItemText = view.findViewById(R.id.list);
//        String txt = list.get(position)[2] + ": " + list.get(position)[1];
        String txt = list.get(position)[1];
        listItemText.setText(txt);

        listItemText2 = view.findViewById(R.id.list2);
        listItemText2.setText(list.get(position)[0]);

        Button join = view.findViewById(R.id.join);
        join.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                prevRoomID = currRoomID;
                currRoomID = UUID.fromString(listItemText2.getText().toString());
                Intent back = new Intent(context, RoomActivity.class);
                context.startActivity(back);
            }
        });

        return view;
    }

    public void updateData(ArrayList<String[]> updatedData) {
        list = updatedData;
        this.notifyDataSetChanged();
    }
}
