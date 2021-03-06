package com.sidm.mgp_lab02_153492y;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import java.io.File;
import java.nio.charset.MalformedInputException;

/**
 * Created by Shania on 30/11/2016.
 */

public class GameOption extends Activity implements OnClickListener {

    MediaPlayer mySound;

    private Button btn_back;
    private Button btn_on;
    private Button btn_off;
    private Button btn_clear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE); // Hides title
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); // Hide top bar
        setContentView(R.layout.option_page);

        btn_back = (Button)findViewById(R.id.btn_back);
        btn_back.setOnClickListener(this);

        btn_on = (Button)findViewById(R.id.btn_on);
        btn_on.setOnClickListener(this);

        btn_off = (Button)findViewById(R.id.btn_off);
        btn_off.setOnClickListener(this);

        btn_clear = (Button)findViewById(R.id.btn_clear);
        btn_clear.setOnClickListener(this);


        mySound = MediaPlayer.create(this, R.raw.music);
    }


    @Override
    public void onClick(View v) {
        Intent intent = new Intent();

        if (v == btn_back) {
            intent.setClass(this, Mainmenu.class);
        }
        else if( v == btn_on){
            intent.setClass(this, GameOption.class);
        }
        else if(v == btn_off){
            intent.setClass(this, GameOption.class);
        }
        else if(v == btn_clear){
            intent.setClass(this, GameOption.class);
            File dir = getFilesDir();
            File file = new File(dir, "highscore");
            file.delete();
        }

        // Start activity based on intent
        startActivity(intent);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }
}
