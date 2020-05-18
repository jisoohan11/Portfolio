package com.example.worddrawing.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.worddrawing.Adapter.ImageAdapter;
import com.example.worddrawing.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.example.worddrawing.Activity.MainActivity.serverURL;
import static com.example.worddrawing.app.AppController.TAG;

public class GalleryActivity extends Activity implements Runnable {
    Button upload_btn;
    ImageButton back_btn;
    JSONArray items;
    JsonObjectRequest jsonRequest;
    ImageAdapter adapter;
    ListView lView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        items = new JSONArray();
        Thread th = new Thread(GalleryActivity.this);
        th.start();
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        adapter = new ImageAdapter(items, this);
        lView = findViewById(R.id.image_list);
        lView.setAdapter(adapter);

        upload_btn = findViewById(R.id.upload_btn);
        upload_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent back = new Intent(GalleryActivity.this, UploadActivity.class);
                startActivity(back);
            }
        });
        back_btn = findViewById(R.id.back_btn);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent back = new Intent(GalleryActivity.this, MainActivity.class);
                startActivity(back);
            }
        });
    }

    @Override
    public void run() {
        RequestQueue queue = Volley.newRequestQueue(GalleryActivity.this);
        String url = serverURL + "getAllImage";

        jsonRequest = new JsonObjectRequest(Request.Method.POST,
                url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());
                JSONObject jsonObj = response;

                try {
                    if (jsonObj.getBoolean("isEmpty")) {
                        return;
                    }
                    JSONArray jsonArray = jsonObj.getJSONArray("Images");
                    items = jsonArray;

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.updateData(items);
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
            }
        });
        queue.add(jsonRequest);

    }
}
