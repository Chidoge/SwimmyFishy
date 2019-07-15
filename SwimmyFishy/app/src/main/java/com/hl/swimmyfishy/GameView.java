package com.hl.swimmyfishy;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {


    private GameThread gameThread;
    private Game game;

    private Bitmap bg_base;
    private Paint paint;

    private int SCREEN_HEIGHT;
    private int SCREEN_WIDTH;
    private boolean firstTouch;

    private Paint textPaint;


    public GameView(Context context,Game game) {

        super(context);

        /* Make it so that this view can handle events */
        this.setFocusable(true);
        this.getHolder().addCallback(this);

        /* Create thread to manage updates and drawing to the canvas */
        this.gameThread = new GameThread(this,this.getHolder());

        /* Save the game state to load it in this game view */
        this.game = game;

        /* Set this variable to stall game thread and only start after the first touch */
        firstTouch = true;

        /* Temporary real time score counter */
        textPaint = new Paint();
        textPaint.setColor(Color.RED);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setTextSize(100);



    }

    public void update()  {

        if (!game.gameOver()){
            game.updateGameState();
        }
        else {
            this.endGame();
        }

    }


    public void endGame(){

        /* Configure the new score and set high score if previous one was beaten
        Load these scores into settings for the game over panel to display */

        int currentScore = game.getScore();
        int bestScore = game.getPrevHighScore();
        if (bestScore < currentScore) {
            game.setHighScore(currentScore);
        }
        game.setCurrentScore(currentScore);
        game.setDisplayNewHighScore(bestScore<currentScore);
        ((GameActivity)(this.getContext())).gameOver();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (firstTouch){
                firstTouch = false;
                this.gameThread.setRunning(true);
                this.gameThread.start();
                game.getFish().jump();
                return true;
            }
            else {
                game.getFish().jump();
                return true;
            }
        }
        return false;
    }

    @Override
    public void draw(Canvas canvas)  {

        super.draw(canvas);

        canvas.drawPaint(paint);

        game.drawObjects(canvas);
        game.checkCollisions();

        canvas.drawBitmap(bg_base,0,SCREEN_HEIGHT*5/6,null);
        canvas.drawText("" + game.getScore(), 100, 100, textPaint);

    }

    private void setBackgrounds(){

        paint = new Paint();
        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.FILL);
        bg_base = BitmapFactory.decodeResource(this.getResources(),R.drawable.bg_base);
        bg_base = Bitmap.createScaledBitmap(bg_base,SCREEN_WIDTH,SCREEN_HEIGHT/6,false);
    }

    public Fish getCharacter(){

        return game.getFish();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        /* Set up the resolution values */
        this.SCREEN_HEIGHT = this.getHeight();
        this.SCREEN_WIDTH = this.getWidth();

        float aspectRatio = (float)SCREEN_HEIGHT / SCREEN_WIDTH;
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity)getContext()).getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width=dm.widthPixels;
        int height=dm.heightPixels;
        double wi=(double)width/(double)dm.xdpi;
        double hi=(double)height/(double)dm.ydpi;
        double x = Math.pow(wi,2);
        double y = Math.pow(hi,2);
        double screenInches = Math.sqrt(x+y);
        double physicalScreenWidth = Math.sqrt(Math.pow(screenInches,2)/((Math.pow(aspectRatio,2)+ 1)));
        double physicalScreenHeight = physicalScreenWidth * aspectRatio;
        setBackgrounds();
        game.createGame(SCREEN_WIDTH,SCREEN_HEIGHT,physicalScreenWidth,physicalScreenHeight);
        gameThread.firstDraw();

    }


    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

       stopThread();
    }

    public void setGame(Game game){

        this.game = game;
    }

    public void restartThread(){

        /* Create thread to manage updates and drawing to the canvas */
        this.gameThread = new GameThread(this,this.getHolder());

        float aspectRatio = (float)SCREEN_HEIGHT / SCREEN_WIDTH;
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity)getContext()).getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width=dm.widthPixels;
        int height=dm.heightPixels;
        double wi=(double)width/(double)dm.xdpi;
        double hi=(double)height/(double)dm.ydpi;
        double x = Math.pow(wi,2);
        double y = Math.pow(hi,2);
        double screenInches = Math.sqrt(x+y);
        double physicalScreenWidth = Math.sqrt(Math.pow(screenInches,2)/((Math.pow(aspectRatio,2)+ 1)));
        double physicalScreenHeight = physicalScreenWidth * aspectRatio;

        firstTouch = true;

        setBackgrounds();
        game.createGame(SCREEN_WIDTH,SCREEN_HEIGHT,physicalScreenWidth,physicalScreenHeight);
        gameThread.firstDraw();

    }


    public void stopThread(){

        if (this.gameThread.isAlive()) {

            this.gameThread.setRunning(false);
            this.gameThread.interrupt();
        }
    }


}