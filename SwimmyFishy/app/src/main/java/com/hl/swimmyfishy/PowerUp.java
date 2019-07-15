package com.hl.swimmyfishy;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.ArrayList;

public class PowerUp implements GameObject {

    private Bitmap powerUp;

    private int x;
    private int y;

    private float distance;

    private boolean goDown;
    private boolean drawPowerUp;

    private final int SCREEN_WIDTH;
    private final int spriteWidth;
    private final int spriteHeight;
    private static float VELOCITY;
    private static float GRAVITY;
    private final float SCALE;

    /* Hit-Box and Hit-Box Colour */
    private Paint paint;
    private Rectangle powerUpHitBox;
    private ArrayList<Rectangle> hitBoxes;

    public PowerUp(Bitmap image, int x, int y, int SCREEN_WIDTH, int SCREEN_HEIGHT){

        this.SCREEN_WIDTH = SCREEN_WIDTH;
        this.x = x;
        this.y = y;
        drawPowerUp = false;
        spriteWidth = image.getWidth();
        spriteHeight = image.getHeight();

        powerUp = Bitmap.createBitmap(image,0,0,spriteWidth,spriteHeight);

        hitBoxes = new ArrayList<>();

        /* Create a rectangle to compare for object collision */
        powerUpHitBox = new Rectangle();
        powerUpHitBox.width = spriteWidth*17/20;
        powerUpHitBox.height = spriteHeight*17/20;
        powerUpHitBox.x = x;
        powerUpHitBox.y = y;
        hitBoxes.add(powerUpHitBox);

        /* Scale all values pertaining to object movement */
        this.SCALE = SCREEN_HEIGHT/1920.f;
        GRAVITY = 0.05f * SCALE;
        VELOCITY = 0.f;

        /* Paint for rectangle drawn around object for debugging */
//        paint = new Paint();
//        paint.setColor(Color.WHITE);
//        paint.setStrokeWidth(10);
//        paint.setStyle(Paint.Style.STROKE);
    }



    @Override
    public void update(){

        if (drawPowerUp) {

            if (this.x <= -spriteWidth) {
                drawPowerUp = false;
            }
            else {
                this.x -= 7*SCALE;
                powerUpHitBox.x -= 7*SCALE;

                /* To simulate a bobbing motion, Velocity will be changed according to the current Velocity
                . Velocity is increased if it needs to fall
                . Velocity is decreased if it needs to rise
                */
                if (VELOCITY <= (-5.0f * SCALE)) {
                    goDown = true;
                } else if (VELOCITY >= (5.0f * SCALE)) {
                    goDown = false;
                }

                if (goDown) {
                    VELOCITY += GRAVITY;
                } else {
                    VELOCITY -= GRAVITY;
                }

                /* Distance updated according to velocity */
                distance = VELOCITY * 3.0f;

                /* y coordinate of the powerUp is updated every cycle if it needs to be drawn.
                . The object rises if velocity is a negative value,
                . The object falls if velocity is a positive value.
                */
                this.y += (int) distance;
                powerUpHitBox.y += (int) distance;

            }
        }

    }

    @Override
    public void draw(Canvas canvas){

        if (drawPowerUp) {
            /* Draw PowUpImage */
            canvas.drawBitmap(powerUp, this.x, this.y, null);

            /* Draw the hit-box for debugging */
//            canvas.drawRect(this.x, this.y,this.x+spriteWidth,this.y+spriteHeight,paint);
        }
    }

    public void spawnPowUp(int xPos) {
        drawPowerUp = true;

        this.x = xPos + SCREEN_WIDTH * 3 / 8;
        powerUpHitBox.x = xPos + SCREEN_WIDTH * 3 / 8;
    }

    @Override
    public boolean offScreen(){
        return false;
    }

    @Override
    public ArrayList<Rectangle> getHitBox(){

        return hitBoxes;
    }

    @Override
    public int getX(){
        return this.x;
    }

    @Override
    public int getY(){
        return this.y;
    }

    @Override
    public boolean isPowerUp(){
        return true;
    }
}
