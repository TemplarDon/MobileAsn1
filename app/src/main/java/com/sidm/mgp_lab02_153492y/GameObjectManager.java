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
    static HashMap<String, SpriteAnimation>animationList = new HashMap<String, SpriteAnimation>();

    public static void InitMeshlist(Context c, int ScreenWidth, int ScreenHeight)
    {
        meshList.put("ground", Bitmap.createScaledBitmap(BitmapFactory.decodeResource(c.getResources(), R.drawable.test), ScreenWidth / 16, ScreenHeight / 12, true));
        meshList.put("pallet", Bitmap.createScaledBitmap(BitmapFactory.decodeResource(c.getResources(), R.drawable.box), ScreenWidth / 16, ScreenHeight / 12, true));
        meshList.put("rope", Bitmap.createScaledBitmap(BitmapFactory.decodeResource(c.getResources(), R.drawable.rope), ScreenWidth / 16, ScreenHeight / 4, true));
        meshList.put("spike", Bitmap.createScaledBitmap(BitmapFactory.decodeResource(c.getResources(), R.drawable.spike), ScreenWidth / 16, ScreenHeight / 12, true));

        SpriteAnimation test = new SpriteAnimation(Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(c.getResources(), R.drawable.flystar),
                ScreenWidth / 10, ScreenHeight / 10, true), 320, 64, 5, 5);
        animationList.put("coin", test);
    }

    public static void CleanUp(boolean DeleteAll)
    {
        LinkedList<GameObject> DeleteList = new LinkedList<>();
        if (!DeleteAll) {
            for (GameObject i : GameObjectManager.getInstance().m_GoList) {
                if (!i.active) {
                    // Delete unwanted objects
                    DeleteList.push(i);
                }
            }
        }
        else
        {
            for (GameObject i : GameObjectManager.getInstance().m_GoList) {
                if (!i.name.equals("player")) {
                    // Delete unwanted objects
                    DeleteList.push(i);
                }
            }
        }

        m_GoList.removeAll(DeleteList);
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
        test.scale = new Vector3(texture.getWidth(), texture.getHeight(), 0);

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
        test.scale = new Vector3(spriteAnimation.getSpriteWidth(), spriteAnimation.getSpriteHeight(), 0);

        test.IsBitmap = false;

        m_GoList.push(test);

        return test;
    }
}
