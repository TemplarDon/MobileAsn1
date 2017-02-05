package com.sidm.mgp_lab02_153492y;

import android.graphics.Bitmap;

/**
 * Created by Donovan's PC on 4/12/2016.
 */

public class GameObject {

    void GameObject() {
    }

    public String name = "object";
    public Vector3 pos;
    public Vector3 scale;
    public Vector3 vel;
    public Boolean active;
    public Boolean gravityApply = false;

    //public Vector3 prevPos;
    public Boolean IsJumping = false;
    public Boolean IsFalling = true;
    public Boolean IsOnGround = true;
    public Boolean CanJump = true;
    public Boolean ShouldMove = true;

    // Spike Specific
    public Boolean KillPlayer = false;

    // Rope
    GameObject parentRope;

    // Platform
    public int MaxIncrease = 2; // How many tiles it can be raised
    public Vector3 startPos = new Vector3(0,0,0);
    public Boolean IsTouched = false; // If touched during MotionEvent.ACTION_DOWN
    public Boolean IsMoving = false; // If true, move till maxincrease
    public GameObject LeftObject, RightObject;

    // Object either has sprite animation or normal texture
    public Bitmap texture;
    public SpriteAnimation spriteAnimation;
    public Boolean IsBitmap;

    private float MAX_FALL_SPEED = 500.f;

    public int rock_health = 3;

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

    public void Update(float dt, Vector3 grav) {

        // Set Prev pos
        Vector3 prevPos = new Vector3(pos.x ,pos.y, pos.z);

        // Update vel based on grav
        if (gravityApply) {
            vel.x += grav.x;
            vel.y += grav.y;

            if (vel.y > MAX_FALL_SPEED)
                vel.y = MAX_FALL_SPEED;
        }

        // Update Pos based on vel
        pos.x += vel.x * dt;
        pos.y += vel.y * dt;

        // Platform specific movement
        if (IsMoving)
        {
            pos.y -= grav.y * 4 * dt;

            if (pos.y < startPos.y - (MaxIncrease * texture.getHeight()))
            {
                IsMoving = false;
            }
        }

        // Get Jumping/Falling boolean
        if (pos.y > prevPos.y) {
            IsFalling = true;
            IsJumping = false;
            //IsOnGround = false;
        } else if (pos.y < prevPos.y){
            IsJumping = true;
            IsFalling = false;
            Jump(dt);
        }
        else
        {
            //IsOnGround = true;
            IsJumping = false;
            IsFalling = false;
        }

    }

    public void UpdateEffect(float dt, Vector3 grav) {

        // Set Prev pos
        Vector3 prevPos = new Vector3(pos.x ,pos.y, pos.z);

        // Update vel based on grav
        if (gravityApply) {
            vel.y += grav.y;
        }

        // Update Pos based on vel
        pos.y += vel.y * dt;

        // Platform specific movement
        if (IsMoving)
        {
            pos.y -= grav.y * 4 * dt;

            if (pos.y < startPos.y - (MaxIncrease * texture.getHeight()))
            {
                IsMoving = false;
            }
        }

    }

    public void Jump(float dt){

            //vel.y = vel.y + (float)(15f) * dt;
            //pos.y = pos.y + vel.y * dt;
    }

/*    // Overloaded for player
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
    }*/
}
