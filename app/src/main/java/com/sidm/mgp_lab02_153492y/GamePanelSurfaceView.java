package com.sidm.mgp_lab02_153492y;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.nfc.Tag;
import android.os.Vibrator;
import android.text.InputFilter;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.LinkedList;
import java.util.Random;

/**
 * Created by 153492Y on 11/24/2016.
 */

public class GamePanelSurfaceView extends SurfaceView implements SurfaceHolder.Callback, SensorEventListener{

    // Implement this interface to receive information about changes to the surface.
    private GameThread myThread = null; // Thread to control the rendering

    // 1a) Variables used for background rendering
    private Bitmap Background, ScaledBackground;

    // 1b) Define Screen width and Screen height as integer
    int ScreenWidth, ScreenHeight;

    // 1c) Variables for defining background start and end point
    private short bgX = 0, bgY = 0;

    // 4a) bitmap array to stores 4 images of the spaceship
    private Bitmap[] shipArr = new Bitmap[4];

    // 4b) Variable as an index to keep track of the spaceship images
    private short shipArrIdx = 0;

    // 2 variables to place ship, based on touch on screen
    //private short mX = 0, mY = 0;

    // Variables for FPS
    public float FPS;
    float deltaTime;
    long dt;


    // Variable for Game State check
    private GAME_STATES GameState;

    private enum GAME_STATES
    {
        START_UP,
        INGAME,
        PAUSED,
        ENDGAME,
    }

    // Init SpriteAnimation
    private SpriteAnimation anim_coin;

    // Define to use font as myfont - tutorial 8
    Typeface myfont;

    //  Init for start png - tutorial 8
    private Bitmap star;
    int numstar = 3;

    int energy = 20;

    // Variables for coins
    private int coinX = 0, coinY = 0;

    // Action bools
    protected boolean b_moveShip = true;

    // Buttons
    private Button btn_jump;
    private Bitmap btn_jump_tex;
    Vector3 btn_jump_pos;

    private Button btn_slide;
    private Bitmap btn_slide_tex;
    Vector3 btn_slide_pos;

    private Button btn_pause;
    private Bitmap btn_pause_tex;
    Vector3 btn_pause_pos;

    private Button btn_start;
    private Bitmap btn_start_tex;
    Vector3 btn_start_pos;

    // Timer
    float f_timer;

    // Rotate
    boolean rotate = false;

    // Score
    int m_Score = 0;
    float timer;

    // Player
    GameObject m_Player;

    // Jump
    boolean jumping = false;

    // LoadMap
    LevelLoader levelLoader;

    // Gravity
    Vector3 m_Gravity;

    // Screen Move Rate
    int ScreenMoveRate;
    int BGMoveRate;
    int ScreenOffset;
    boolean MoveScreen;

    // Level
    Level CurrLevel;
    Level NextLevel;

    // Tutorial 8
    Activity activityTracker; //Use to track and then launch to the desired activity

    // Tutorial 9
    public Vibrator m_Vibrator;
    SoundManager soundManager;

    // Tutorial 13
    // Toast
    CharSequence text;
    int toastTime;
    Toast toast;

    // Alert
    public boolean showAlert = false;
    AlertDialog.Builder alert = null;
    private Alert AlertObject;

    // Shared Preferences
    SharedPreferences SharedPref_Name;
    SharedPreferences.Editor editName;
    String PlayerName;

    SharedPreferences SharedPref_Score;
    SharedPreferences.Editor editScore;
    int PlayerScore;

    // Tutorial 14
    private SensorManager sensor;
    float[] SensorVar = new float[3];
    private float[] values = {0,0,0};

    private Bitmap ball;
    private float ballX = 0, ballY = 0;

    private long lastTime = System.currentTimeMillis();

    //constructor for this GamePanelSurfaceView class
    public GamePanelSurfaceView(Context context, final Activity activity) {

        // Context is the current state of the application/object
        super(context);

        // Adding the callback (this) to the surface holder to intercept events
        getHolder().addCallback(this);

        // 1d) Set information to get screen size
        DisplayMetrics metric = context.getResources().getDisplayMetrics();
        ScreenWidth = metric.widthPixels;
        ScreenHeight = metric.heightPixels;

        // 1e)load the image when this class is being instantiated
        Background = BitmapFactory.decodeResource(getResources(), R.drawable.mobile_bg);
        ScaledBackground = Bitmap.createScaledBitmap(Background, ScreenWidth, ScreenHeight, true);

        // 4c) Load the images of the spaceships
        shipArr[0] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ship2_1), ScreenWidth / 12, ScreenHeight / 9, true);
        shipArr[1] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ship2_2), ScreenWidth / 8, ScreenHeight / 6, true);
        shipArr[2] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ship2_3), ScreenWidth / 8, ScreenHeight / 6, true);
        shipArr[3] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ship2_4), ScreenWidth / 8, ScreenHeight / 6, true);

        // Load the sprite sheet
        anim_coin = new SpriteAnimation(Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(getResources(), R.drawable.flystar),
                ScreenWidth / 5, ScreenHeight / 5, true), 320, 64, 5, 5);

        // Create the game loop thread
        myThread = new GameThread(getHolder(), this);

        // Load star
        star = BitmapFactory.decodeResource(getResources(), R.drawable.star);

        // Load font type - tutorial 8
        myfont = Typeface.createFromAsset(getContext().getAssets(),"fonts/Tandysoft.ttf");

        // Tutorial 8 activity
        // what is the condition ?? boolean = gameover
        activityTracker = activity;
        Intent intent = new Intent();
        //intent.setClass(getContext(), Mainmenu.class);
        //activityTracker.startActivity(intent);

        // Tutorial 9
        m_Vibrator = (Vibrator)getContext().getSystemService(Context.VIBRATOR_SERVICE);

        soundManager = new SoundManager();
        soundManager.Init();
        soundManager.LoadSounds(context);

        // Tutorial 13
        // Init the Toast
        ToastMessage(context);

        // Shared Preferences
        SharedPref_Name = getContext().getSharedPreferences("PlayerUSERID", Context.MODE_PRIVATE);
        editName = SharedPref_Name.edit();
        PlayerName = "PlayerDefaultName";
        PlayerName = SharedPref_Name.getString("PlayerUSERID", "DEFAULT");

        SharedPref_Score = getContext().getSharedPreferences("PlayerUSERSCORE", Context.MODE_PRIVATE);
        editScore = SharedPref_Score.edit();
        PlayerScore = 0;
        PlayerScore = SharedPref_Score.getInt("PlayerUSERSCORE", 0);

        // Create Alert Dialog
        AlertObject = new Alert(this);
        alert = new AlertDialog.Builder(getContext());

        // Allow player to input name
        final EditText input = new EditText(getContext());

        // Define the input method where 'enter' key is disabled
        input.setInputType(InputType.TYPE_CLASS_TEXT);

        // Define max of 20 chars to be entered for name field
        int maxLength = 20;
        InputFilter[] FilterArray = new InputFilter[1];
        FilterArray[0] = new InputFilter.LengthFilter(maxLength);
        input.setFilters(FilterArray);

        // Setup the alert dialog
        alert.setTitle("Game Over");
        alert.setMessage("Enter Name:");
        alert.setCancelable(false);

        alert.setIcon(R.drawable.star);
        alert.setView(input);

        alert.setPositiveButton("Ok",
                new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface arg0, int arg1)
                    {
                        PlayerName = input.getText().toString();
                        editName.putString("PlayerUSERID", PlayerName);
                        editName.commit();

                        PlayerScore = m_Score;
                        editScore.putInt("PlayerUSERSCORE", PlayerScore);
                        editScore.commit();

                        GameState = GAME_STATES.START_UP;

                        Intent intent = new Intent();
                        intent.setClass(getContext(), Mainmenu.class);
                        activityTracker.startActivity(intent);
                    }
                }
        );

        // Tutorial 14
        sensor = (SensorManager)getContext().getSystemService(Context.SENSOR_SERVICE);
        sensor.registerListener(this, sensor.getSensorList(Sensor.TYPE_ACCELEROMETER).get(0), SensorManager.SENSOR_DELAY_NORMAL);

        ball = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ball), ScreenWidth / 6, ScreenHeight / 6, true);

        // Make the GamePanel focusable so it can handle events
        setFocusable(true);

        // Load Button Images
        btn_jump_tex = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.jump_button), ScreenWidth / 6, ScreenHeight / 6, true);
        btn_jump_pos = new Vector3(0, 6.5f * ScreenHeight / 8, 0);

        btn_slide_tex = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.slide_button), ScreenWidth / 6, ScreenHeight / 6, true);
        btn_slide_pos = new Vector3(0, 5 * ScreenHeight / 8, 0);

        btn_pause_tex = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.pause_button), ScreenWidth / 11, ScreenHeight / 8, true);
        btn_pause_pos = new Vector3(8 * ScreenWidth / 9, 18, 0);

        btn_start_tex = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.begin_button), ScreenWidth / 3, ScreenHeight / 4, true);
        btn_start_pos = new Vector3(ScreenWidth / 3, ScreenHeight / 3, 0);

        // Timer
        f_timer = 0.f;

        timer = 0.f;

        // GameState
        GameState = GAME_STATES.START_UP;

        // MeshList
        GameObjectManager.getInstance().InitMeshlist(this.getContext(),ScreenWidth ,ScreenHeight);

        // GameObjects
        //m_Player = GameObjectManager.getInstance().CreateGameObject(new Vector3(0, ScreenHeight / 2, 0), shipArr[0], true);
        SpriteAnimation temp = new SpriteAnimation(Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(getResources(), R.drawable.player_spritesheet),
                ScreenWidth / 8, ScreenHeight / 6, true), 320, 64, 5, 5);
        m_Player = GameObjectManager.getInstance().CreateGameObject(new Vector3(ScreenWidth * 0.05f, ScreenHeight / 2, 0), temp, true);
        m_Player.gravityApply = true;
        m_Player.vel = new Vector3(0,0,0);
        m_Player.name = "player";

        // LoadMap
        levelLoader = new LevelLoader(this.getContext());
        levelLoader.Init(ScreenWidth, ScreenHeight);

        // Screen Move Rate
        ScreenMoveRate = (int)(ScreenWidth * 0.2);
        BGMoveRate = 100;
        ScreenOffset = 0;
        MoveScreen = true;

        // Levels
        CurrLevel = levelLoader.LoadLevel(0, true, ScreenMoveRate);
        NextLevel = levelLoader.LoadLevel(1, false, ScreenMoveRate);

        // Gravity
        m_Gravity = new Vector3(0, 20f, 0);
    }

    //must implement inherited abstract methods
    public void surfaceCreated(SurfaceHolder holder) {
        // Create the thread
        if (!myThread.isAlive()) {
            myThread = new GameThread(getHolder(), this);
            myThread.startRun(true);
            myThread.start();
            soundManager.StartBGM();
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // Destroy the thread
        if (myThread.isAlive()) {
            myThread.startRun(false);
            StopVibrate();
            soundManager.Exit();
        }
        boolean retry = true;
        while (retry) {
            try {
                myThread.join();
                retry = false;
            } catch (InterruptedException e) {
            }
        }

        // Release the memory
        sensor.unregisterListener(this);
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    public void RenderStartUp(Canvas canvas) {

        if (canvas == null) {
            return;
        }

        RenderBackground(canvas);
        RenderPlayer(canvas);

        //RenderTextOnScreen(canvas, Integer.toString(3 - (int)f_timer), 1100, 600, 500);

        RenderButton(canvas, btn_start_tex, btn_start_pos);

        // Debug State
        //RenderTextOnScreen(canvas, "START-UP", 130, 75, 50);
    }

    public void RenderGameplay(Canvas canvas) {

        if (canvas == null) {
            return;
        }

        RenderBackground(canvas);
        RenderPlayer(canvas);

        // Render Objects
        RenderGameObjects(canvas);

        // Print FPS
        //RenderTextOnScreen(canvas, "vel: " + m_Player.vel.VectorToStr(), 130, 125, 50);

        // Draw SpriteAnim
        //anim_coin.draw(canvas);
        //anim_coin.setX(coinX);
        //anim_coin.setY(coinY);

        RenderButton(canvas, btn_jump_tex, btn_jump_pos);
        //RenderButton(canvas, btn_slide_tex, btn_slide_pos);
        RenderButton(canvas, btn_pause_tex, btn_pause_pos);

        // Debug State
        //RenderTextOnScreen(canvas, "IN-GAME", ScreenWidth /2, ScreenHeight /2, 50);

        RenderTextOnScreen(canvas, "SCORE: " + Integer.toString(m_Score), (int)(ScreenWidth * 0.7), (int)(ScreenHeight * 0.1), (int)(ScreenHeight * 0.05));

        // Print FPS
        RenderTextOnScreen(canvas, "FPS: " + FPS, ScreenWidth / 30, ScreenHeight/ 15 , 25);

        RenderTextOnScreen(canvas, "Time: " + timer, ScreenWidth/2, ScreenHeight / 15, 25);

        // tutorial 8
        //RenderStarAsLifes(canvas);
        //Renderpowerbar(canvas);

        // tutorial 14
        //canvas.drawBitmap(ball, ballX, ballY, null);

    }

    private void RenderBackground(Canvas canvas)
    {
        canvas.drawBitmap(ScaledBackground, bgX, bgY, null); // 1st BG
        canvas.drawBitmap(ScaledBackground, bgX + ScreenWidth, bgY, null); // 2nd Image, bgY + ScreenHeight for vertical
    }

    private void RenderButton(Canvas canvas, Bitmap btnToRender, Vector3 pos)
    {
        canvas.drawBitmap(btnToRender, pos.x, pos.y, null);
    }

    private void RenderPlayer(Canvas canvas) {
        if (m_Player.IsBitmap)
        {
            canvas.drawBitmap(m_Player.texture, m_Player.pos.x, m_Player.pos.y, null);
        }
        else
        {
            m_Player.spriteAnimation.draw(canvas);
            m_Player.spriteAnimation.setX((int)m_Player.pos.x);
            m_Player.spriteAnimation.setY((int)m_Player.pos.y);
        }
    }

    private void RenderGameObjects(Canvas canvas)
    {
        for (GameObject i : GameObjectManager.getInstance().m_GoList)
        {
            if (!i.active)
                continue;

            int size = GameObjectManager.getInstance().m_GoList.size();

            if (i.IsBitmap)
            {
                // Is just single texture
                canvas.drawBitmap(i.texture, i.pos.x, i.pos.y, null);
            }
            else
            {
                // Is SpriteAnimation
                i.spriteAnimation.draw(canvas);
                i.spriteAnimation.setX((int)i.pos.x);
                i.spriteAnimation.setY((int)i.pos.y);
            }
        }
    }

    //Update method to update the game play
    public void update(float dt, float fps) {
        FPS = fps;

        switch (GameState) {
            case START_UP: {

                // Update ship.
                shipArrIdx++;
                shipArrIdx %= 4;

                // Make SpriteAnim
                //anim_coin.update(System.currentTimeMillis());
                break;
            }


            case INGAME: {

                timer += dt;

/*                if(jumping == true)
                {
                   m_Player.vel.y = m_Player.vel.y + (float)(-9.8) * dt;
                    m_Player.pos.y = m_Player.pos.y + m_Player.vel.y * dt;
                    jumping = false;
                }*/

                // 4e) Update the spaceship images / shipIndex so that the animation will occur.
                shipArrIdx++;
                shipArrIdx %= 4;

                // Make SpriteAnim
                //anim_coin.update(System.currentTimeMillis());

                m_Player.spriteAnimation.update(System.currentTimeMillis());

                // Update GameObjects
                GameObjectManager.getInstance().CleanUp(false);
                for (GameObject i : GameObjectManager.getInstance().m_GoList) {

                    if (!i.active)
                        continue;

                    // Update Pallets
                    if (i.name == "pallet") {
                        if (!i.parentRope.active)
                            i.gravityApply = true;
                    }

                    // Update Player
                    if (i.equals(m_Player))
                        i.Update(dt, m_Gravity);

                    // Update all objects except player
                    if (MoveScreen && !i.equals(m_Player))
                    {
                        i.Update(dt, m_Gravity);
                    }
                    else if (!MoveScreen&& !i.equals(m_Player))
                    {
                        if (i.name.equals("pallet") || i.name.equals("platform"))
                        {
                            // Function that only handles special effects
                            i.UpdateEffect(dt, m_Gravity);
                        }
                    }

                    // Any Object with x < 0, set active to false
                    if (i.IsBitmap && i.pos.x < -i.texture.getWidth() / 2)
                        i.active = false;
                    else if (!i.IsBitmap && i.pos.x < -i.spriteAnimation.getSpriteWidth() / 2)
                        i.active = false;
                }

                // Update Screen Values
                if (MoveScreen) {
                    ScreenOffset += ScreenMoveRate * dt;

                    CurrLevel.Update(dt, ScreenMoveRate);
                    NextLevel.Update(dt, ScreenMoveRate);

                    // Do level switch if needed
                    if (!CurrLevel.m_OnScreen) {
                        CurrLevel = NextLevel;
                        CurrLevel.m_LevelLength = ScreenWidth;

                        Random rand = new Random();

                        NextLevel = levelLoader.LoadLevel(rand.nextInt(5), false, ScreenMoveRate);

                        m_Score++;
                        toast.show();
                    }

                    // Update BG
                    bgX -= BGMoveRate * dt;

                    if (bgX < -ScreenWidth) {
                        bgX = 0;
                    }
                }

                MoveScreen = true;

                // Check Collision
                for (GameObject i : GameObjectManager.getInstance().m_GoList) {
                    if (!i.active)
                        continue;

                    // Check Collision between player and objects
                    if (!i.equals(m_Player)) {

                        if (i.name.equals("pallet")) {
                            for (GameObject i2 : GameObjectManager.getInstance().m_GoList) {

                                if (!i2.active)
                                    continue;

                                if (i2.name.equals("spike")) {
                                    if (CheckCollision(i, i2, 0, dt)) {
                                        int checkX = (int) (i.pos.x);
                                        int checkY = (int) (i.pos.y + i.texture.getHeight() / 2);

                                        if (checkY < i2.pos.y) {
                                            if (i.IsFalling) {
                                                i.vel.y = -m_Gravity.y;
                                                i.pos.y -= i.texture.getHeight() / 12;
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // OFFSET SHOULD CHANGE, ITS WEIRD RN
                        if (CheckCollision(m_Player, i, /*m_Player.pos.x + m_Player.spriteAnimation.getSpriteWidth() / 2*/m_Player.spriteAnimation.getSpriteWidth() / 3, dt)) {
                            if (i.KillPlayer) {
                                // Kill player
                                /*
                                GameState = GAME_STATES.START_UP;
                                m_Player.pos.x = 0;
                                m_Player.pos.y = (ScreenHeight / 2);
                                m_Score = 0;
                                timer = 0;
                                */
                                GameObjectManager.getInstance().CleanUp(true);
                                //CurrLevel = levelLoader.LoadLevel(0, true, ScreenMoveRate);
                                //NextLevel = levelLoader.LoadLevel(1, false, ScreenMoveRate);

                                soundManager.StopBGM();
                                soundManager.PlaySound("Die");
                                showAlert = true;
                                GameState = GAME_STATES.ENDGAME;
                                break;
                            }

                            int checkX = (int) (m_Player.pos.x + m_Player.spriteAnimation.getSpriteWidth() / 8);
                            int checkY = (int) (m_Player.pos.y + m_Player.spriteAnimation.getSpriteHeight() / 2);

                            if (checkX < i.pos.x) {
                                if (!i.name.equals("pallet") || !i.name.equals("platform"))
                                    MoveScreen = false;
                            }

                            if (checkY < i.pos.y) {
                                if (m_Player.IsFalling) {
                                    m_Player.vel.y = -m_Gravity.y;
                                    m_Player.pos.y -= m_Player.spriteAnimation.getSpriteHeight() / 12;
                                    m_Player.CanJump = true;
                                }
                            }
                        }
/*                        int checkX = (int)(m_Player.pos.x + ScreenOffset) / levelLoader.TileWidth;
                        int checkY = (int)m_Player.pos.y / levelLoader.TileHeight;

                        // Check below
                        if ((CurrLevel.m_CollisionGrid[checkY + 2][checkX] == 1) || (CurrLevel.m_CollisionGrid[checkY + 2][checkX + 1] == 1))
                        {
                            CanPlayerMoveVertical = false;
                            m_Player.vel.y = -9.8f;
                        }

                        // Check Right
                        if ((CurrLevel.m_CollisionGrid[checkY][checkX + 1] == 1))
                        {
                            CanPlayerMoveHorizontal = false;
                        }*/
                        }
                    }

                //SensorMove();
                    break;
            }

            case ENDGAME:
                if (showAlert) {
                    AlertObject.RunAlert();
                    showAlert = false;
                }
                break;
        }
    }


    // Rendering is done on Canvas
    public void doDraw(Canvas canvas) {
        switch (GameState) {
            case START_UP:
                RenderStartUp(canvas);
                break;

            case INGAME:
                RenderGameplay(canvas);
                break;

            case ENDGAME:
                RenderBackground(canvas);
                break;
        }
    }

    // Print text on screen
    public void RenderTextOnScreen(Canvas canvas, String text, int posX, int posY, int textsize) {
        Paint paint = new Paint();

        // 0 - 255
        // Alpha - 255 == opaque
        // RBG - 255 == white
        paint.setARGB(255, 255, 0, 0);
        paint.setStrokeWidth(100);
        paint.setTextSize(textsize);
        paint.setTypeface(myfont); // tutorial 8
        canvas.drawText(text, posX, posY, paint);
    }

    // Tutorial 8
    private void RenderStarAsLifes(Canvas canvas)
    {
        if(numstar == 3)
        {
            canvas.drawBitmap(star, 28, ScreenHeight - 700, null);
            canvas.drawBitmap(star, 78, ScreenHeight - 700, null);
            canvas.drawBitmap(star, 128, ScreenHeight - 700, null);
        }
        else if(numstar == 2)
        {
            canvas.drawBitmap(star, 28, ScreenHeight - 700, null);
            canvas.drawBitmap(star, 78, ScreenHeight - 700, null);
        }
        else if(numstar == 1)
        {
            canvas.drawBitmap(star, 28, ScreenHeight - 700, null);
        }
    }

    // Tutorial 8
    private void Renderpowerbar(Canvas canvas)
    {
        Paint paint = new Paint();

        // Draw a rectangle box
        paint.setColor(Color.RED);
        paint.setStrokeWidth(10);
        //paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawRect(ScreenWidth/ 20 + 5, ScreenHeight /20 - 5, 4 * ScreenWidth /20, 2 * ScreenHeight/ 25, paint);

        // Fill the rectangle
        paint.setColor(Color.YELLOW);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(ScreenWidth/ 20 + 8, ScreenHeight /20, ScreenWidth /20 + energy, 2 * ScreenHeight/ 25 - 5, paint);
    }

    // Tutorial 9
    public void StartVibrate()
    {
        long pattern[] = {0,50,0};
        if (m_Vibrator.hasVibrator()) {
            m_Vibrator.vibrate(pattern, -1);
        }
        Log.v(VIEW_LOG_TAG, "test if vibration has occured");
    }

    public void StopVibrate()
    {
        if (m_Vibrator.hasVibrator()){
            m_Vibrator.cancel();
        }
    }

    // Tutorial 13
    public void ToastMessage(Context context)
    {
        text = "Stage Done!";
        toastTime = Toast.LENGTH_SHORT;
        toast = Toast.makeText(context, text, toastTime);
    }

    // Tutorial 14
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        values = event.values;
    }

    public void SensorMove()
    {
        float testX, testY;

        testX = ballX + (values[1] * ((System.currentTimeMillis() - lastTime) / 1000));
        testY = ballX + (values[0] * ((System.currentTimeMillis() - lastTime) / 1000));

        // Ball is going out of the screen in X-Axis
        if (testX <= ball.getWidth() / 2 || testX >= ScreenMoveRate - ball.getWidth() / 2) {
            // Ball is on screen in Y-Axis
            if (testY > /*shipArr[shipArrIdx]*/ball.getHeight()/2 && testY < ScreenHeight - ball.getHeight() / 2)
            {
                ballY = testY;
            }
        }
        // Ball is of screen in Y-Axis
        else if (testY <= ball.getHeight() / 2 || testY >= ScreenHeight - ball.getHeight() / 2)
        {
            ballY = testY;
        }
        // Ball is on screen in X-Axis
        else if (testX > ball.getWidth() / 2 && testX < ScreenWidth - ball.getWidth() / 2)
        {
            ballX = testX;
        }
        // Both axis of ball are in screen
        else
        {
            ballX = testX;
            ballY = testY;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        /*
        // 5) In event of touch on screen, the spaceship will relocate to the point of touch
        short m_touchX = (short) event.getX();
        short m_touchY = (short) event.getY();

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            mX = (short) (m_touchX - shipArr[shipArrIdx].getWidth() / 2);
            mY = (short) (m_touchY - shipArr[shipArrIdx].getHeight() / 2);
        }
        return super.onTouchEvent(event);
        */

        short m_touchX = (short) event.getX();
        short m_touchY = (short) event.getY();

        int action = event.getAction(); // Check for the action of touch

        switch(action) {

            case MotionEvent.ACTION_DOWN:

                // To check finger touch x,y within image, i.e holding down on the image
                // Check if button pressed
                if (GameState == GAME_STATES.START_UP) // START UP
                {
                    if (CheckCollision(
                            (int) btn_start_pos.x, (int) btn_start_pos.y, btn_start_tex.getWidth(), btn_start_tex.getHeight(),
                            m_touchX, m_touchY, 0, 0)) {
                        GameState = GAME_STATES.INGAME;
                    }
                }
                else if(GameState == GAME_STATES.INGAME) // IN GAME
                {
                    //if (CheckCollision(
                            //(int)m_Player.pos.x, (int)m_Player.pos.y, (int)m_Player.GetScale().x, (int)m_Player.GetScale().y,
                            //m_touchX, m_touchY, 0, 0))
                        //b_moveShip = true;
                    //else
                        //b_moveShip = false;


                    // JUMP BUTTON
                    if (CheckCollision((int) btn_jump_pos.x, (int) btn_jump_pos.y, btn_jump_tex.getWidth(), btn_jump_tex.getHeight(), m_touchX, m_touchY, 0, 0)) {
                        if (m_Player.pos.y <= 269) {
                            m_Player.IsOnGround = false;
                        } else {
                            m_Player.IsOnGround = true;

                            // Shan value
                            //m_Player.vel.y = -350;

                            // Dondon value
                            //m_Player.vel.y = -180;

                            // Ratio
                            //m_Player.vel.y = (float)(-ScreenHeight / 2.8);
                        }

                        if (m_Player.CanJump) {
                            // Ratio
                            m_Player.vel.y = (float) (-ScreenHeight / 2.5);
                            m_Player.CanJump = false;
                            StartVibrate();
                            soundManager.PlaySound("Jump");
                        }
                    }

                    // SLIDE BUTTON
                    if (CheckCollision((int) btn_slide_pos.x, (int) btn_slide_pos.y, btn_slide_tex.getWidth(), btn_slide_tex.getHeight(), m_touchX, m_touchY, 0, 0))
                    {


                    }

                    // PAUSE BUTTON
                    if (CheckCollision((int) btn_pause_pos.x, (int) btn_pause_pos.y, btn_pause_tex.getWidth(), btn_pause_tex.getHeight(), m_touchX, m_touchY, 0, 0)) {
                        m_Player.pos.x = 0;
                        GameState = GAME_STATES.START_UP;
                    }

                }

                break;

            case MotionEvent.ACTION_MOVE:

                if (b_moveShip == true)
                {
                    //m_Player.pos.x = (short) (m_touchX - shipArr[shipArrIdx].getWidth() / 2);
                    //m_Player.pos.y = (short) (m_touchY - shipArr[shipArrIdx].getHeight() / 2);

                    // Check Collision with coin
                    if (CheckCollision(
                            (int)m_Player.pos.x, (int)m_Player.pos.y, (int)m_Player.GetScale().x, (int)m_Player.GetScale().y,
                            coinX, coinY, anim_coin.getSpriteWidth(), anim_coin.getSpriteHeight()))
                    {
                        m_Score++;
                        Random randomNum = new Random();
                        coinX = randomNum.nextInt(ScreenWidth);
                        coinY = randomNum.nextInt(ScreenHeight);
                    }

                }

                for (GameObject i : GameObjectManager.getInstance().m_GoList)
                {

                    if (!i.active)
                        continue;

                    if (i.name.equals("rope")) {
                        if (CheckCollision(
                                (int) i.pos.x, (int) i.pos.y, i.texture.getWidth(), i.texture.getHeight(),
                                m_touchX, m_touchY, 0, 0)) {
                            i.active = false;
                        }
                    }

                    if (i.name.equals("platform"))
                    {
                        if (CheckCollision(
                                (int) i.pos.x, (int) i.pos.y, i.texture.getWidth(), i.texture.getHeight(),
                                m_touchX, m_touchY, 0, 0)) {
                            i.IsTouched = true;
                        }
                    }
                }
                break;

            case MotionEvent.ACTION_UP:
                // Check if any platform object has been touched
                for (GameObject i : GameObjectManager.getInstance().m_GoList) {

                    if (!i.active)
                        continue;

                    if (!i.name.equals("platform"))
                        continue;

                    if (i.IsTouched)
                    {
                        // Check if release point is above the start pos and within a range on the x-axis
                        if (m_touchY < i.pos.y && (m_touchX < i.pos.x + ScreenWidth / 4 && m_touchX > i.pos.x - ScreenWidth / 4))
                        {
                            GameObject temp = i;
                            while (temp.LeftObject != null)
                            {
                                temp.LeftObject.IsMoving = true;
                                temp = temp.LeftObject;
                            }

                            temp = i;
                            while (temp.RightObject != null)
                            {
                                temp.RightObject.IsMoving = true;
                                temp = temp.RightObject;
                            }

                            i.IsMoving = true;
                        }
                        i.IsTouched = false;
                    }
                }
                break;
        }

        return true;
    }

    // Collision Check
    public boolean CheckCollision(int x1, int y1, int w1, int h1, int x2, int y2, int w2, int h2) {

        Vector3 box1_TopLeft = new Vector3(x1, y1, 0);
        Vector3 box1_TopRight = new Vector3(x1 + w1, y1, 0);
        Vector3 box1_BtmLeft = new Vector3(x1, y1 + h1, 0);
        Vector3 box1_BtmRight = new Vector3(x1 + w1, y1 + h1, 0);

        Vector3 box2_TopLeft = new Vector3(x2, y2, 0);
        Vector3 box2_TopRight = new Vector3(x2 + w2, y2, 0);
        Vector3 box2_BtmLeft = new Vector3(x2, y2 + h2, 0);
        Vector3 box2_BtmRight = new Vector3(x2 + w2, y2 + h2, 0);

        // Note: For y-axis downwards is positive
        // Note: Origin in top-left croner of screen

        // Check if box2 collides with box1
        // TopLeft
        if (box2_TopLeft.x >= box1_TopLeft.x && box2_TopLeft.x <= box1_TopRight.x)
        {
            if (box2_TopLeft.y >= box1_TopLeft.y && box2_TopLeft.y <= box1_BtmLeft.y)
                return true;
        }

        // TopRight
        if (box2_TopRight.x >= box1_TopLeft.x && box2_TopRight.x <= box1_TopRight.x)
        {
            if (box2_TopRight.y >= box1_TopLeft.y && box2_TopRight.y <= box1_BtmLeft.y)
                return true;
        }

        // BtmLeft
        if (box2_BtmLeft.x >= box1_TopLeft.x && box2_BtmLeft.x <= box1_TopRight.x)
        {
            if (box2_BtmLeft.y >= box1_TopLeft.y && box2_BtmLeft.y <= box1_BtmLeft.y)
                return true;
        }

        // BtmRight
        if (box2_BtmRight.x >= box1_TopLeft.x && box2_BtmRight.x <= box1_TopRight.x)
        {
            if (box2_BtmRight.y >= box1_TopLeft.y && box2_BtmRight.y <= box1_BtmLeft.y)
                return true;
        }
        return false;
    }

    // Collision Check w/ GameObjects
    public boolean CheckCollision(GameObject go1, GameObject go2, float offset, float dt) {

        int h1 = (int)(go1.GetScale().y);
        int w1 = (int)(go1.GetScale().x + offset);
        int x1 = (int)((go1.pos.x + go1.vel.x * dt) - w1 / 2);
        int y1 = (int)((go1.pos.y + go1.vel.y * dt) - h1 / 2);

        int h2 = (int)(go2.GetScale().y);
        int w2 = (int)(go2.GetScale().x + offset);
        int x2 = (int)((go2.pos.x + go2.vel.x * dt) - w2 / 2);
        int y2 = (int)((go2.pos.y + go2.vel.y * dt) - h2 / 2);

        Vector3 box1_TopLeft = new Vector3(x1, y1, 0);
        Vector3 box1_TopRight = new Vector3(x1 + w1, y1, 0);
        Vector3 box1_BtmLeft = new Vector3(x1, y1 + h1, 0);
        Vector3 box1_BtmRight = new Vector3(x1 + w1, y1 + h1, 0);

        Vector3 box2_TopLeft = new Vector3(x2, y2, 0);
        Vector3 box2_TopRight = new Vector3(x2 + w2, y2, 0);
        Vector3 box2_BtmLeft = new Vector3(x2, y2 + h2, 0);
        Vector3 box2_BtmRight = new Vector3(x2 + w2, y2 + h2, 0);

        // Note: For y-axis downwards is positive
        // Note: Origin in top-left croner of screen

        // Check if box2 collides with box1
        // TopLeft
        if (box2_TopLeft.x >= box1_TopLeft.x && box2_TopLeft.x <= box1_TopRight.x)
        {
            if (box2_TopLeft.y >= box1_TopLeft.y && box2_TopLeft.y <= box1_BtmLeft.y)
                return true;
        }

        // TopRight
        if (box2_TopRight.x >= box1_TopLeft.x && box2_TopRight.x <= box1_TopRight.x)
        {
            if (box2_TopRight.y >= box1_TopLeft.y && box2_TopRight.y <= box1_BtmLeft.y)
                return true;
        }

        // BtmLeft
        if (box2_BtmLeft.x >= box1_TopLeft.x && box2_BtmLeft.x <= box1_TopRight.x)
        {
            if (box2_BtmLeft.y >= box1_TopLeft.y && box2_BtmLeft.y <= box1_BtmLeft.y)
                return true;
        }

        // BtmRight
        if (box2_BtmRight.x >= box1_TopLeft.x && box2_BtmRight.x <= box1_TopRight.x)
        {
            if (box2_BtmRight.y >= box1_TopLeft.y && box2_BtmRight.y <= box1_BtmLeft.y)
                return true;
        }
        return false;
    }

}


