package com.sidm.mgp_lab02_153492y;

/**
 * Created by Donovan's PC on 6/12/2016.
 */

public class Level {

    int[][] m_ScreenMap = new int[0][0];
    int m_LevelLength; // The right most point on the x-axis
    boolean m_OnScreen;

    Level(int numGrids_Width, int numGrids_Height, int Length)
    {
        m_ScreenMap = new int[numGrids_Height][numGrids_Width];
        m_LevelLength = Length;
        m_OnScreen = true;
    }

    void Update(float dt, int moveRate)
    {
        m_LevelLength -= 1200 * dt;

        if (m_LevelLength < -m_LevelLength)
            m_OnScreen = false;
    }
}
