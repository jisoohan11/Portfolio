package com.example.worddrawing.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.worddrawing.Adapter.ParticipantAdapter;
import com.example.worddrawing.R;

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

import static com.example.worddrawing.Activity.LobbyActivity.socketURL;
import static com.example.worddrawing.Activity.MainActivity.PROTOCOL_PREFIX;
import static com.example.worddrawing.Activity.MainActivity.cid;
import static com.example.worddrawing.Activity.MainActivity.cname;
import static com.example.worddrawing.Activity.MainActivity.currRoomID;
import static com.example.worddrawing.Activity.MainActivity.currRoomName;
import static com.example.worddrawing.Activity.MainActivity.serverURL;
import static com.example.worddrawing.Activity.MainActivity.drawerID;
import static com.example.worddrawing.app.AppController.TAG;

public class RoomActivity extends Activity implements Runnable {
    ProgressDialog dialog;
    ArrayList<String> items;
    ArrayList<String> idList;
    boolean isClicked;
    boolean isTicking;
    boolean countFinished;
    private WebSocketClient cc;
    private ProgressDialog pDialog;
    private JsonObjectRequest jsonRequest;
    ParticipantAdapter adapter;
    CountDownTimer cTimer;
    ListView lView;
//    String w = "ws://10.24.226.190:8080/room/" + cid + "/" + cname + "/" + currRoomName + "/" + currRoomID;
    String w = socketURL + "room/" + cid + "/" + cname + "/" + currRoomName + "/" + currRoomID;
//    String w = "ws://10.26.51.15:8080/room/" + cid + "/" + cname + "/" + currRoomName + "/" + currRoomID;

    Button enter_btn, ready_btn;
    ImageButton back_btn;
    TextView t1;
    EditText e1;

    public enum TYPE {
        JOIN(0),
        CHAT(1),
        READY(2),
        USER_EXIT(3);

        private int val;
        TYPE(int _val) {
            val = _val;
        }

        public int getVal() {
            return val;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);

        t1= findViewById(R.id.tx1);
        e1= findViewById(R.id.et1);
        isClicked = false;
        isTicking = false;
        countFinished = false;

        createWebSocketClient();
        items = new ArrayList<String>();
        idList = new ArrayList<String>();
        Thread th = new Thread(RoomActivity.this);
        th.start();
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        adapter = new ParticipantAdapter(items, this);
        lView = findViewById(R.id.list_participants);
        lView.setAdapter(adapter);

        enter_btn = findViewById(R.id.enter_btn);
        enter_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject json = new JSONObject();
                try {
                    json.put(PROTOCOL_PREFIX, TYPE.CHAT.getVal());
                    json.put("chat", e1.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                cc.send(json.toString());
                e1.setText("");
            }
        });

        ready_btn = findViewById(R.id.ready_btn);
        ready_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject json = new JSONObject();

                if(isClicked) { // was ready
                    v.setBackgroundColor(Color.parseColor("#c5e1b0"));
                    isClicked = false;
                } else { // get ready
                    v.setBackgroundColor(Color.parseColor("#f6c0c0"));
                    isClicked = true;
                }

                try {
                    json.put(PROTOCOL_PREFIX, TYPE.READY.getVal());
                    if (isClicked) {
                        json.put("display", 1);
                    }
                    else {
                        json.put("display", 0);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                cc.send(json.toString());
            }
        });

        back_btn = findViewById(R.id.back_btn);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isClicked) {
                    ready_btn.performClick();
                }
                if (isTicking) {
                    isTicking = false;
                    cTimer.cancel();
                }
                JSONObject JSON = new JSONObject();
                try {
                    JSON.put(PROTOCOL_PREFIX, TYPE.USER_EXIT.getVal());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                cc.send(JSON.toString());
                Intent back = new Intent(RoomActivity.this, LobbyActivity.class);
                cc.close();
                startActivity(back);
            }
        });

        e1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    lView.setVisibility(View.GONE);
                }
            }
        });

        e1.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    enter_btn.performClick();
                    return true;
                }
                return false;
            }
        });

        t1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (e1.hasFocus()) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    e1.clearFocus();
                    lView.setVisibility(View.VISIBLE);
                }
            }
        });

        t1.setMovementMethod(new ScrollingMovementMethod());
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
            public void onOpen(ServerHandshake serverHandshake) {
                Log.d("OPEN", "run() returned: " + "is connecting");
                JSONObject JSON = new JSONObject();
                try {
                    JSON.put(PROTOCOL_PREFIX, TYPE.JOIN.getVal());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                cc.send(JSON.toString());
            }

            @Override
            public void onMessage(String s) {
                Log.d("MESSAGE", "message");
                try {
                    JSONObject jsonString = new JSONObject(s);
                    String protocol = jsonString.getString(PROTOCOL_PREFIX);
                    if (protocol.equals(""+ TYPE.JOIN.getVal())) {
                        JSONArray jsonArray = jsonString.getJSONArray("users");
                        items.clear();
                        idList.clear();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject map = jsonArray.getJSONObject(i);
                            items.add(map.getString("userName"));
                            idList.add(map.getString("userId"));
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.updateData(items);
                            }
                        });
                    }
                    else if (protocol.equals(""+ TYPE.CHAT.getVal())) {
                        String username = jsonString.getString("userName");
                        String chat =  jsonString.getString("chat");
                        String txt = "\n" + " " + username + ": " + chat;
                        t1.append(txt);
                    }
                    else if (protocol.equals(""+ TYPE.READY.getVal())) {
                        String txt = "\n" + "Ready Count: " + jsonString.getInt("count");
                        t1.append(txt);
                        if (jsonString.getInt("readyToStart") == 1) {
                            txt = "\n" + "Ready To Start Now!!";
                            t1.append(txt);

                            drawerID = idList.get(0);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    int Seconds = 5;
                                    cTimer = new CountDownTimer(Seconds * 1000 + 900, 1000) {
                                        public void onTick(long millisUntilFinished) {
                                            isTicking = true;
                                            t1.append("\n" + "seconds remaining: " + millisUntilFinished / 1000);
                                            if (millisUntilFinished<=1000) {
                                                ready_btn.setEnabled(false);
                                                countFinished = true;
                                            }
                                        }
                                        public void onFinish() {
                                            countFinished = true;
                                            t1.append("\n" + "done!");
                                            cTimer.cancel();
                                            cc.close();
                                            Intent startGame = new Intent(RoomActivity.this, LoadGameActivity.class);
                                            startActivity(startGame);
                                        }
                                    };
                                    cTimer.start();
                                }
                            });
                        } else {
                            if (isTicking) {
                                isTicking = false;
                                cTimer.cancel();
                            }
                        }
                    }
                    else if (protocol.equals(""+ TYPE.USER_EXIT.getVal())) {
                        if (isTicking && !countFinished) {
                            isTicking = false;
                            cTimer.cancel();
                        }
                        String userName = jsonString.getString("userName");
                        String userId = jsonString.getString("userId");
                        items.remove(userName);
                        idList.remove(userId);
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
            public void onClose(int i, String s, boolean b) {
                Log.d("CLOSE", "onClose() returned: ");
                if (isClicked && !countFinished) {
                    ready_btn.performClick();
                }
            }

            @Override
            public void onError(Exception e) { Log.d("Exception:", e.toString()); }
        };
        cc.connect();
    }

    @Override
    public void run() {
        try {
            RequestQueue queue = Volley.newRequestQueue(this);
            String url = serverURL+"roommng/roomuser/" + currRoomID;

//            pDialog.setMessage("Loading...");
//            pDialog.show();

            jsonRequest = new JsonObjectRequest(Request.Method.GET, url,null,
                    new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    Log.d(TAG, response.toString());
                    pDialog.hide();
                    JSONObject jsonObj = response;
                    try {
                        JSONArray jsonArray = jsonObj.getJSONArray("list");
                        items.clear();
                        idList.clear();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            String name = jsonArray.getString(i);
                            items.add(name);
                            idList.add(name);
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.updateData(items);
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            },new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d(TAG,"Error: "+ error.getMessage());
                    pDialog.hide();
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            Log.e("error", e.getMessage());
        }
    }

    @Override
    public void onBackPressed() {
    }
}
