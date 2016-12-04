package com.sidm.mgp_lab02_153492y;

import android.graphics.Bitmap;

/**
 * Created by Donovan's PC on 4/12/2016.
 */

public class GameObject {

    void GameObject() {

    }

    public Vector3 pos;
    public Boolean active;

    // Object either has sprite animation or normal texture
    public Bitmap texture;
    public SpriteAnimation spriteAnimation;
    public Boolean IsBitmap;

    public Vector3 GetScale()
    {
        Vector3 returnVec;

        if(IsBitmap)
        {
            returnVec = new Vector3(texture.getWidth(), texture.getHeight(), 0);
        }
        else
        {
            returnVec = new Vector3(spriteAnimation.getSpriteWidth(), spriteAnimation.getSpriteHeight(), 0);
        }

        return returnVec;
    }
}
