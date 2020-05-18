package com.example.worddrawing.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.worddrawing.Resource.Contact;
import com.example.worddrawing.R;

import static com.example.worddrawing.Activity.MainActivity.cid;
import static com.example.worddrawing.Activity.MainActivity.cname;

public class SettingActivity extends AppCompatActivity {
Button back,contact;
TextView username, userid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        back= (Button) findViewById(R.id.back);
        contact= (Button) findViewById(R.id.contact);
        username = (TextView)findViewById(R.id.username);
        userid = (TextView)findViewById(R.id.unique);

        username.setText(cname.toUpperCase());
        userid.setText(cid);
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

    }
}
