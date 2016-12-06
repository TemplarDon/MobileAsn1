package com.sidm.mgp_lab02_153492y;

import android.content.Context;
import android.graphics.Bitmap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;

/**
 * Created by Donovan's PC on 5/12/2016.
 */

public class LevelLoader {

    Context m_Context;

    public int levelGridHeight = 12;
    public int levelGridWidth = 16;
    public int TileWidth;
    public int TileHeight;

    public LevelLoader(Context c)
    {
        m_Context = c;
    }

    public void Init(int ScreenWidth, int ScreenHeight)
    {
        TileWidth = ScreenWidth / levelGridWidth;
        TileHeight = ScreenHeight / levelGridHeight;
    }

    public Level LoadLevel(int lvlNum, boolean firstLevel)
    {
        InputStream inputStream = m_Context.getResources().openRawResource(R.raw.lvl1);
        BufferedReader bufferedReader= new BufferedReader(new InputStreamReader(inputStream));

        Level returnLvl;
        int offset = 0;
        if (firstLevel) {
            returnLvl = new Level(levelGridWidth, levelGridHeight, TileWidth * levelGridWidth);
            offset = 0;
        }
        else{
            returnLvl = new Level(levelGridWidth, levelGridHeight, 2 * TileWidth * levelGridWidth);
            offset = TileWidth * levelGridWidth;
        }


        try {
            String line ;
            int height = 0;

            //LinkedList<LinkedList<Integer>> m_ScreenMap = new LinkedList<LinkedList<Integer>>();

            GameObject temp;

            while((line = bufferedReader.readLine()) != null)
            {
                String[] parts = line.split(",");

                for (int width = 0; width < levelGridWidth; ++width)
                {
                    //int offset = GameObjectManager.getInstance().meshList.get("ground").getWidth() / 2;
                    Vector3 pos = new Vector3(offset + width * TileWidth, height * TileHeight, 0);

                    switch (Integer.parseInt(parts[width]))
                    {
                        case 1:
                            temp = GameObjectManager.getInstance().CreateGameObject(pos, GameObjectManager.getInstance().meshList.get("ground"), true);
                            temp.vel.x = -200;
                            returnLvl.m_CollisionGrid[height][width] = 1;
                            break;

                        case 2:

                            break;

                        case 3:
                            break;

                        case 4:
                            break;

                        default:
                            break;
                    }
                }
                ++height;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return returnLvl;
    }

}
