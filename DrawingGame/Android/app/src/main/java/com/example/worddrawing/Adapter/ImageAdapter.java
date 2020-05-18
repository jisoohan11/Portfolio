package com.example.worddrawing.Adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.worddrawing.Activity.GalleryActivity;
import com.example.worddrawing.R;
import com.example.worddrawing.Resource.ImageUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import static com.example.worddrawing.Activity.MainActivity.serverURL;
import static com.example.worddrawing.app.AppController.TAG;

public class ImageAdapter extends BaseAdapter implements ListAdapter {
    private String[] list;
    private Context context;
    private ProgressDialog pDialog;
    RelativeLayout image_adapter;
    TextView image_id;
    ImageView image;
    StringRequest stringRequest;
    JsonObjectRequest jsonObjectRequest;
    String bitmapString;
    String id;

    public ImageAdapter(String[] list, Context context) {
        this.list = list;
        this.context = context;
        pDialog = new ProgressDialog(context);
    }

    @Override
    public int getCount() {
        return list.length;
    }

    @Override
    public Object getItem(int pos) {
        Object json = new JSONObject();
        json = list[pos];
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

        id = "";
        id = list[position];

        image_id = view.findViewById(R.id.image_id);
        image_id.setText(id);

        image = view.findViewById(R.id.image);
        new getBitMap().execute();

//        RequestQueue queue = Volley.newRequestQueue(context);
//        String url = serverURL + "getbyid/" + id;
//        RequestFuture<String> future = RequestFuture.newFuture();
//        stringRequest =new StringRequest(Request.Method.POST, url, future, future);
//        queue.add(stringRequest);
//        try {
//            bitmapString = future.get();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        }
//        getbitmap(id);
        try {
            Thread.sleep(400);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            bitmapString = URLDecoder.decode(bitmapString, "UTF8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Bitmap bitmap = ImageUtil.convert64toBitmap(bitmapString);
        image.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 750, 1120, false));

        return view;
    }

    public void updateData(String[] updatedData) {
        list = updatedData;
        this.notifyDataSetChanged();
    }

//    public void getbitmap(String id) {
//        RequestQueue queue = Volley.newRequestQueue(context);
//        String url = serverURL + "getbyid/" + id;
//
//        stringRequest =new StringRequest(Request.Method.POST,
//                url,new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                Log.d(TAG, response);
//                bitmapString = response;
//            }
//        },new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                VolleyLog.d(TAG,"Error: "+ error.getMessage());
//            }
//        });
//        queue.add(stringRequest);
//    }

    class getBitMap extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                StringBuffer sb = new StringBuffer();
                URL url = new URL(serverURL + "getbyid/" + id);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                if (conn != null) {
                    conn.setConnectTimeout(5000);
                    conn.setUseCaches(false);
                    if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
                        while (true) {
                            String line = br.readLine();
                            if (line == null)
                                break;
                            sb.append(line + "\n");
                        }
                        Log.d("myLog", sb.toString());
                        br.close();
                    }
                    conn.disconnect();
                }
                bitmapString = sb.toString();
            }catch (Exception e){
                e.printStackTrace();
                Log.e("error", e.getMessage());
            }
            return null;
        }
    }
}
