package com.example.worddrawing.Activity.Register;

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

import static com.example.worddrawing.Activity.MainActivity.serverURL;
import static com.example.worddrawing.app.AppController.TAG;

public class RegisterPresenter extends AppCompatActivity implements IRegisterPresenter{
    IRegister view;
    private StringRequest strReq;
    public  RegisterPresenter(IRegister v){
        this.view =  v;

    }

    @Override
    public void register(String user, String pass) {
        RequestQueue queue = Volley.newRequestQueue(RegisterPresenter.this);
        String url = serverURL + "register/" + user + "/" + pass;
        strReq = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, response);
                if(response == "-1"){
                    Toast.makeText(getApplicationContext(),"Username already exist",Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
            }
        });
        queue.add(strReq);
        Toast.makeText(getApplicationContext(),"Register success", Toast.LENGTH_SHORT).show();
        Intent back = new Intent(RegisterPresenter.this, LoginActivity.class);
        startActivity(back);
    }
}
