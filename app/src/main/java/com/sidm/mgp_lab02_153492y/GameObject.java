package com.sidm.mgp_lab02_153492y;

import android.graphics.Bitmap;

/**
 * Created by Donovan's PC on 4/12/2016.
 */

public class GameObject {

    void GameObject() {

    }

    public String name;
    public Vector3 pos;
    public Vector3 scale;
    public Vector3 vel;
    public Boolean active;
    public Boolean gravityApply = false;

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

    public void Update(float dt, Vector3 grav)
    {
        // Update vel based on grav
        if (gravityApply) {
            vel.x += grav.x;
            vel.y += grav.y;
        }

        // Update Pos based on vel
        pos.x += vel.x * dt;
        pos.y += vel.y * dt;
    }

    // Overloaded for player
    public void Update(float dt, Vector3 grav, boolean vertical, boolean horizontal)
    {
        // Update vel based on grav
        if (gravityApply && vertical) {
            vel.x += grav.x;
            vel.y += grav.y;
        }

        // Update Pos based on vel
        if (horizontal)
            pos.x += vel.x * dt;

        if (vertical)
            pos.y += vel.y * dt;
    }
}
