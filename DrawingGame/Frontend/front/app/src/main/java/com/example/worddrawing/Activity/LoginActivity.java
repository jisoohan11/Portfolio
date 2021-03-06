package com.example.worddrawing.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.worddrawing.R;

import static com.example.worddrawing.Activity.MainActivity.cid;
import static com.example.worddrawing.Activity.MainActivity.cname;
import static com.example.worddrawing.Activity.MainActivity.serverURL;
import static com.example.worddrawing.app.AppController.TAG;

public class LoginActivity extends AppCompatActivity {
Button register, login;
EditText username, password;
StringRequest strReq;
String pass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        register = (Button) findViewById(R.id.regs);
        register.setEnabled(true);
        login = (Button) findViewById(R.id.login);
        login.setEnabled(true);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent register = new Intent(LoginActivity.this, RegisterActivity.class);

                startActivity(register);
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String user = username.getText().toString();
                pass = password.getText().toString();
                RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
                String url = serverURL + "login/" + user + "/" + pass;
//                String tag_string_req ="string_req";

                //pDialog.setMessage("Loading...");
                //pDialog.show();
                strReq = new StringRequest(Request.Method.GET,
                        url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, response);
                        if (response.equals("-1")){
                            Toast.makeText(getApplicationContext(),"Wrong username", Toast.LENGTH_SHORT).show();
                        }
                        else if(response.equals("0")){
                            Toast.makeText(getApplicationContext(),"Wrong password", Toast.LENGTH_SHORT).show();
                        }else{
                            cid = response;
                            cname = user;
                            Toast.makeText(getApplicationContext(),"Welcome "+cname, Toast.LENGTH_SHORT).show();
                            Intent success = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(success);
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d(TAG, "Error: " + error.getMessage());
                        //  pDialog.hide();
                    }
                });
//                AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
                queue.add(strReq);
                // notifyDataSetChanged();


            }
        });


    }
}
