package com.example.worddrawing.Activity.Register;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.worddrawing.Activity.Login.LoginActivity;
import com.example.worddrawing.R;

import static com.example.worddrawing.Activity.MainActivity.serverURL;
import static com.example.worddrawing.app.AppController.TAG;

public class RegisterActivity extends AppCompatActivity implements IRegister{
    private EditText username, password;
    private Button register;
    private ProgressDialog pDialog;
    private StringRequest strReq;
    private String user;
    private String pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        //email=findViewById(R.id.email);
        register = findViewById(R.id.register);
        final IRegisterPresenter p = new RegisterPresenter(this);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //fetch edit text value
            getUsername();
            getPassword();
            register();

            }
        });
    }

    @Override
    public String getUsername() {
        user = username.getText().toString();
        return user;
    }

    @Override
    public String getPassword() {
        pass = password.getText().toString();
        return pass;
    }

    @Override
    public String registerUser() {
        return "ahmed";
    }

    @Override
    public String registerPassword() {
        return "asd123";
    }

    @Override
    public void register() {
        RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);
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
        Intent back = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(back);
    }

}
