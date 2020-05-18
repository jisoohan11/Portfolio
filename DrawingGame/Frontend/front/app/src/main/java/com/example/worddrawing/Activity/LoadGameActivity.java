package com.example.worddrawing.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import com.example.worddrawing.R;

import static com.example.worddrawing.Activity.MainActivity.cid;
import static com.example.worddrawing.Activity.MainActivity.drawerID;

public class LoadGameActivity extends Activity {

    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);

        Intent startGame;
        if (cid.equals(drawerID)){
            startGame = new Intent(LoadGameActivity.this, DrawActivity.class);
            startActivity(startGame);
        }
        else {
            startGame = new Intent(LoadGameActivity.this, GuessActivity.class);
            startActivity(startGame);
        }
    }
}
