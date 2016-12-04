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
    private short GameState;

    // Init SpriteAnimation
    private SpriteAnimation anim_coin;

    // Variables for coins
    private int coinX = 0, coinY = 0;

    // Action bools
    protected boolean b_moveShip = false;

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

    public void RenderGameplay(Canvas canvas) {
        // 2) Re-draw 2nd image after the 1st image ends
        if (canvas == null) {
            return;
        }

        canvas.drawBitmap(ScaledBackground, bgX, bgY, null); // 1st BG
        canvas.drawBitmap(ScaledBackground, bgX + ScreenWidth, bgY, null); // 2nd Image, bgY + ScreenHeight for vertical

        // 4d) Draw the spaceships
        canvas.drawBitmap(shipArr[shipArrIdx], mX, mY, null); // location of the ship, based on touch

        // Bonus) To print FPS on the screen
        RenderTextOnScreen(canvas, "FPS: " + FPS, 130, 75, 50);

        // Draw SpriteAnim
        anim_coin.draw(canvas);
        anim_coin.setX(coinX);
        anim_coin.setY(coinY);
    }


    //Update method to update the game play
    public void update(float dt, float fps) {
        FPS = fps;

        switch (GameState) {
            case 0: {
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
            }
            break;
        }
    }

    // Rendering is done on Canvas
    public void doDraw(Canvas canvas) {
        switch (GameState) {
            case 0:
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
                if (CheckCollision(
                        mX, mY, shipArr[shipArrIdx].getWidth() / 2, shipArr[shipArrIdx].getHeight() / 2,
                        m_touchX, m_touchY, 0, 0))
                    b_moveShip = true;
                else
                    b_moveShip = false;


                break;

            case MotionEvent.ACTION_MOVE:

                if (b_moveShip == true)
                {
                    mX = (short) (m_touchX - shipArr[shipArrIdx].getWidth() / 2);
                    mY = (short) (m_touchY - shipArr[shipArrIdx].getHeight() / 2);

                    // Check Collision with coin
                    if (CheckCollision(
                            mX, mY, shipArr[shipArrIdx].getWidth() / 2, shipArr[shipArrIdx].getHeight() / 2,
                            coinX, coinY, anim_coin.getSpriteWidth() / 2, anim_coin.getSpriteHeight() / 2))
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

    // Collision Check // To be completed
    public boolean CheckCollision(int x1, int y1, int w1, int h1, int x2, int y2, int w2, int h2) {
        if (x2 >= x1 && x2 <= x1 + w1) { // Start to detect collision of the top left corner
            if (y2 >= y1 && y2 <= y1 + h1) // Comparing yellow box to blue box
                return true;
        }
        if (x2 + w2 >= x1 && x2 + w2 <= x1 + w1) { // Top right corner
            if (y2 >= y1 && y2 <= y1 + h1)
                return true;
        }
        return false;
    }
}

