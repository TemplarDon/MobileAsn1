package com.sidm.mgp_lab02_153492y;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

public class Mainmenu extends Activity implements OnClickListener {

    private Button btn_start;
    private Button btn_highScore;
    private Button btn_option;
    private Button btn_help;
    private Button btn_quit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE); // Hides title
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); // Hide top bar
        setContentView(R.layout.mainmenu);

        btn_start = (Button)findViewById(R.id.btn_start);
        btn_start.setOnClickListener(this);

        btn_highScore = (Button)findViewById(R.id.btn_highScore);
        btn_highScore.setOnClickListener(this);

        btn_option = (Button)findViewById(R.id.btn_option);
        btn_option.setOnClickListener(this);

        btn_help = (Button)findViewById(R.id.btn_help);
        btn_help.setOnClickListener(this);

    }

    @Override
    public void onClick(View v)
    {
        Intent intent = new Intent();

        if (v == btn_start)
        {
            intent.setClass(this, Gamepage.class);
        }
        else if(v == btn_highScore)
        {
            intent.setClass(this, HighScore.class);
        }
        else if (v == btn_option)
        {
            intent.setClass(this, GameOption.class);
        }
        else if (v == btn_help)
        {
            intent.setClass(this, HelpPage.class);
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
