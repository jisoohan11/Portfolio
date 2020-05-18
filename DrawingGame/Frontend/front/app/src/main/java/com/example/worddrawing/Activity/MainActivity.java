package com.example.worddrawing.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.worddrawing.R;

import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    public final static String SESS_USER_ID = "USER_ID";
    public final static String SESS_USER_NAME = "USER_NAME";
    public final static String SESS_ROOM_UID = "USER_ROOM_UID";
    public final static String SESS_ROOM_NAME = "USER_ROOM_NAME";
    public final static int MAX_ROOM_USERS = 4;
    public final static String PROTOCOL_PREFIX = "proto";
    public final static String PROTOCOL_SUC = "suc";
    public final static String serverURL = "http://10.24.226.190:8080/";
//    public final static String serverURL = "http://10.20.20.77:8080/";
//    public final static String serverURL = "http://10.29.181.3:8080/";
    public static String cid  = null;
    public static String cname = null;
    public static UUID currRoomID;
    public static UUID prevRoomID;
    public static String currRoomName;
    public static String prevRoomName;
    public static String drawerID  = null;
    Button gallery, friendlist, create, join, setting, makefile;
    ImageButton profile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setting= (Button) findViewById(R.id.setting);
        gallery= (Button) findViewById(R.id.gallery);
        friendlist= (Button) findViewById(R.id.friend);
        //This will work as chatting for FirstActivity
        create= (Button) findViewById(R.id.create);
        join= (Button) findViewById(R.id.join);
        profile= (ImageButton) findViewById(R.id.profile);
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent setting = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(setting);
            }
        });
        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery = new Intent(MainActivity.this, UploadActivity.class);
                startActivity(gallery);
            }
        });
        friendlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent friend = new Intent(MainActivity.this, FriendActivity.class);
                startActivity(friend);
            }
        });
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profile = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(profile);
            }
        });
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent draw = new Intent(MainActivity.this, LobbyActivity.class);
                startActivity(draw);
            }
        });

        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent join = new Intent(MainActivity.this, DrawActivity.class);
                startActivity(join);


            }
        });

    }

}
