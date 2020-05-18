package com.example.worddrawing.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.worddrawing.Resource.DrawingView;
import com.example.worddrawing.R;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

import yuku.ambilwarna.AmbilWarnaDialog;

import static com.example.worddrawing.Activity.LobbyActivity.socketURL;
import static com.example.worddrawing.Activity.MainActivity.PROTOCOL_PREFIX;
import static com.example.worddrawing.Activity.MainActivity.cid;
import static com.example.worddrawing.Activity.MainActivity.cname;
import static com.example.worddrawing.Activity.MainActivity.currRoomID;
import static com.example.worddrawing.Activity.MainActivity.currRoomName;
import static com.example.worddrawing.Activity.MainActivity.serverURL;

public class DrawActivity extends Activity implements OnClickListener {
    //custom drawing view
    private DrawingView drawView;
    //buttons
    private ImageButton drawBtn, eraseBtn, newBtn, saveBtn, colorBtn;
    private Button chatBtn, enterBtn, wordBtn, confirmBtn;
    //sizes
    private float smallBrush, mediumBrush, largeBrush;
    private WebSocketClient cc;
//    String w = "ws://10.24.226.190:8080/game/"+ cid+ "/" + cname + "/" + currRoomName + "/" + currRoomID;
    String w = socketURL + "game/"+ cid+ "/" + cname + "/" + currRoomName + "/" + currRoomID;
    Dialog paletteDialog;
    Dialog chatDialog;
    Dialog wordDialog;
    EditText e1;
    TextView t1, definitionView, answerView, wordTab;
    int mDefaultColor;
    String answer, definition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println(w);
        setContentView(R.layout.activity_draw);

        answer = "temp";
        definition = "temp";

        //get drawing view
        drawView = findViewById(R.id.drawing);
        mDefaultColor = ContextCompat.getColor(DrawActivity.this, R.color.colorPrimary);
        //sizes from dimensions
        smallBrush = getResources().getInteger(R.integer.small_size);
        mediumBrush = getResources().getInteger(R.integer.medium_size);
        largeBrush = getResources().getInteger(R.integer.large_size);

        wordTab = findViewById(R.id.word);
        drawBtn = findViewById(R.id.draw_btn);
        drawBtn.setOnClickListener(this);
        drawView.setBrushSize(mediumBrush);
        drawView.setLastBrushSize(mediumBrush);
        eraseBtn = findViewById(R.id.erase_btn);
        eraseBtn.setOnClickListener(this);
        newBtn = findViewById(R.id.new_btn);
        newBtn.setOnClickListener(this);
        saveBtn = findViewById(R.id.save_btn);
        saveBtn.setOnClickListener(this);
        colorBtn = findViewById(R.id.color_btn);
        colorBtn.setOnClickListener(this);
        chatBtn = findViewById(R.id.chat_btn);
        chatBtn.setOnClickListener(this);

        createWebSocketClient();

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        wordDialog = new Dialog(this);
        wordDialog.setContentView(R.layout.change_word);
        wordDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        definitionView = wordDialog.findViewById(R.id.definition);
        answerView = wordDialog.findViewById(R.id.answer);
        wordBtn = wordDialog.findViewById(R.id.get_new_btn);
        wordBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestQueue queue = Volley.newRequestQueue(DrawActivity.this);
                String url = serverURL + "changeWord/" + answer;

                JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                        url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            answer = response.getString("Word");
                            definition = response.getString("Definition");
                            answerView.setText(answer);
                            definitionView.setText(definition);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
                queue.add(jsonObjReq);
            }
        });
        confirmBtn = wordDialog.findViewById(R.id.confirm_btn);
        confirmBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                wordTab.setText(answer);
                wordDialog.dismiss();
            }
        });
        wordBtn.performClick();
        wordDialog.show();

        chatDialog = new Dialog(this);
        chatDialog.setContentView(R.layout.chat);
        chatDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        t1= chatDialog.findViewById(R.id.game_chat_tx1);
        e1= chatDialog.findViewById(R.id.game_chat_et1);
        enterBtn = chatDialog.findViewById(R.id.game_chat_enter_btn);
        enterBtn.setOnClickListener(new View.OnClickListener() {
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
                    enterBtn.performClick();
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
            }
            @Override
            public void onMessage(String s) {
                Log.d("MESSAGE", "message");
                try {
                    JSONObject jsonString = new JSONObject(s);
                    DrawingView.TYPE protocol = DrawingView.TYPE.values()[Integer.parseInt(jsonString.getString(PROTOCOL_PREFIX))];
                    if (protocol.equals(DrawingView.TYPE.CHAT)) {
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
                Log.d("Exception:", e.toString());
            }
        };
        drawView.linkSocket(cc);
        cc.connect();
    }
    public void paintClicked(View view) throws JSONException {
        drawView.setErase(false);
        drawView.setBrushSize(drawView.getLastBrushSize());
        String color = view.getTag().toString();
        drawView.setColor(color);
        JSONObject JSON = new JSONObject();
        JSON.put("proto", "colorChange");
        JSON.put("color", color);
        cc.send(JSON.toString());
        paletteDialog.dismiss();
    }
    public void openColorPicker(View view) {
        AmbilWarnaDialog colorPicker = new AmbilWarnaDialog(this, mDefaultColor, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onCancel(AmbilWarnaDialog dialog) {
//              paletteDialog.dismiss();
            }
            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                drawView.setErase(false);
                String hexColor = String.format("#%06X", (0xFFFFFF & color));
                drawView.setBrushSize(drawView.getLastBrushSize());
                drawView.setColor(hexColor);
                JSONObject JSON = new JSONObject();
                try {
                    JSON.put("proto", "colorChange");
                    JSON.put("color", hexColor);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                cc.send(JSON.toString());
                paletteDialog.dismiss();
            }
        });
        colorPicker.show();
    }
    @Override
    public void onClick(View view){
        if(view.getId()==R.id.draw_btn) {
            //draw button clicked
            final Dialog brushDialog = new Dialog(this);
            brushDialog.setTitle("Brush size:");
            brushDialog.setContentView(R.layout.brush_chooser);
            //listen for clicks on size buttons
            ImageButton smallBtn = (ImageButton)brushDialog.findViewById(R.id.small_brush);
            smallBtn.setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View v) {
                    JSONObject brush = new JSONObject();
                    drawView.setErase(false);
                    drawView.setBrushSize(smallBrush);
                    drawView.setLastBrushSize(smallBrush);
                    try {
                        brush.put(PROTOCOL_PREFIX, DrawingView.TYPE.TOOL_BAR.getVal());
                        brush.put("tool", "changeBrush");
                        brush.put("brushSize", smallBrush);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    cc.send(brush.toString());
                    brushDialog.dismiss();
                }
            });
            ImageButton mediumBtn = (ImageButton)brushDialog.findViewById(R.id.medium_brush);
            mediumBtn.setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View v) {
                    JSONObject brush = new JSONObject();
                    drawView.setErase(false);
                    drawView.setBrushSize(mediumBrush);
                    drawView.setLastBrushSize(mediumBrush);
                    try {
                        brush.put(PROTOCOL_PREFIX, DrawingView.TYPE.TOOL_BAR.getVal());
                        brush.put("tool", "changeBrush");
                        brush.put("brushSize", mediumBrush);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    cc.send(brush.toString());
                    brushDialog.dismiss();
                }
            });
            ImageButton largeBtn = (ImageButton)brushDialog.findViewById(R.id.large_brush);
            largeBtn.setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View v) {
                    JSONObject brush = new JSONObject();
                    drawView.setErase(false);
                    drawView.setBrushSize(largeBrush);
                    drawView.setLastBrushSize(largeBrush);
                    try {
                        brush.put(PROTOCOL_PREFIX, DrawingView.TYPE.TOOL_BAR.getVal());
                        brush.put("tool", "changeBrush");
                        brush.put("brushSize", largeBrush);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    cc.send(brush.toString());
                    brushDialog.dismiss();
                }
            });
            //show and wait for user interaction
            brushDialog.show();
        }
        else if(view.getId()==R.id.erase_btn) {
            //switch to erase - choose size
            final Dialog brushDialog = new Dialog(this);
            brushDialog.setTitle("Eraser size:");
            brushDialog.setContentView(R.layout.brush_chooser);
            //size buttons
            ImageButton smallBtn = (ImageButton)brushDialog.findViewById(R.id.small_brush);
            smallBtn.setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View v) {
                    JSONObject eraser = new JSONObject();
                    drawView.setErase(true);
                    drawView.setBrushSize(smallBrush);
                    try {
                        eraser.put(PROTOCOL_PREFIX, DrawingView.TYPE.TOOL_BAR.getVal());
                        eraser.put("tool", "eraser");
                        eraser.put("brushSize", smallBrush);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    cc.send(eraser.toString());
                    brushDialog.dismiss();
                }
            });
            ImageButton mediumBtn = (ImageButton)brushDialog.findViewById(R.id.medium_brush);
            mediumBtn.setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View v) {
                    JSONObject eraser = new JSONObject();
                    drawView.setErase(true);
                    drawView.setBrushSize(mediumBrush);
                    try {
                        eraser.put(PROTOCOL_PREFIX, DrawingView.TYPE.TOOL_BAR.getVal());
                        eraser.put("tool", "eraser");
                        eraser.put("brushSize", mediumBrush);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    cc.send(eraser.toString());
                    brushDialog.dismiss();
                }
            });
            ImageButton largeBtn = (ImageButton)brushDialog.findViewById(R.id.large_brush);
            largeBtn.setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View v) {
                    JSONObject eraser = new JSONObject();
                    drawView.setErase(true);
                    drawView.setBrushSize(largeBrush);
                    try {
                        eraser.put(PROTOCOL_PREFIX, DrawingView.TYPE.TOOL_BAR.getVal());
                        eraser.put("tool", "eraser");
                        eraser.put("brushSize", largeBrush);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    cc.send(eraser.toString());
                    brushDialog.dismiss();
                }
            });
            brushDialog.show();
        }
        else if(view.getId()==R.id.new_btn) {
            AlertDialog.Builder newDialog = new AlertDialog.Builder(this);
            newDialog.setTitle("New drawing");
            newDialog.setMessage("Start new drawing (you will lose the current drawing)?");
            newDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    JSONObject JSON = new JSONObject();
                    drawView.startNew();
                    try {
                        JSON.put(PROTOCOL_PREFIX, DrawingView.TYPE.TOOL_BAR.getVal());
                        JSON.put("tool", "new");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    cc.send(JSON.toString());
                    dialog.dismiss();
                }
            });
            newDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    dialog.cancel();
                }
            });
            newDialog.show();
        }
        else if(view.getId()==R.id.save_btn) {
            //save drawing
            AlertDialog.Builder saveDialog = new AlertDialog.Builder(this);
            saveDialog.setTitle("Save drawing");
            saveDialog.setMessage("Save drawing to device Gallery?");
            saveDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    //save drawing
                    drawView.setDrawingCacheEnabled(true);
                    //attempt to save
//                    String imgSaved = MediaStore.Images.Media.insertImage(getContentResolver(), drawView.getDrawingCache(),UUID.randomUUID().toString() + ".png", "drawing");
                    String imgSaved = MediaStore.Images.Media.insertImage(getContentResolver(), drawView.getDrawingCache(),UUID.randomUUID().toString() + ".png", "drawing");
                    //feedback
                    if (imgSaved != null) {
                        Toast savedToast = Toast.makeText(getApplicationContext(),
                                "Drawing saved to Gallery!", Toast.LENGTH_SHORT);
                        savedToast.show();
                    } else {
                        Toast unsavedToast = Toast.makeText(getApplicationContext(),
                                "Oops! Image could not be saved.", Toast.LENGTH_SHORT);
                        unsavedToast.show();
                    }
                    drawView.destroyDrawingCache();
                }
            });
            saveDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            saveDialog.show();
        }
        else if(view.getId()==R.id.color_btn) {
            paletteDialog = new Dialog(this);
            paletteDialog.setTitle("Brush size:");
            paletteDialog.setContentView(R.layout.palette);
            paletteDialog.show();
        }
        else if(view.getId()==R.id.chat_btn) {
            chatDialog.show();
        }
    }

    @Override
    public void onBackPressed() {
    }
}



