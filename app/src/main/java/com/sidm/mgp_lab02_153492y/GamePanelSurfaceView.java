package com.sidm.mgp_lab02_153492y;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Button;

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
    private short mX = 0, mY = 0;

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
    }

    // Init SpriteAnimation
    private SpriteAnimation anim_coin;

    // Variables for coins
    private int coinX = 0, coinY = 0;

    // Action bools
    protected boolean b_moveShip = false;

    // Buttons
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

    // Player
    GameObject m_Player;

    // LoadMap
    LevelLoader levelLoader;

    // Gravity
    Vector3 m_Gravity;

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
        shipArr[0] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ship2_1), ScreenWidth / 5, ScreenHeight / 5, true);
        shipArr[1] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ship2_2), ScreenWidth / 5, ScreenHeight / 5, true);
        shipArr[2] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ship2_3), ScreenWidth / 5, ScreenHeight / 5, true);
        shipArr[3] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ship2_4), ScreenWidth / 5, ScreenHeight / 5, true);

        // Load the sprite sheet
        anim_coin = new SpriteAnimation(Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(getResources(), R.drawable.flystar),
                ScreenWidth / 5, ScreenHeight / 5, true), 320, 64, 5, 5);

        // Create the game loop thread
        myThread = new GameThread(getHolder(), this);

        // Make the GamePanel focusable so it can handle events
        setFocusable(true);

        // Load Button Images
        btn_slide_tex = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.button), ScreenWidth / 8, ScreenHeight / 8, true);
        btn_slide_pos = new Vector3(0, 1250, 0);

        btn_pause_tex = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.button), ScreenWidth / 8, ScreenHeight / 8, true);
        btn_pause_pos = new Vector3(2250, 0, 0);

        btn_start_tex = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.button), ScreenWidth / 4, ScreenHeight / 4, true);
        btn_start_pos = new Vector3(1100, 600, 0);

        // Timer
        f_timer = 0.f;

        // GameState
        GameState = GAME_STATES.START_UP;

        // MeshList
        GameObjectManager.getInstance().InitMeshlist(this.getContext(),ScreenWidth ,ScreenHeight);

        // GameObjects
        m_Player = GameObjectManager.getInstance().CreateGameObject(new Vector3(0, 700, 0), shipArr[0], true);
        m_Player.gravityApply = true;

        // LoadMap
        levelLoader = new LevelLoader(this.getContext());
        levelLoader.Init(ScreenWidth, ScreenHeight);
        levelLoader.LoadLevel(1);

        // Gravity
        m_Gravity = new Vector3(0, 9.8f, 0);
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
        RenderTextOnScreen(canvas, "START-UP", 130, 75, 50);
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
        RenderTextOnScreen(canvas, "FPS: " + FPS, 130, 75, 50);

        // Draw SpriteAnim
        anim_coin.draw(canvas);
        anim_coin.setX(coinX);
        anim_coin.setY(coinY);

        RenderButton(canvas, btn_slide_tex, btn_slide_pos);
        RenderButton(canvas, btn_pause_tex, btn_pause_pos);

        // Debug State
        RenderTextOnScreen(canvas, "IN-GAME", 130, 75, 50);
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

    private void RenderPlayer(Canvas canvas)
    {
        canvas.drawBitmap(m_Player.texture, m_Player.pos.x, m_Player.pos.y, null); // location of the ship, based on touch
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
                anim_coin.update(System.currentTimeMillis());
                break;
            }


            case INGAME: {
                // 3) Update the background to allow panning effect
                bgX -= 250 * dt;

                if (bgX < -ScreenWidth) {
                    bgX = 0;
                }


                // 4e) Update the spaceship images / shipIndex so that the animation will occur.
                shipArrIdx++;
                shipArrIdx %= 4;

                // Make SpriteAnim
                anim_coin.update(System.currentTimeMillis());

                boolean CanPlayerMove = true;

                // Update GameObjects
                for (GameObject i : GameObjectManager.getInstance().m_GoList)
                {
                    if (!i.active)
                    continue;

                    // Update all objects except player
                    if (!i.equals(m_Player))
                    {
                        i.Update(dt, m_Gravity);

                        if (CheckCollision(m_Player, i))
                        {
                            CanPlayerMove = false;
                        }
                    }
                }

                // Update Player
                if (CanPlayerMove)
                    m_Player.Update(dt, m_Gravity);

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
                if (GameState == GAME_STATES.START_UP) {
                    if (CheckCollision(
                            (int) btn_start_pos.x, (int) btn_start_pos.y, btn_start_tex.getWidth(), btn_start_tex.getHeight(),
                            m_touchX, m_touchY, 0, 0)) {
                        GameState = GAME_STATES.INGAME;
                    }
                }
                else
                {
                    if (CheckCollision(
                            (int)m_Player.pos.x, (int)m_Player.pos.y, (int)m_Player.GetScale().x, (int)m_Player.GetScale().y,
                            m_touchX, m_touchY, 0, 0))
                        b_moveShip = true;
                    else
                        b_moveShip = false;
                }

                break;

            case MotionEvent.ACTION_MOVE:

                if (b_moveShip == true)
                {
                    m_Player.pos.x = (short) (m_touchX - shipArr[shipArrIdx].getWidth() / 2);
                    m_Player.pos.y = (short) (m_touchY - shipArr[shipArrIdx].getHeight() / 2);

                    // Check Collision with coin
                    if (CheckCollision(
                            (int)m_Player.pos.x, (int)m_Player.pos.y, (int)m_Player.GetScale().x, (int)m_Player.GetScale().y,
                            coinX, coinY, anim_coin.getSpriteWidth(), anim_coin.getSpriteHeight()))
                    {
                        Random randomNum = new Random();
                        coinX = randomNum.nextInt(ScreenWidth);
                        coinY = randomNum.nextInt(ScreenHeight);
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
    public boolean CheckCollision(GameObject go1, GameObject go2) {

        int h1 = (int)(go1.GetScale().y);
        int w1 = (int)(go1.GetScale().x);
        int x1 = (int)(go1.pos.x - w1 / 2);
        int y1 = (int)(go1.pos.y - h1 / 2);

        int h2 = (int)(go2.GetScale().y);
        int w2 = (int)(go2.GetScale().x);
        int x2 = (int)(go2.pos.x - w2 / 2);
        int y2 = (int)(go2.pos.y - h2 / 2);

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


