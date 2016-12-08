package com.sidm.mgp_lab02_153492y;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Button;
import android.os.Vibrator;

import java.util.LinkedList;
import java.util.Random;

/**
 * Created by 153492Y on 11/24/2016.
 */

public class GamePanelSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

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
        SHUT_DOWN,
    }

    // Init SpriteAnimation
    private SpriteAnimation anim_coin;

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

    private Button btn_home;
    private Bitmap btn_home_tex;
    Vector3 btn_home_pos;

    private Button btn_start;
    private Bitmap btn_start_tex;
    Vector3 btn_start_pos;

    private Button btn_resume;
    private Bitmap btn_resume_tex;
    Vector3 btn_resume_pos;

    // Timer
    float f_timer;

    // Rotate
    boolean rotate = false;

    // Score
    int m_Score = 0;
    float timer;

    // Player
    GameObject m_Player;
    private SpriteAnimation anim_player;

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

    Vibrator v;

    //constructor for this GamePanelSurfaceView class
    public GamePanelSurfaceView(Context context) {

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

        // Make the GamePanel focusable so it can handle events
        setFocusable(true);

        // Load Button Images
        btn_jump_tex = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.jump_button), ScreenWidth / 6, ScreenHeight / 6, true);
        btn_jump_pos = new Vector3(0, 6.5f * ScreenHeight / 8, 0);

        btn_slide_tex = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.slide_button), ScreenWidth / 6, ScreenHeight / 6, true);
        btn_slide_pos = new Vector3(0, 5 * ScreenHeight / 8, 0);

        btn_pause_tex = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.pause_button), ScreenWidth / 10, ScreenHeight / 10, true);
        btn_pause_pos = new Vector3(8 * ScreenWidth / 9, 18, 0);

        btn_start_tex = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.begin_button), ScreenWidth / 3, ScreenHeight / 4, true);
        btn_start_pos = new Vector3(ScreenWidth / 3, ScreenHeight / 3, 0);

        btn_resume_tex = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.resume_button), ScreenWidth / 3, ScreenHeight / 4, true);
        btn_resume_pos = new Vector3(ScreenWidth / 3, ScreenHeight / 6, 0);

        btn_home_tex = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.home_button), ScreenWidth / 3, ScreenHeight / 4, true);
        btn_home_pos = new Vector3(ScreenWidth / 3,  ScreenHeight / 2, 0);

        // Timer
        f_timer = 0.f;

        timer = 0.f;

        // GameState
        GameState = GAME_STATES.START_UP;

        // MeshList
        GameObjectManager.getInstance().InitMeshlist(this.getContext(),ScreenWidth ,ScreenHeight);

        // GameObjects
        anim_player = new SpriteAnimation(Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(getResources(), R.drawable.player_spritesheet),
                ScreenWidth / 8, ScreenHeight / 6, true), 320, 64, 5, 5);
        m_Player = GameObjectManager.getInstance().CreateGameObject(new Vector3(ScreenWidth / 32, ScreenHeight / 2, 0), shipArr[0], true);
        m_Player.gravityApply = true;
        m_Player.vel = new Vector3(0,0,0);
        m_Player.name = "player";

        // LoadMap
        levelLoader = new LevelLoader(this.getContext());
        levelLoader.Init(ScreenWidth, ScreenHeight);

        // Screen Move Rate
        ScreenMoveRate = 180;
        BGMoveRate = 100;
        ScreenOffset = 0;
        MoveScreen = true;

        // Levels
        CurrLevel = levelLoader.LoadLevel(0, true, ScreenMoveRate);
        NextLevel = levelLoader.LoadLevel(1, false, ScreenMoveRate);

        // Gravity
        m_Gravity = new Vector3(0, 15f, 0);

        // Vibration
        v = (Vibrator) this.getContext().getSystemService(Context.VIBRATOR_SERVICE);
    }

    //must implement inherited abstract methods
    public void surfaceCreated(SurfaceHolder holder) {
        // Create the thread
        if (!myThread.isAlive()) {
            myThread = new GameThread(getHolder(), this);
            myThread.startRun(true);
            myThread.start();
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // Destroy the thread
        if (myThread.isAlive()) {
            myThread.startRun(false);


        }
        boolean retry = true;
        while (retry) {
            try {
                myThread.join();
                retry = false;
            } catch (InterruptedException e) {
            }
        }
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
        RenderTextOnScreen(canvas, "FPS: " + FPS, ScreenWidth / 16, ScreenHeight/ 15 , 30);


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

        RenderTextOnScreen(canvas, "SCORE: " + Integer.toString(m_Score), ScreenWidth /2, ScreenHeight/ 15, 30);

        //RenderTextOnScreen(canvas, "Time: " + timer, ScreenWidth/2, ScreenHeight / 3, 50);

    }

    public void RenderPause(Canvas canvas)
    {
        RenderButton(canvas, btn_resume_tex, btn_resume_pos);
        RenderButton(canvas, btn_home_tex, btn_home_pos);
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
        if (m_Player.IsBitmap) {
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

                // Update GameObjects
                GameObjectManager.getInstance().CleanUp(false);
                for (GameObject i : GameObjectManager.getInstance().m_GoList) {

                    if (!i.active)
                        continue;

                    // Update sprites
                    if (!i.IsBitmap) {
                        i.spriteAnimation.update(System.currentTimeMillis());
                    }
                    // Update Pallets
                    if (i.name == "pallet") {
                        if (!i.parentRope.active)
                            i.gravityApply = true;
                    }

                    // Update Player
                    if (i.equals(m_Player))
                        i.Update(dt, m_Gravity);

                    // Update all objects except player
                    if (MoveScreen && !i.equals(m_Player)) {
                        i.Update(dt, m_Gravity);
                    }

                    // Any Object with x < 0, set active to false
                    if (i.IsBitmap) {
                        if (i.pos.x < -i.texture.getWidth() / 2)
                            i.active = false;
                    }
                    else
                    {
                        if (i.pos.x < -i.spriteAnimation.getSpriteWidth() / 2)
                            i.active = false;
                    }
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

                        NextLevel = levelLoader.LoadLevel(rand.nextInt(2), false, ScreenMoveRate);
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
                                        int checkY = (int) (i.pos.y + i.texture.getHeight() / 2);

                                        if (checkY < i2.pos.y) {
                                            if (i.IsFalling) {
                                                i.vel.y =  -(m_Gravity.y);
                                                i.pos.y -= i.texture.getHeight() / 16;
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        int offset;
                        if (m_Player.IsBitmap)
                            offset = m_Player.texture.getWidth() / 8;
                        else
                            offset = m_Player.spriteAnimation.getSpriteWidth() / 8;

                        if (CheckCollision(m_Player, i, offset, dt)) {
                            if (i.KillPlayer)
                            {
                                // Kill player
                                GameState = GAME_STATES.START_UP;
                                m_Player.pos.x = ScreenWidth / 32;
                                m_Player.pos.y = (ScreenHeight / 2);
                                m_Player.vel.y = 0;
                                m_Score = 0;
                                timer = 0;
                                GameObjectManager.getInstance().CleanUp(true);
                                CurrLevel = levelLoader.LoadLevel(0, true, ScreenMoveRate);
                                NextLevel = levelLoader.LoadLevel(1, false, ScreenMoveRate);
                                break;
                            }

                            int checkX;
                            int checkY;
                            if (m_Player.IsBitmap)
                            {
                                checkX = (int) (m_Player.pos.x + m_Player.texture.getWidth() / 8);
                                checkY = (int) (m_Player.pos.y + m_Player.texture.getHeight() / 3);
                            }
                            else {
                                checkX = (int) (m_Player.pos.x + m_Player.spriteAnimation.getSpriteWidth() / 3);
                                checkY = (int) (m_Player.pos.y + m_Player.spriteAnimation.getSpriteHeight() / 3);
                            }
                            if (checkX < i.pos.x) {
                                if (!i.name.equals("pallet"))
                                    MoveScreen = false;
                            }

                            else if (checkY < i.pos.y) {
                                if (m_Player.IsFalling) {
                                    m_Player.vel.y = -(m_Gravity.y);
                                    //m_Player.pos.y -= m_Player.spriteAnimation.getSpriteHeight();
                                    m_Player.pos.y -= m_Player.texture.getHeight() / 16;
                                }
                            }

                            if(i.name.equals("coin"))
                            {
                                i.active = false;
                                m_Score++;
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
                    break;
            }

            case SHUT_DOWN: {
                break;
            }
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
            case PAUSED:
                RenderPause(canvas);
                break;
            case SHUT_DOWN:
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
        canvas.drawText(text, posX, posY, paint);
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
                    if (CheckCollision((int) btn_jump_pos.x, (int) btn_jump_pos.y, btn_jump_tex.getWidth(), btn_jump_tex.getHeight(), m_touchX, m_touchY, 0, 0))
                    {
                        // Vibrate for 500 milliseconds
                        if(v.hasVibrator())
                        {
                            v.vibrate(500);
                        }

                        m_Player.IsJumping = true;

                        if(m_Player.pos.y <= 269)
                        {
                            m_Player.IsOnGround = false;
                        }
                        else
                        {
                            m_Player.IsOnGround = true;

                            // Shan value
                            m_Player.vel.y = -350;

                            // Dondon value
                            //m_Player.vel.y = -180;
                        }
                    }
                    // SLIDE BUTTON
                    if (CheckCollision((int) btn_slide_pos.x, (int) btn_slide_pos.y, btn_slide_tex.getWidth(), btn_slide_tex.getHeight(), m_touchX, m_touchY, 0, 0))
                    {


                    }

                    // PAUSE BUTTON
                    if (CheckCollision((int) btn_pause_pos.x, (int) btn_pause_pos.y, btn_pause_tex.getWidth(), btn_pause_tex.getHeight(), m_touchX, m_touchY, 0, 0)) {
                        m_Player.pos.x = 0;
                        GameState = GAME_STATES.PAUSED;
                    }


                }
                else if(GameState == GAME_STATES.PAUSED)
                {
                    // HOME BUTTON
                    if (CheckCollision((int) btn_home_pos.x, (int) btn_home_pos.y, btn_home_tex.getWidth(), btn_home_tex.getHeight(), m_touchX, m_touchY, 0, 0))
                    {
                        GameObjectManager.CleanUp(true);
                        GameState = GAME_STATES.SHUT_DOWN;
                        m_Player.pos.x = 0;
                        Context context = this.getContext();
                        context.startActivity(new Intent(context, Mainmenu.class));
                        break;
                    }

                    if (CheckCollision(
                            (int) btn_resume_pos.x, (int) btn_resume_pos.y, btn_resume_tex.getWidth(), btn_resume_tex.getHeight(),
                            m_touchX, m_touchY, 0, 0)) {
                        GameState = GAME_STATES.INGAME;
                    }


                }

                break;

            case MotionEvent.ACTION_MOVE:

                if (b_moveShip == true)
                {
                    //m_Player.pos.x = (short) (m_touchX - shipArr[shipArrIdx].getWidth() / 2);
                    //m_Player.pos.y = (short) (m_touchY - shipArr[shipArrIdx].getHeight() / 2);

                    // Check Collision with coin
/*                    if (CheckCollision(
                            (int)m_Player.pos.x, (int)m_Player.pos.y, (int)m_Player.GetScale().x, (int)m_Player.GetScale().y,
                            coinX, coinY, anim_coin.getSpriteWidth(), anim_coin.getSpriteHeight()))
                    {
                        m_Score++;
                        Random randomNum = new Random();
                        coinX = randomNum.nextInt(ScreenWidth);
                        coinY = randomNum.nextInt(ScreenHeight);
                    }*/

                }

                for (GameObject i : GameObjectManager.getInstance().m_GoList) {

                    if (!i.active)
                        continue;

                    if(i.IsBitmap) {
                        if (CheckCollision(
                                (int) i.pos.x, (int) i.pos.y, i.texture.getWidth(), i.texture.getHeight(),
                                m_touchX, m_touchY, 0, 0)) {
                            if (i.name.equals("rope")) {
                                i.active = false;
                            }
                        }
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

        int h1 = (int)(go1.GetScale().y + offset);
        int w1 = (int)(go1.GetScale().x + offset);
        int x1 = (int)((go1.pos.x + go1.vel.x * dt) - w1 / 2);
        int y1 = (int)((go1.pos.y + go1.vel.y * dt) - h1 / 2);

        int h2 = (int)(go2.GetScale().y + offset);
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


