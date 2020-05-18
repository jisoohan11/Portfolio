package com.example.worddrawing.Activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.example.worddrawing.R;
import com.example.worddrawing.Adapter.RoomAdapter;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.UUID;

import static com.example.worddrawing.Activity.MainActivity.SESS_ROOM_NAME;
import static com.example.worddrawing.Activity.MainActivity.SESS_ROOM_UID;
import static com.example.worddrawing.Activity.MainActivity.cid;
import static com.example.worddrawing.Activity.MainActivity.cname;
import static com.example.worddrawing.Activity.MainActivity.currRoomID;
import static com.example.worddrawing.Activity.MainActivity.prevRoomID;
import static com.example.worddrawing.Activity.MainActivity.currRoomName;
import static com.example.worddrawing.Activity.MainActivity.prevRoomName;

public class LobbyActivity extends Activity {
    public static String socketURL = "ws://10.24.226.190:8080/";
//    public static String socketURL = "ws://10.20.20.77:8080/";
//    ProgressDialog dialog;
    ArrayList<String[]> items;
    private WebSocketClient cc;
    RoomAdapter adapter;
    ListView lView;

    String w = socketURL + "lobby/" + cid + "/" + cname;

    public enum TYPE {
        ROOM_CREATE(0),
        ROOMINFO_UPDATE(1);

        private int val;
        TYPE(int _val) {
            val = _val;
        }

        public int getVal() {
            return val;
        }
    }

    private ImageButton refreshBtn, backBtn;
    private Button createRoomBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

        items = new ArrayList<String[]>();
        adapter = new RoomAdapter(items, this);
        lView = findViewById(R.id.list_room);
        lView.setAdapter(adapter);

        createWebSocketClient();
//        Thread th = new Thread(LobbyActivity.this);
//        th.start();
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        refreshBtn = findViewById(R.id.refresh_btn);
        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject JSON = new JSONObject();
                try {
                    JSON.put("proto", TYPE.ROOMINFO_UPDATE.getVal());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                cc.send(JSON.toString());
            }
        });

        createRoomBtn = findViewById(R.id.create_room_btn);
        createRoomBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog createRoomDialog = new Dialog(LobbyActivity.this);
                createRoomDialog.setTitle("Create Game");
                createRoomDialog.setContentView(R.layout.create_room);

                Button enter = createRoomDialog.findViewById(R.id.enter);
                enter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        EditText roomText = createRoomDialog.findViewById(R.id.room_name);
                        if (roomText.getText().toString().equals("") || roomText.getText().toString().isEmpty()) {
                            return;
                        }
                        prevRoomName = currRoomName;
                        currRoomName = roomText.getText().toString();
                        prevRoomID = currRoomID;
                        currRoomID = UUID.randomUUID();

                        Intent createRoom = new Intent(LobbyActivity.this, RoomActivity.class);
                        createRoomDialog.dismiss();
                        startActivity(createRoom);

//                        try {
//                            Thread.sleep(1000);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//
//                        RequestQueue queue = Volley.newRequestQueue(LobbyActivity.this);
//                        String url = serverURL+"roommng/setdrawer/" + currRoomID + "/" + cid;
//                        StringRequest strReq = new StringRequest(Request.Method.GET,
//                                url, new Response.Listener<String>() {
//                            @Override
//                            public void onResponse(String response) {
//                                Log.d(TAG, response);
//                                if (!response.equals("-1"))
//                                    drawerID = response;
//                            }
//                        }, new Response.ErrorListener() {
//                            @Override
//                            public void onErrorResponse(VolleyError error) {
//                                VolleyLog.d(TAG, "Error: " + error.getMessage());
//                            }
//                        });
//                        queue.add(strReq);

                        cc.close();
                    }
                });
                createRoomDialog.show();
            }
        });

        backBtn = findViewById(R.id.back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent back = new Intent(LobbyActivity.this, MainActivity.class);
                cc.close();
                startActivity(back);
            }
        });

        refreshBtn.performClick();
    }

    private void createWebSocketClient() {
        URI uri;
        Draft[] drafts = {new Draft_6455()};
        try {
            uri = new URI(w);
        }catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }
        cc = new WebSocketClient(uri, drafts[0]) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) { Log.d("OPEN", "run() returned: " + "is connecting"); }

            @Override
            public void onMessage(String s) {
                Log.d("MESSAGE", "message");
                try {
                    JSONObject jsonString = new JSONObject(s);
                    String protocol = jsonString.getString("proto");
                    if (protocol.equals(""+TYPE.ROOMINFO_UPDATE.getVal())) {
                        JSONArray jsonArray = jsonString.getJSONArray("rooms");
                        items.clear();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject map = jsonArray.getJSONObject(i);
                            String[] arr = new String[3];
                            arr[0] = (String) map.get(SESS_ROOM_UID);
                            arr[1] = (String) map.get(SESS_ROOM_NAME);
//                            arr[2] = (String) map.get("roomCount");
                            items.add(arr);
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.updateData(items);
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onClose(int i, String s, boolean b) { Log.d("CLOSE", "onClose() returned: "); }

            @Override
            public void onError(Exception e) { Log.d("Exception:", e.toString()); }
        };
        cc.connect();
    }

    @Override
    public void onBackPressed() {
    }
}
