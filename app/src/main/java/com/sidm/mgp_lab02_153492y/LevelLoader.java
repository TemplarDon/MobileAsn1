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

    int levelGridHeight = 12;
    int levelGridWidth = 16;
    int TileWidth;
    int TileHeight;

    public LevelLoader(Context c)
    {
        m_Context = c;
    }

    public void Init(int ScreenWidth, int ScreenHeight)
    {
        TileWidth = ScreenWidth / levelGridWidth;
        TileHeight = ScreenHeight / levelGridHeight;
    }

    public void LoadLevel(int lvlNum)
    {
        InputStream inputStream = m_Context.getResources().openRawResource(R.raw.lvl1);
        BufferedReader bufferedReader= new BufferedReader(new InputStreamReader(inputStream));

        try {
            String line ;
            int height = 0;

            //LinkedList<LinkedList<Integer>> m_ScreenMap = new LinkedList<LinkedList<Integer>>();

            while((line = bufferedReader.readLine()) != null)
            {
                String[] parts = line.split(",");

                for (int width = 0; width < levelGridWidth; ++width)
                {
                    Vector3 pos = new Vector3(width * TileWidth, height * TileHeight, 0);

                    switch (Integer.parseInt(parts[width]))
                    {
                        case 1:
                            GameObjectManager.getInstance().CreateGameObject(pos, GameObjectManager.getInstance().meshList.get("ground"), true);
                            break;

                        case 2:
                            GameObjectManager.getInstance().CreateGameObject(pos, GameObjectManager.getInstance().meshList.get("pallet"), true);
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

    }

}
