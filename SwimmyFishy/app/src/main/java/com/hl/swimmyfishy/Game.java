package com.hl.swimmyfishy;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

import java.util.ArrayList;

import static com.hl.swimmyfishy.Fish.STATE.*;

public class Game {

    private Context context;

    /* Variables relating to game objects */
    private int characterType;
    private Fish fish;
    private PowerUp powUp;
    private ArrayList<Pipe> pipes;
    private ArrayList<GameObject> objects;
    private int level;

    public Game(Context context) {

        this.context = context;
        this.pipes = new ArrayList<>();
        this.objects = new ArrayList<>();
        this.level = 0;

    }

    public void createGame(int SCREEN_WIDTH, int SCREEN_HEIGHT, double physicalScreenWidth, double physicalScreenHeight){

        /* Create the player character */
        Bitmap fishBitMap = BitmapFactory.decodeResource(context.getResources(),characterType);
        fishBitMap = Bitmap.createScaledBitmap(fishBitMap, (int)(fishBitMap.getWidth() * physicalScreenWidth/2.8312587 * 3/4),(int)(fishBitMap.getHeight() * physicalScreenHeight/5.033349 *3/4),false);
        fish =  new Fish(fishBitMap,SCREEN_WIDTH/3,SCREEN_HEIGHT/3,SCREEN_WIDTH,SCREEN_HEIGHT);


        int fishWidth = fish.getSpriteWidth();
        /* Create the pipes */
        Bitmap pipeBitMap = BitmapFactory.decodeResource(context.getResources(),R.drawable.pipes);
        pipeBitMap = Bitmap.createScaledBitmap(pipeBitMap, SCREEN_WIDTH*5/12,SCREEN_HEIGHT,false);
        for (int i =0 ;i <2;i++){
            Pipe p  = new Pipe(pipeBitMap,i*SCREEN_WIDTH*3/4,0,SCREEN_WIDTH,SCREEN_HEIGHT);
            p.setFishWidth(fishWidth);
            this.objects.add(p);
            pipes.add(p);
        }

        /* Create the Power Up */
        Bitmap powUpImage = BitmapFactory.decodeResource(context.getResources(),R.drawable.power_up);
        powUpImage = Bitmap.createScaledBitmap(powUpImage, (int)(powUpImage.getWidth() * physicalScreenWidth/2.8312587 * 3/4),(int)(powUpImage.getHeight() * physicalScreenHeight/5.033349 *3/4),false);
        powUp = new PowerUp(powUpImage,(SCREEN_WIDTH + 500),(SCREEN_HEIGHT*3/4),SCREEN_WIDTH,SCREEN_HEIGHT);
        this.objects.add(powUp);

    }

    public void updateGameState(){

        fish.update();
        powUp.update();

        pipes.get(0).update(pipes.get(1).getX()-10);
        pipes.get(1).update(pipes.get(0).getX());

        for (Pipe p: pipes){
            if (p.offScreen()){
                if ((level%2 == 0) && (level != 0)){
                    powUp.spawnPowUp(p.getX());
                }
            }
        }
        updateLevel();

    }

    public void drawObjects(Canvas canvas) {

        for (GameObject object : objects){
            object.draw(canvas);
        }
        fish.draw(canvas);
    }

    public void checkCollisions(){

        for (GameObject object : objects){
            if (!object.offScreen()){
                fish.checkCollision(object, object.isPowerUp());
            }
        }

    }

    public void updateLevel(){

        for (Pipe pipe : pipes){
            if (!pipe.offScreen()) {
                if (pipe.getPassRect().firstIntersect()) {
                    if (fish.getFishHitBox().intersects(pipe.getPassRect())) {
                        pipe.getPassRect().setFirstIntersect(false);
                        level++;
                    }
                }
            }

        }
    }

    public void setCharacterType(int character) {

        this.characterType = character;
    }

    public boolean gameOver(){

        return (this.getFish().getState() == DEAD);
    }

    public Fish getFish(){

        return this.fish;
    }

    public int getScore(){
        return level;
    }

    public int getPrevHighScore(){

        SharedPreferences settings = context.getSharedPreferences("Settings",Context.MODE_PRIVATE);
        String currentScoreString = settings.getString("highScore","0");
        while (!(Character.isDigit(currentScoreString.charAt(currentScoreString.length()-1)))){
            currentScoreString = currentScoreString.substring(0,currentScoreString.length()-1);
        }

        return Integer.parseInt(currentScoreString);

    }

    public void setHighScore(int hs){

        String highScore = Integer.toString(hs);
        while (highScore.length() < 5){
            highScore = highScore + "x";
        }
        SharedPreferences settings = context.getSharedPreferences("Settings",Context.MODE_PRIVATE);
        settings.edit().putString("highScore",highScore).apply();

    }

    public void setCurrentScore(int score){

        String currentScore = Integer.toString(score);
        while (currentScore.length() < 5){
            currentScore = currentScore + "x";
        }
        SharedPreferences settings = context.getSharedPreferences("Settings",Context.MODE_PRIVATE);
        settings.edit().putString("currentScore",currentScore).apply();
    }

    public void setDisplayNewHighScore(boolean newHighScore){

        SharedPreferences settings = context.getSharedPreferences("Settings",Context.MODE_PRIVATE);
        settings.edit().putBoolean("newHighScore",newHighScore).apply();

    }


}
