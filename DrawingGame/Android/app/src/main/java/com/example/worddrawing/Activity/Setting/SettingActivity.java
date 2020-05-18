package com.example.worddrawing.Activity.Setting;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
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
import com.example.worddrawing.Activity.MainActivity;
import com.example.worddrawing.Resource.Contact;
import com.example.worddrawing.R;

import static com.example.worddrawing.Activity.MainActivity.cid;
import static com.example.worddrawing.Activity.MainActivity.cname;
import static com.example.worddrawing.Activity.MainActivity.serverURL;
import static com.example.worddrawing.app.AppController.TAG;

public class SettingActivity extends AppCompatActivity implements ISetting{
    private Button back,contact,change;
    private TextView username, userid, password;
    static String pass;
    private StringRequest strReq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        back= (Button) findViewById(R.id.back);
        contact= (Button) findViewById(R.id.contact);
        username = (TextView)findViewById(R.id.username);
        userid = (TextView)findViewById(R.id.unique);
        password = (TextView)findViewById(R.id.password);
        change = (Button)findViewById(R.id.change);
        getUser();
        getID();
    back.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent back = new Intent(SettingActivity.this, MainActivity.class);
            startActivity(back);
        }
    });
    contact.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent contact = new Intent(SettingActivity.this, Contact.class);
            startActivity(contact);
        }
    });
    change.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            changePassword();
        }
    });



    }



    @Override
    public void getUser() {
        username.setText(cname.toUpperCase());

    }

    @Override
    public void getID() {
        userid.setText(cid);
    }

    @Override
    public void changePassword() {
        final String user = username.getText().toString();
        pass = password.getText().toString();
        RequestQueue queue = Volley.newRequestQueue(SettingActivity.this);
        String url = serverURL + "editpassword/" + cid + "/" + pass;

        strReq = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, response);
                if (response.equals("password has been changed")){
                    Toast.makeText(getApplicationContext(),"Password Changed", Toast.LENGTH_SHORT).show();
                    Intent changedpass = new Intent(SettingActivity.this, LoginActivity.class);
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

    @Override
    public String chaningPasswordTest() {

    return "changed";
    }
}
