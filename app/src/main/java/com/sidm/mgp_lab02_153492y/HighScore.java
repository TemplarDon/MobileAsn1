package com.sidm.mgp_lab02_153492y;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by Shania on 4/12/2016.
 */

public class HighScore extends Activity implements View.OnClickListener {

    private Button btn_back;

    SharedPreferences SharedPref_Name;
    SharedPreferences SharedPref_Score;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE); // Hides title
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); // Hide top bar
        setContentView(R.layout.highscore_page);

        btn_back = (Button)findViewById(R.id.btn_back);
        btn_back.setOnClickListener(this);

        // Score
        TextView scoreText;
        scoreText = (TextView)findViewById(R.id.scoreView);

        String PlayerName;
        int PlayerScore;

        SharedPref_Name = getSharedPreferences("PlayerUSERID", Context.MODE_PRIVATE);
        PlayerName = SharedPref_Name.getString("PlayerUSERID", "DEFAULT");

        SharedPref_Score = getSharedPreferences("PlayerSCORE", Context.MODE_PRIVATE);
        PlayerScore = SharedPref_Score.getInt("PlayerSCORE", 0);

        scoreText.setText(String.format(PlayerName + " " + PlayerScore));

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