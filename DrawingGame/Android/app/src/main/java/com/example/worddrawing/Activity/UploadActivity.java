package com.example.worddrawing.Activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.worddrawing.Resource.ImageUtil;
import com.example.worddrawing.R;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import static com.example.worddrawing.Activity.MainActivity.serverURL;
import static com.example.worddrawing.app.AppController.TAG;

public class UploadActivity extends Activity {
    StringRequest strReq;
    ImageView imgVwSelected;
    Button ImageSend, btnImageSelection;
    ImageButton back_btn;
    File tempSelectFile;
    String encoded;
    byte[] imageAsArray;
    private int mDataSize = 0;
    private int mWidth = -1;
    private int mHeight = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        ImageSend = (Button) findViewById(R.id.btnImageSend);
        ImageSend.setEnabled(false);
        ImageSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RequestQueue queue = Volley.newRequestQueue(UploadActivity.this);
                String url = serverURL+"upload";
//                String tag_string_req ="string_req";

                strReq =new StringRequest(Request.Method.POST,
                        url,new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, response);
                    }
                },new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d(TAG,"Error: "+ error.getMessage());
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<>();
                        params.put("encoded", encoded);

                        return params;
                    }
                };
                queue.add(strReq);//151712
            }
        });
        btnImageSelection = findViewById(R.id.btnImageSelection);
        btnImageSelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { // Intent를 통해 이미지를 선택
                Intent intent = new Intent();
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 1);
            }
        });
        imgVwSelected = findViewById(R.id.imgVwSelected);

        back_btn = findViewById(R.id.back_btn);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent back = new Intent(UploadActivity.this, GalleryActivity.class);
                startActivity(back);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != 1 || resultCode != RESULT_OK) {
            return;
        }
        Uri dataUri = data.getData();
        imgVwSelected.setImageURI(dataUri);
        try {
//            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(); // 1
            InputStream in = getContentResolver().openInputStream(dataUri);
            Bitmap image = BitmapFactory.decodeStream(in);
            encoded = ImageUtil.convertBitmapto64(image);
            image = ImageUtil.convert64toBitmap(encoded);
//            image.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream); // 2
            imgVwSelected.setImageBitmap(image);
//            imageAsArray = byteArrayOutputStream.toByteArray(); // 3
            int test = encoded.length();
            in.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        ImageSend.setEnabled(true);
    }
}//138576 ; 79752

