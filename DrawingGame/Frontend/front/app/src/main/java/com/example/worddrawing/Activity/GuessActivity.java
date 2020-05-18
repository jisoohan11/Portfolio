package com.example.worddrawing.Activity;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
//import android.support.v4.content.ContextCompat;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.worddrawing.Resource.DrawingView;
import com.example.worddrawing.R;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;

import static com.example.worddrawing.Activity.LobbyActivity.socketURL;
import static com.example.worddrawing.Activity.MainActivity.PROTOCOL_PREFIX;
import static com.example.worddrawing.Activity.MainActivity.cid;
import static com.example.worddrawing.Activity.MainActivity.cname;
import static com.example.worddrawing.Activity.MainActivity.currRoomID;
import static com.example.worddrawing.Activity.MainActivity.currRoomName;

public class GuessActivity extends Activity {
    //custom drawing view
    private DrawingView drawView;
    //sizes
    private float smallBrush, mediumBrush, largeBrush;
    private WebSocketClient cc;
//    String w = "ws://10.24.226.190:8080/game/"+ cid+ "/" + cname + "/" + currRoomName + "/" + currRoomID;
    String w = socketURL + "game/"+ cid+ "/" + cname + "/" + currRoomName + "/" + currRoomID;
    private Button chatBtn, enter_btn;
    Dialog chatDialog;
    int mDefaultColor;
    EditText e1;
    TextView t1, server;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guess);

        chatDialog = new Dialog(this);
        chatDialog.setContentView(R.layout.chat);
        chatDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        t1= chatDialog.findViewById(R.id.game_chat_tx1);
        e1= chatDialog.findViewById(R.id.game_chat_et1);
        enter_btn = chatDialog.findViewById(R.id.game_chat_enter_btn);
        enter_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject json = new JSONObject();
                try {
                    json.put(PROTOCOL_PREFIX, DrawingView.TYPE.CHAT.getVal());
                    json.put("chat", e1.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                cc.send(json.toString());
                e1.setText("");
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
                }
            }
        });
        t1.setMovementMethod(new ScrollingMovementMethod());
        //
        drawView = findViewById(R.id.sharedScreen);
        mDefaultColor = ContextCompat.getColor(GuessActivity.this, R.color.colorPrimary);
        smallBrush = getResources().getInteger(R.integer.small_size);
        mediumBrush = getResources().getInteger(R.integer.medium_size);
        largeBrush = getResources().getInteger(R.integer.large_size);
        drawView.setBrushSize(mediumBrush);
        server= findViewById(R.id.server);
        chatBtn = findViewById(R.id.chat_btn);
        chatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chatDialog.show();
            }
        });
        server.setVisibility(View.INVISIBLE);
        createWebSocketClient();
    }

    private void createWebSocketClient() {
        Draft[] drafts = {new Draft_6455()};
        try {
            cc = new WebSocketClient(new URI(w), drafts[0]) {
                @Override
                public void onOpen(ServerHandshake serverHandshake) {
                    Log.d("OPEN", "run() returned: " + "is connecting");
                }
                @Override
                public void onMessage(String s) {
                    server.setText("Server:");
                    Log.d("MESSAGE", "message");
                    try {
                        JSONObject jsonString = new JSONObject(s);
                        DrawingView.TYPE protocol = DrawingView.TYPE.values()[Integer.parseInt(jsonString.getString(PROTOCOL_PREFIX))];
                        if (protocol.equals(DrawingView.TYPE.DRAW_UPDATE)) {
                            drawUpdate(BigDecimal.valueOf(jsonString.getDouble("x")).floatValue(),
                                    BigDecimal.valueOf(jsonString.getDouble("y")).floatValue(),
                                    jsonString.getInt("step"));
                        } else if (protocol.equals(DrawingView.TYPE.TOOL_BAR)) {
                            String tool = jsonString.getString("tool");
                            if (tool.equals("colorChange")) {
                                drawView.setErase(false);
                                drawView.setBrushSize(drawView.getLastBrushSize());
                                drawView.setLastBrushSize(drawView.getLastBrushSize());
                                drawView.setColor(jsonString.getString("color"));
                            } else if (tool.equals("changeBrush")) {
                                drawView.setErase(false);
                                drawView.setBrushSize(BigDecimal.valueOf(jsonString.getDouble("brushSize")).floatValue());
                            } else if (tool.equals("eraser")) {
                                drawView.setErase(true);
                                drawView.setBrushSize(BigDecimal.valueOf(jsonString.getDouble("brushSize")).floatValue());
                            } else if (tool.equals("new")) {
                                drawView.startNew();
                            }
                        } else if (protocol.equals(DrawingView.TYPE.CHAT)) {
                            String username = jsonString.getString("userName");
                            String chat =  jsonString.getString("chat");
                            String txt = "\n" + " " + username + ": " + chat;
                            t1.append(txt);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void onClose(int i, String s, boolean b) {
                    Log.d("CLOSE", "onClose() returned: ");
                }
                @Override
                public void onError(Exception e) {
                }
            };
        }
        catch (URISyntaxException e) {
            Log.d("Exception:", e.getMessage().toString());
            e.printStackTrace();
        }
        drawView.linkSocket(cc);
        cc.connect();
    }

    public void drawUpdate(float x, float y, int step) {
        String s = server.getText().toString();
        if (step == 0) {
            server.setText(s + " move " + x + y);
            drawView.move(x, y);
        }
        else if (step == 1) {
            server.setText(s + " stroke " + x + y);
            drawView.stroke(x, y);
        }
        else {
            server.setText(s + " reset " + x + y);
            drawView.resetPath(x, y);
        }
    }

    @Override
    public void onBackPressed() {
    }
}