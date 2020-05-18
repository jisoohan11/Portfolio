package com.example.worddrawing.Adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.worddrawing.R;
import com.example.worddrawing.Resource.ImageUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ImageAdapter extends BaseAdapter implements ListAdapter {
    private JSONArray list;
    private Context context;
    private ProgressDialog pDialog;
    RelativeLayout image_adapter;
    TextView image_id;
    ImageView image;

    public ImageAdapter(JSONArray list, Context context) {
        this.list = list;
        this.context = context;
        pDialog = new ProgressDialog(context);
    }

    @Override
    public int getCount() {
        return list.length();
    }

    @Override
    public Object getItem(int pos) {
        Object json = new JSONObject();
        try {
            json = list.get(pos);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    @Override
    public long getItemId(int pos) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.image_adapter, null);
        }

        JSONObject json;
        try {
            json = list.getJSONObject(position);

            image_id = view.findViewById(R.id.image_id);
            String id = json.getString("id");
            image_id.setText(id);

            image = view.findViewById(R.id.image);
            String bitmapString = json.getString("bitmap");
            String[] temparr = bitmapString.split("(?<=\\G.{10})");
            bitmapString = "";
            for (int i = 0; i < temparr.length; i++) {
                bitmapString = bitmapString + temparr[i];
            }
            Bitmap bitmap = ImageUtil.convert64toBitmap(bitmapString);
            image.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 120, 120, false));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return view;
    }

    public void updateData(JSONArray updatedData) {
        list = updatedData;
        this.notifyDataSetChanged();
    }
}
