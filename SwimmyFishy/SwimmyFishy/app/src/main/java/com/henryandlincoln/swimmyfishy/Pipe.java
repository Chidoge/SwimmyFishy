package com.henryandlincoln.swimmyfishy;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.ArrayList;
import java.util.Random;

public class Pipe implements GameObject {

    private Bitmap upPipe;
    private Bitmap downPipe;

    private int upPipeY;
    private int downPipeY;
    private final int spriteWidth;
    private final int spriteHeight;
    private final int SCREEN_WIDTH;

    private int x;

    private final float SCALE;

    private boolean drawPipe;
    private boolean initialDraw;

    private Rectangle upRect;
    private Rectangle downRect;
    private Rectangle passRect;

    private Random rand;

    private final int UP_LIMIT;
    private final int DOWN_LIMIT;

    private ArrayList<Rectangle> hitBoxes;

    private Paint paint;
    private Paint paint2;

    public Pipe(Bitmap image, int x, int y, int SCREEN_WIDTH,int SCREEN_HEIGHT){



        this.SCREEN_WIDTH = SCREEN_WIDTH;
        this.x = x;
        drawPipe = false;
        initialDraw = true;
        spriteWidth = image.getWidth()/2;
        spriteHeight = image.getHeight();

        /* Create random number generator */
        rand = new Random();

        /* Create rectangles to compare for object collision */
        upRect = new Rectangle();
        downRect = new Rectangle();
        passRect  = new Rectangle();

        hitBoxes = new ArrayList<>(2);
        hitBoxes.add(upRect);
        hitBoxes.add(downRect);
        //hitBoxes.add(passRect);

        upRect.width = spriteWidth;
        upRect.height = spriteHeight;
        downRect.width = spriteWidth;
        downRect.height = spriteHeight;
        passRect.width = spriteWidth;
        passRect.height = spriteHeight/5;


        /* The pipe moving in the x axis will have its speed adjusted according to the screen width */
        this.SCALE = SCREEN_WIDTH/1080.f;

        /* Create an image each of the pipe facing up and facing down, from the original sprite sheet */
        upPipe = Bitmap.createBitmap(image,0*image.getWidth()/2,0,spriteWidth,spriteHeight);
        downPipe = Bitmap.createBitmap(image,image.getWidth()/2,0,spriteWidth,spriteHeight);

        /* Set the limits of how high the pipes will be */
        DOWN_LIMIT = SCREEN_HEIGHT /4;
        UP_LIMIT = SCREEN_HEIGHT * 3/5;

        /* Create paint for debugging (for object collision algorithm) */
        paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStrokeWidth(10);
        paint.setStyle(Paint.Style.STROKE);
        paint2 = new Paint();
        paint2.setColor(Color.YELLOW);
        paint2.setStrokeWidth(10);
        paint2.setStyle(Paint.Style.STROKE);
    }

    public void update(int xPos){

        /* Pipe moves at a velocity that is relative to the screen width */
        this.x -= 7*SCALE;

        /* Sets up the pipe to be drawn with random heights */
        if (drawPipe) {
            if (initialDraw){
                upPipeY = rand.nextInt(UP_LIMIT) + DOWN_LIMIT;
                downPipeY = (int)(upPipeY -  spriteHeight* 1.2);
                initialDraw = false;
            }
        }

        /* Resets the pipe to be drawn starting off screen on the right side */
        if (this.x <= - spriteWidth){
            this.x = xPos + SCREEN_WIDTH * 3/4;
            drawPipe = true;
            initialDraw = true;
            passRect.setFirstIntersect(true);
        }
    }


    @Override
    public void draw(Canvas canvas){

        /* Conditional used to delay pipes for first few seconds of the game */
        if (drawPipe){
            /* Draw both pipes */
            canvas.drawBitmap(downPipe,x, downPipeY, null);
            canvas.drawBitmap(upPipe,x,upPipeY,null);

            /* Draw hit box for debugging */
            if (this.x >= 0 && this.x <= SCREEN_WIDTH) {
                upRect.x = this.x;
                upRect.y = upPipeY;
                downRect.x = this.x;
                downRect.y = downPipeY;
                passRect.x = this.x;
                passRect.y = downPipeY  + spriteHeight;
                canvas.drawRect(downRect.x, downRect.y, downRect.x + downRect.width, downRect.y + downRect.height, paint);
                canvas.drawRect(upRect.x, upRect.y, upRect.x + upRect.width, upRect.y + upRect.height, paint);
                canvas.drawRect(passRect.x, passRect.y,passRect.x + passRect.width, passRect.y + passRect.height,paint2);

            }
        }
    }

    @Override
    public ArrayList<Rectangle> getHitBox(){

        upRect.x = this.x;
        upRect.y = upPipeY;
        downRect.x = this.x;
        downRect.y = downPipeY;
        passRect.x = this.x;
        passRect.y = downPipeY + spriteHeight;

        hitBoxes.set(0,upRect);
        hitBoxes.set(1,downRect);

        return hitBoxes;
    }

    public Rectangle getPassRect(){

        return passRect;
    }


    @Override
    public int getX(){
        return this.x;
    }

    @Override
    public int getY(){
        return this.upPipeY;
    }

    @Override
    public boolean offScreen(){
        return this.initialDraw;
    }

    @Override
    public void update(){

    }



}
