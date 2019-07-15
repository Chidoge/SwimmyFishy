package com.hl.swimmyfishy;

import android.graphics.Canvas;

import java.util.ArrayList;

public interface GameObject {

    public boolean offScreen();
    public boolean isPowerUp();
    public ArrayList<Rectangle> getHitBox();

    public void update();
    public void draw(Canvas canvas);

    public int getX();
    public int getY();

}
