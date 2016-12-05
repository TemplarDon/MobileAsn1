package com.sidm.mgp_lab02_153492y;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * Created by Donovan's PC on 5/12/2016.
 */
public class GameObjectManager {
    private static GameObjectManager ourInstance = new GameObjectManager();

    public static GameObjectManager getInstance() {
        return ourInstance;
    }

    private GameObjectManager() {
    }

    static HashMap<String, Bitmap>meshList = new HashMap<String, Bitmap>();

    public static void InitMeshlist(Context c, int ScreenWidth, int ScreenHeight)
    {
        //meshList.put("ground", Bitmap.createScaledBitmap(BitmapFactory.decodeResource(c.getResources(), R.drawable.test), ScreenWidth / 5, ScreenHeight / 5, true));
        meshList.put("ground", BitmapFactory.decodeResource(c.getResources(), R.drawable.test2));
        meshList.put("pallet", BitmapFactory.decodeResource(c.getResources(), R.drawable.test));
        meshList.put("rope", BitmapFactory.decodeResource(c.getResources(), R.drawable.test));
    }

    static LinkedList<GameObject> m_GoList = new LinkedList<>();

    public static GameObject CreateGameObject(Vector3 pos, Bitmap texture, Boolean active)
    {
        GameObject test = new GameObject();

        test.active = active;
        test.pos = pos;
        test.texture = texture;
        test.spriteAnimation = null;
        test.vel = new Vector3(0,0,0);

        test.IsBitmap = true;

        m_GoList.push(test);

        return test;
    }

    public static GameObject CreateGameObject(Vector3 pos, SpriteAnimation spriteAnimation, Boolean active)
    {
        GameObject test = new GameObject();

        test.active = active;
        test.pos = pos;
        test.spriteAnimation = spriteAnimation;
        test.texture = null;
        test.vel = new Vector3(0,0,0);

        test.IsBitmap = false;

        m_GoList.push(test);

        return test;
    }
}