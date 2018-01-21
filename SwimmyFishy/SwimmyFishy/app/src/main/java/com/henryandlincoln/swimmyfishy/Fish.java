package com.henryandlincoln.swimmyfishy;

import android.graphics.Bitmap;
import android.graphics.Canvas;


public class Fish extends GameObject {

    private static final int ROW_UP_ACTION = 0;
    private static final int ROW_DOWN_ACTION = 1;
    private static final int ROW_RECOVER_ACTION = 2;
    private static final int ROW_FALL_ACTION = 3;


    private int row;
    private int col;

    public static float VELOCITY;
    public static float GRAVITY;
    private float lastDrawNanoTime;
    private boolean falling;
    private long now;
    private float distance;
    private int timeDifference;

    private Bitmap[] notFlapping;
    private Bitmap[] flapping;
    private Bitmap[] fall;
    private Bitmap[] recover;

    private GameView gameView;

    public Fish(GameView gameView, Bitmap image, int x, int y,int SCREEN_HEIGHT) {

        super(image, 4, 4, x, y,SCREEN_HEIGHT);

        /* Initialise the values */
        this.row = ROW_UP_ACTION;
        this.col = 0;
        falling = false;
        lastDrawNanoTime = -1 ;
        VELOCITY =  0.1f;
        GRAVITY = 0.1f;
        this.gameView= gameView;

        /* Create bitmap arrays for different actions */
        this.notFlapping = new Bitmap[colCount];
        this.flapping = new Bitmap[colCount];
        this.recover = new Bitmap[colCount];
        this.fall = new Bitmap[colCount];


        /* Populate the bitmap arrays */
        for (int col =0;col<this.colCount;col++){
            this.notFlapping[col] =  this.createSubImageAt(ROW_DOWN_ACTION,col);
            this.flapping[col] = this.createSubImageAt(ROW_UP_ACTION,col);
            this.fall[col] = this.createSubImageAt(ROW_FALL_ACTION,col);
            this.recover[col] = this.createSubImageAt(ROW_RECOVER_ACTION,col);
        }
    }

    public Bitmap[] getMoveBitmaps() {
        switch (row)  {
            case ROW_DOWN_ACTION:
                return  this.notFlapping;
            case ROW_UP_ACTION:
                return this.flapping;
            case ROW_FALL_ACTION:
                return this.fall;
            case ROW_RECOVER_ACTION:
                return this.recover;
            default:
                return null;
        }
    }

    public Bitmap getCurrentMoveBitmap() {

        if (this.row == ROW_FALL_ACTION){
            this.col = 0;
        }
        return this.getMoveBitmaps()[this.col];
    }


    public void update() {

        this.col++;

        if(col >= this.colCount)  {
            this.col = 0;
        }

        now = System.nanoTime();
        if(lastDrawNanoTime==-1) {
            lastDrawNanoTime= now;
        }
        timeDifference = (int) ((now - lastDrawNanoTime)/ 1000000 );

        VELOCITY += GRAVITY;
        distance = VELOCITY * timeDifference;


        this.y = this.y + (int) distance;

        falling = (VELOCITY >= 0);


        /* Keep object at edge of screen if there */
        if (this.y <= 0 )  {
            this.y = 0;
        }
        else if (this.y >= SCREEN_HEIGHT - height - 190){
            this.y = SCREEN_HEIGHT - height - 190;
            VELOCITY = 0.f;
            GRAVITY = 0.f;
        }
        else {
            GRAVITY =  0.1f;
        }


        if (!falling) {
            this.row = ROW_UP_ACTION;
        }
        else if (falling && ((this.row == ROW_DOWN_ACTION) || (this.row == ROW_FALL_ACTION))) {
            this.row = ROW_FALL_ACTION;
        }
        else if (falling && (this.row == ROW_UP_ACTION)){
            this.row = ROW_DOWN_ACTION;
        }
        else if (!falling  && (this.row == ROW_FALL_ACTION)){
            this.row = ROW_RECOVER_ACTION;
        }
        else {
            this.row = ROW_UP_ACTION;
        }


    }

    public void draw(Canvas canvas)  {
        canvas.drawBitmap(this.getCurrentMoveBitmap(),x, y, null);
        // Last draw time.
        this.lastDrawNanoTime= System.nanoTime();
    }

    public void jump()  {

       VELOCITY = - 2.0f;
    }
}




