package com.example.worddrawing.Activity.Setting;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.worddrawing.Activity.Login.LoginActivity;
import com.example.worddrawing.Activity.Register.RegisterPresenter;

import static com.example.worddrawing.Activity.MainActivity.cid;
import static com.example.worddrawing.Activity.MainActivity.serverURL;
import static com.example.worddrawing.Activity.Setting.SettingActivity.pass;
import static com.example.worddrawing.app.AppController.TAG;

public class SettingPresenter extends AppCompatActivity implements ISettingPresenter {
    ISetting view;
    private StringRequest strReq;


    public SettingPresenter(ISetting v){
        this.view = v;
    }

    @Override
    public void changePassword() {
        view.getUser();
        view.getID();
        RequestQueue queue = Volley.newRequestQueue(SettingPresenter.this);

        String url = serverURL + "editpassword/" + cid + "/" + pass;

        strReq = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, response);
                if (response.equals("password has been changed")){
                    Toast.makeText(getApplicationContext(),"Password Changed", Toast.LENGTH_SHORT).show();
                    Intent changedpass = new Intent(SettingPresenter.this, LoginActivity.class);
                    startActivity(changedpass);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
            }
        });
        queue.add(strReq);

    }
}
