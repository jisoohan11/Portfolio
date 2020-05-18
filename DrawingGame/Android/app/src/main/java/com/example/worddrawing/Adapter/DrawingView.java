package com.example.worddrawing.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.SurfaceView;

import com.example.worddrawing.R;

import org.java_websocket.client.WebSocketClient;
import org.json.JSONException;
import org.json.JSONObject;

import static com.example.worddrawing.Activity.MainActivity.PROTOCOL_PREFIX;
import static com.example.worddrawing.Activity.MainActivity.cid;
import static com.example.worddrawing.Activity.MainActivity.drawerID;

public class DrawingView extends SurfaceView {

    public enum TYPE {
        JOIN(0),
        GAMESTART(1),
        CHAT(2),
        DRAW_UPDATE(3),
        SELECT_STATE(4),
        CORANSWER(5),
        BREAK_GAME(6),
        TOOL_BAR(7),
        NEW_WORD(8);

        private int val;
        TYPE(int _val) {
            val = _val;
        }

        public int getVal() {
            return val;
        }
    }
    public enum STATETYPE {
        CLEAR(0);
        private int val;
        STATETYPE(int _val) {
            val = _val;
        }
        public int getVal() {
            return val;
        }
    }

    private Path drawPath;
    private Paint drawPaint, canvasPaint;
    private int paintColor = 0xFF660000;
    private Canvas drawCanvas;
    private Bitmap canvasBitmap;
    private float brushSize, lastBrushSize;

    private WebSocketClient cc;

    public DrawingView(Context context){
        super(context);
        setupDrawing();
    }

    public DrawingView(Context context, AttributeSet attrs){
        super(context, attrs);
        setupDrawing();
    }

    //setup drawing
    private void setupDrawing(){

        //prepare for drawing and setup paint stroke properties
        brushSize = getResources().getInteger(R.integer.medium_size);
        lastBrushSize = brushSize;
        drawPath = new Path();
        drawPaint = new Paint();
        drawPaint.setColor(paintColor);
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(brushSize);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);
        canvasPaint = new Paint(Paint.DITHER_FLAG);
    }
    //size assigned to view
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);
    }
    //draw the view - will be called after touch event
    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
        canvas.drawPath(drawPath, drawPaint);
    }
    //register user touches as drawing action
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!cid.equals(drawerID)) {
            return false;
        }
        float touchX = event.getX();
        float touchY = event.getY();
        JSONObject JSON = new JSONObject();
        try {
            JSON.put(PROTOCOL_PREFIX, TYPE.DRAW_UPDATE.getVal());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                move(touchX, touchY);
                try {
                    JSON.put("x", touchX);
                    JSON.put("y", touchY);
                    JSON.put("step", 0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                cc.send(JSON.toString());
                break;
            case MotionEvent.ACTION_MOVE:
                stroke(touchX, touchY);
                try {
                    JSON.put("x", touchX);
                    JSON.put("y", touchY);
                    JSON.put("step", 1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                cc.send(JSON.toString());
                break;
            case MotionEvent.ACTION_UP:
                stroke(touchX, touchY);
                try {
                    JSON.put("x", touchX);
                    JSON.put("y", touchY);
                    JSON.put("step", 2);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                cc.send(JSON.toString());
                drawCanvas.drawPath(drawPath, drawPaint);
                drawPath.reset();
                break;
            default:
                return false;
        }
        invalidate();
        return true;
    }
    public void move(float x, float y) {
        drawPath.moveTo(x, y);
    }
    public void stroke(float x, float y) {
        drawPath.lineTo(x, y);
    }
    public void resetPath(float x, float y) {
        stroke(x, y);
        drawCanvas.drawPath(drawPath, drawPaint);
        drawPath.reset();
    }
    public void drawPath() {
        drawCanvas.drawPath(drawPath, drawPaint);
    }
    public boolean linkSocket(WebSocketClient link) {
        cc = link;
        return true;
    }
    //update color
    public void setColor(String newColor){
        paintColor = Color.parseColor(newColor);
        drawPaint.setColor(paintColor);
        invalidate();
    }
    //set brush size
    public void setBrushSize(float newSize){
        float pixelAmount = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                newSize, getResources().getDisplayMetrics());
        brushSize=pixelAmount;
        drawPaint.setStrokeWidth(brushSize);
    }
    //get and set last brush size
    public void setLastBrushSize(float lastSize){
        lastBrushSize=lastSize;
    }
    public float getLastBrushSize(){
        return lastBrushSize;
    }
    //set erase true or false
    public void setErase(boolean isErase){
        if(isErase) {
            drawPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        }
        else drawPaint.setXfermode(null);
    }
    //start new drawing
    public void startNew(){
        drawCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        invalidate();
    }
}