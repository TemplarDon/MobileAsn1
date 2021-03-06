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
        meshList.put("ground", Bitmap.createScaledBitmap(BitmapFactory.decodeResource(c.getResources(), R.drawable.test), ScreenWidth / 16, ScreenHeight / 12, true));
        meshList.put("pallet", Bitmap.createScaledBitmap(BitmapFactory.decodeResource(c.getResources(), R.drawable.box), ScreenWidth / 16, ScreenHeight / 12, true));
        meshList.put("rope", Bitmap.createScaledBitmap(BitmapFactory.decodeResource(c.getResources(), R.drawable.rope), ScreenWidth / 16, ScreenHeight / 4, true));
        meshList.put("spike", Bitmap.createScaledBitmap(BitmapFactory.decodeResource(c.getResources(), R.drawable.spike), ScreenWidth / 16, ScreenHeight / 14, true));
        meshList.put("sidespike", Bitmap.createScaledBitmap(BitmapFactory.decodeResource(c.getResources(), R.drawable.sidespike), ScreenWidth / 16, ScreenHeight / 14, true));
        meshList.put("platform", Bitmap.createScaledBitmap(BitmapFactory.decodeResource(c.getResources(), R.drawable.platform), ScreenWidth / 16, ScreenHeight / 12, true));
        meshList.put("rock", Bitmap.createScaledBitmap(BitmapFactory.decodeResource(c.getResources(), R.drawable.rock2), ScreenWidth / 22, ScreenHeight / 14, true));
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
