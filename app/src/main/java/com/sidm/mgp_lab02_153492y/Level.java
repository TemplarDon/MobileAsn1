package com.sidm.mgp_lab02_153492y;

/**
 * Created by Donovan's PC on 6/12/2016.
 */

public class Level {

    int[][] m_CollisionGrid = new int[0][0];
    int m_LevelLength; // The right most point on the x-axis
    boolean m_OnScreen;

    Level(int numGrids_Width, int numGrids_Height, int Length)
    {
        m_CollisionGrid = new int[numGrids_Height][numGrids_Width];
        m_LevelLength = Length;
        m_OnScreen = true;
    }

    void Update(float dt, int moveRate)
    {
        m_LevelLength -= moveRate * dt;

        if (m_LevelLength < 0)
            m_OnScreen = false;
    }
}
