package com.hl.swimmyfishy;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class GameActivity extends Activity {

    private static final String PREFS_NAME = "Settings";
    private static final int PLAY_AGAIN = 1 ;
    private GameView gameView;
    private int characterType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        /* Remove status,title bar and lock screen to portrait */
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        /* Load the character choice into the Game object, then pass into the game view to use */
        SharedPreferences settings = this.getApplicationContext().getSharedPreferences(PREFS_NAME,MODE_PRIVATE);
        characterType = settings.getInt("character",R.drawable.catfish);

        /* Create game object to pass into the game view, which will load the settings */
        Game game = new Game(this);
        game.setCharacterType(characterType);

        gameView = new GameView(this,game);
        setContentView(gameView);

    }

    public void gameOver(){

        Intent i = new Intent(GameActivity.this,GameOverActivity.class);
        startActivityForResult(i,PLAY_AGAIN);

    }

    @Override
    protected void onPause() {
        super.onPause();
        gameView.stopThread();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        gameView.stopThread();
    }

    @Override
    protected void onActivityResult(int requestCode , int resultCode, Intent data){
        /* Use this to skip this activity and escape to main menu if the button was pressed */
        if (requestCode == PLAY_AGAIN){
            if (resultCode == RESULT_OK) {
                boolean playAgain =  getIntent().getBooleanExtra("playAgain",true);
                if (playAgain){
                     /* Create a new game and send it to the view */
                    Game game = new Game(this);
                    game.setCharacterType(characterType);

                    gameView.setGame(game);
                    gameView.restartThread();
                }
            }
        }
        super.onActivityResult(requestCode,resultCode,data);
    }

}
