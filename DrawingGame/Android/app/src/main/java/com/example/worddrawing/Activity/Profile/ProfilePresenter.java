package com.example.worddrawing.Activity.Profile;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;

import static com.example.worddrawing.Activity.MainActivity.cid;
import static com.example.worddrawing.Activity.MainActivity.serverURL;
import static com.example.worddrawing.Activity.Profile.ProfileActivity.items;

public class ProfilePresenter implements IProfilePresenter{

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

