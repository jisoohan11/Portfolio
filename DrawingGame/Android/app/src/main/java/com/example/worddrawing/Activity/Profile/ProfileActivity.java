package com.example.worddrawing.Activity.Profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.worddrawing.Activity.MainActivity;
import com.example.worddrawing.Adapter.FriendAdapter;
import com.example.worddrawing.R;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

import static com.example.worddrawing.Activity.MainActivity.cid;
import static com.example.worddrawing.Activity.MainActivity.cname;
import static com.example.worddrawing.Activity.MainActivity.serverURL;

public class ProfileActivity extends AppCompatActivity implements Runnable{
    private Button back;
    static ArrayList<String> items;
    private TextView user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        back = findViewById(R.id.back);
        user = findViewById(R.id.user);

        user.setText(cname.toUpperCase() + "'s Profile");
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent back = new Intent(ProfileActivity.this, MainActivity.class);
                startActivity(back);
            }
        });
        items = new ArrayList<String>();
        Thread th = new Thread(ProfileActivity.this);
        th.start();
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        FriendAdapter adapter = new FriendAdapter(items, this);
        ListView lView = findViewById(R.id.flist);
        lView.setAdapter(adapter);


    }
    @Override
    public void run() {
        try {
            StringBuffer sb = new StringBuffer();
            URL url = new URL(serverURL+"getfriend/"+cid);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            if (conn != null) {
                conn.setConnectTimeout(5000);
                conn.setUseCaches(false);
                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
                    while (true) {
                        String line = br.readLine();
                        if (line == null)
                            break;
                        sb.append(line + "\n");
                    }
                    br.close();
                }
                conn.disconnect();
            }
            JSONObject jsonObj = new JSONObject(sb.toString());
            Iterator<String> keys = jsonObj.keys();
            while(keys.hasNext()) {
                String key = keys.next();
                String name = jsonObj.getString(key);
                items.add(name);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


}


