package com.hl.swimmyfishy;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import java.util.ArrayList;
import static com.hl.swimmyfishy.Fish.STATE.*;


public class Fish implements GameObject {

    enum STATE {

        ALIVE,
        SHIELD,
        DEAD
    }

    /* Variables for the initialisation of the object */
    private final int SCREEN_HEIGHT;
    private final int SCREEN_WIDTH;
    private final int spriteWidth;
    private final int spriteHeight;
    private final float SCALE;

    private static final int SPRITE_SHEET_ROWS = 1;
    private static final int SPRITE_SHEET_COLS = 2;
    /* ---------------------------------------------- */

    private static float VELOCITY;
    private static float GRAVITY;

    private STATE state;
    private float distance;
    private float angle;
    private Matrix matrix;

    private int x;
    private int y;

    private final AnimationManager animationManager;

    /* Hit-box plus hit-box colour */
    private Rectangle fishHitBox;
    private Paint paint;


    public Fish(Bitmap spriteSheet, int x, int y, int SCREEN_WIDTH, int SCREEN_HEIGHT) {

        this.state = ALIVE;

        /* Create new matrix that rotates fish when it is falling */
        matrix = new Matrix();

        /* Set up the sprite sheet for extracting animations */
        spriteWidth = spriteSheet.getWidth()/SPRITE_SHEET_COLS;
        spriteHeight = spriteSheet.getHeight()/SPRITE_SHEET_ROWS;
        this.SCREEN_HEIGHT = SCREEN_HEIGHT;
        this.SCREEN_WIDTH = SCREEN_WIDTH;
        this.x = x;
        this.y = y;

        /* Scale all values pertaining to fish movement */
        this.SCALE = SCREEN_HEIGHT/1920.f;
        GRAVITY = 0.5f * SCALE;
        VELOCITY = 0.1f * SCALE;

        /* Set up the hit box rectangle for player */
        fishHitBox = new Rectangle();
        fishHitBox.width = spriteWidth*17/20;
        fishHitBox.height = spriteHeight*17/20;

        /* Set up flap animation and pass into animation manager which will play the animation 5 times per second */
        Bitmap[] flapAnim = new Bitmap[SPRITE_SHEET_COLS];
        for (int i =0;i<SPRITE_SHEET_COLS;i++) {
            flapAnim[i] = Bitmap.createBitmap(spriteSheet, (i* spriteWidth), 0, spriteWidth, spriteHeight);
        }
        Animation flapAnimation = new Animation(flapAnim,0.2f);
        animationManager = new AnimationManager(flapAnimation);

//        /* Paint for rectangle drawn around fish for debugging */
//        paint = new Paint();
//        paint.setColor(Color.GREEN);
//        paint.setStrokeWidth(10);
//        paint.setStyle(Paint.Style.STROKE);
    }


    public void checkCollision(GameObject object, boolean isPowerUp){

        /* Update the game hit-box ONLY when this collision-checking method is called */
        fishHitBox.x = x;
        fishHitBox.y = y;

        /* An object may have multiple hit-boxes, such as a pair of pipes.
        Therefore we use an array list to get all the hit-boxes of one object */
        ArrayList<Rectangle> hitBox = object.getHitBox();
        for (Rectangle hb : hitBox) {
            if (fishHitBox.intersects(hb)){
                if (!isPowerUp) {
                    if (this.state == SHIELD){
                        this.state = ALIVE;
                        paint.setColor(Color.GREEN);
                        hb.setFirstIntersect(false);
                    }
                    else if(hb.firstIntersect()){
                        this.state = DEAD;
                    }
                }
                else{
                    /* TODO */
                    this.state = SHIELD;
                    paint.setColor(Color.WHITE);
                }
            }
        }
    }

    /* Method for rotating the fish character */
    private void rotate(float angle){

        angle = (angle>= 90) ? 90 : angle;

        this.matrix.reset();
        this.matrix.setRotate(angle,spriteWidth/2,spriteHeight/2);
        this.matrix.postTranslate(x,y);
    }

    /* Method for accelerating the fish upwards(jumping) */
    public void jump()  {

        VELOCITY = - 7.0f * SCALE;
    }

    /* A public method used by the game thread to reset the angle of the fish character on first draw of the game screen */
    public void resetAngle(){

        this.angle = 0;
        this.matrix.reset();
        this.matrix.setRotate(0,spriteWidth/2,spriteHeight/2);
        this.matrix.postTranslate(x,y);
    }

    public STATE getState() {

        return this.state;
    }

    public Rectangle getFishHitBox(){

        return this.fishHitBox;
    }

    @Override
    public void update() {

        /* Velocity increases with every update cycle -> v = u + at */
        VELOCITY += GRAVITY;

        /* Distance updated according to velocity */
        distance = VELOCITY * 3.0f;

        /* y coordinate of fish is updated every cycle.
        . The fish rises if velocity is a negative value,
        . The fish falls if velocity is a positive value.
         */
        this.y +=  (int) distance;

        /* Angle is based on velocity, multiplied by an arbitrary value that works */
        angle = VELOCITY * 5.0f;

        /* Keep fish at top of screen if there */
        if (this.y <= 0 )  {
            this.y = 0;
        }
        /* If fish touches the base -> game over */
        else if (this.y >= SCREEN_HEIGHT*5/6 - spriteHeight){
            this.y = (SCREEN_HEIGHT*5/6 - spriteHeight);
            this.state = DEAD;
        }

        /* Update the matrix which rotates the fish according to its current angle */
        this.rotate(angle);

        /* Update the animation manager, then move to the next frame of animation */
        animationManager.update();
        animationManager.playAction(0);

    }

    @Override
    public void draw(Canvas canvas){

        animationManager.draw(canvas,matrix);

        /* Draw the hit-box for debugging */
//        canvas.drawRect(this.x,this.y,this.x+spriteWidth,this.y+spriteHeight,paint);
    }

    @Override
    public int getX() {
        return this.x;
    }

    @Override
    public int getY(){
        return this.y;
    }

    public int getSpriteWidth(){
        return spriteWidth;
    }

    @Override
    public ArrayList<Rectangle> getHitBox(){

       return new ArrayList<>();
    }

    @Override
    public boolean offScreen(){
        return true;
    }

    @Override
    public boolean isPowerUp(){
        return false;
    }

}




