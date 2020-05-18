package com.example.worddrawing.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.worddrawing.Adapter.FriendAdapter;
import com.example.worddrawing.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static com.example.worddrawing.Activity.MainActivity.serverURL;

public class FriendActivity extends Activity implements Runnable {
    Button back, add, delete;
    ListView list;
    EditText name;
    EditText email;
    Button btnSend;
    TextView textView;

    //List<MemberDTO> items;
    ArrayList<String> items;
    public void sendRequest() {
        String url = serverURL+"users";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);
        back =  findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent back = new Intent(FriendActivity.this, MainActivity.class);
                startActivity(back);
            }
        });
        items = new ArrayList<String>();
        Thread th = new Thread(FriendActivity.this);
        th.start();
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //intiate custom adapter
        FriendAdapter adapter = new FriendAdapter(items, this);
        //handle listview and assign adapter
        ListView lView = findViewById(R.id.friend_list);
        lView.setAdapter(adapter);
    }
    @Override
    public void run() {
        try {
            StringBuffer sb = new StringBuffer();
            URL url = new URL(serverURL+"json.all");
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
                    Log.d("myLog", sb.toString());
                    br.close();
                }
                conn.disconnect();
            }
            JSONObject jsonObj = new JSONObject(sb.toString());
            JSONArray jArray = (JSONArray) jsonObj.get("members");
            for (int i = 0; i < jArray.length(); i++) {
                JSONObject row = jArray.getJSONObject(i);
                items.add(row.getString("user"));
                Log.d("received : ", row.getString("user"));
            }
        }catch (Exception e){
            e.printStackTrace();
            Log.e("error", e.getMessage());
        }
    }
}
