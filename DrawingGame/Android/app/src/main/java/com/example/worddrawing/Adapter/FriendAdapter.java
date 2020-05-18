package com.example.worddrawing.Adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.worddrawing.R;

import java.util.ArrayList;

import static com.example.worddrawing.Activity.MainActivity.cid;
import static com.example.worddrawing.Activity.MainActivity.serverURL;
import static com.example.worddrawing.app.AppController.TAG;

public class FriendAdapter extends BaseAdapter implements ListAdapter{
    private ArrayList<String> list;
    private Context context;
    private ProgressDialog pDialog;
    private StringRequest strReq;

    public FriendAdapter(ArrayList<String> list, Context context) {
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.activity_friend_adapter, null);
        }

        TextView listItemText = view.findViewById(R.id.list);
        listItemText.setText(list.get(position));

        Button delete = view.findViewById(R.id.delete);
        Button add = view.findViewById(R.id.add );

        delete.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                RequestQueue queue = Volley.newRequestQueue(context);
                String id = cid;
                String fname = list.get(position);
                String url = serverURL+"delfriend/"+id+"/"+fname;

                pDialog.setMessage("Loading...");
                pDialog.show();
                strReq =new StringRequest(Request.Method.GET,
                        url,new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, response);
                        pDialog.hide();
                    }
                },new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d(TAG,"Error: "+ error.getMessage());
                        pDialog.hide();
                    }
                });
                queue.add(strReq);
                notifyDataSetChanged();
            }
        });
        add.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                RequestQueue queue = Volley.newRequestQueue(context);
                String id = cid;
                String fname = list.get(position);
                String url = serverURL+"addfriend/"+id+"/"+fname;

                pDialog.setMessage("Loading...");
                pDialog.show();
                strReq =new StringRequest(Request.Method.GET,
                        url,new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, response);
                        pDialog.hide();
                    }
                },new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d(TAG,"Error: "+ error.getMessage());
                        pDialog.hide();
                    }
                });
                queue.add(strReq);
                notifyDataSetChanged();
            }
        });

        return view;
    }


}
