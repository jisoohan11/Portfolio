package com.example.worddrawing.Activity.Friend;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.example.worddrawing.Activity.MainActivity.cid;
import static com.example.worddrawing.Activity.MainActivity.serverURL;
import static com.example.worddrawing.Activity.Friend.FriendActivity.items;

public class FriendPresenter implements Runnable {
    @Override
    public void run() {
        try {
            StringBuffer sb = new StringBuffer();
            URL url = new URL(serverURL+"/potentialFriends/"+cid);
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
