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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.worddrawing.Adapter.ImageAdapter;
import com.example.worddrawing.R;

import static com.example.worddrawing.Activity.MainActivity.serverURL;
import static com.example.worddrawing.app.AppController.TAG;

public class GalleryActivity extends Activity implements Runnable {
    Button upload_btn;
    ImageButton back_btn;
    StringRequest stringRequest;
    ImageAdapter adapter;
    ListView lView;
    String[] idList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        Thread th = new Thread(GalleryActivity.this);
        idList = new String[0];
        th.start();
        adapter = new ImageAdapter(idList, this);
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
            String url = serverURL + "getidlist";

            stringRequest = new StringRequest(Request.Method.POST,
                    url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d(TAG, response);
                    if (!response.equals("")) {
                        idList = response.split(",");

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.updateData(idList);
                            }
                        });
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d(TAG, "Error: " + error.getMessage());
                }
            });
            queue.add(stringRequest);
    }
}
