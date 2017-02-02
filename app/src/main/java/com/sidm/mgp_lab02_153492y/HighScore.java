package com.sidm.mgp_lab02_153492y;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * Created by Shania on 4/12/2016.
 */

public class HighScore extends Activity implements View.OnClickListener {

    private Button btn_back;

    SharedPreferences SharedPref_Name;
    SharedPreferences SharedPref_Score;

    String filename = "myfile";
    String string = "Hello world!";
    FileOutputStream outputStream;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE); // Hides title
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); // Hide top bar
        setContentView(R.layout.highscore_page);

        // Back button on highscore page
        btn_back = (Button)findViewById(R.id.btn_back);
        btn_back.setOnClickListener(this);

        // Score
        TextView scoreText;
        scoreText = (TextView)findViewById(R.id.scoreView);

        String Data;
        String writeData;
        String PlayerName;
        int PlayerScore;

        SharedPref_Name = getSharedPreferences("PlayerUSERID", Context.MODE_PRIVATE);
        PlayerName = SharedPref_Name.getString("PlayerUSERID", "DEFAULT");

        SharedPref_Score = getSharedPreferences("PlayerUSERSCORE", Context.MODE_PRIVATE);
        PlayerScore = SharedPref_Score.getInt("PlayerUSERSCORE", 0);

        // Write to highscore page
        //scoreText.setText(String.format(PlayerName + " " + PlayerScore));

        FileLoader fileloader = new FileLoader();

        Data = fileloader.readFromFile(this);
        scoreText.setText(String.format(Data));


    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();

        if (v == btn_back) {
            intent.setClass(this, Mainmenu.class);
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