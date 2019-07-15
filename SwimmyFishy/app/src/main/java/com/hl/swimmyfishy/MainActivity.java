package com.hl.swimmyfishy;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;


public class MainActivity extends Activity {

    private static final String PREFS_NAME = "Settings";
    private long lastClickTime = 0;
    private String highScore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        /* Remove status,title bar and lock screen to portrait */
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);

        /* Read and load settings using SharedPreferences */
        loadSettings();
        configurePlayButton();
        configureCharacterSelButton();
        configureHighScore();

    }

    private void loadSettings() {

        SharedPreferences settings = this.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        highScore = settings.getString("highScore", "0xxxx");
    }

    private void configurePlayButton() {

        Button playBtn = (Button) findViewById(R.id.play);
        final MediaPlayer soundEffectButton = MediaPlayer.create(this, R.raw.bubble_pop_3);

        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /* Conditional to prevent double click */
                if (SystemClock.elapsedRealtime() - lastClickTime < 1000) {
                    return;
                }

                lastClickTime = SystemClock.elapsedRealtime();

                soundEffectButton.start();

                /* Start the game */
                Intent i = new Intent(MainActivity.this, GameActivity.class);
                startActivity(i);
            }
        });
    }

    private void configureCharacterSelButton() {

        Button selBtn = (Button) findViewById(R.id.characterSelect);

        final MediaPlayer soundEffectButton = MediaPlayer.create(this, R.raw.bubble_pop_2);

        selBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /* Conditional to prevent double click */
                if (SystemClock.elapsedRealtime() - lastClickTime < 1000) {
                    return;
                }

                lastClickTime = SystemClock.elapsedRealtime();

                soundEffectButton.start();
                /* Open the character select screen */
                Intent i = new Intent(MainActivity.this, CharacterSelectActivity.class);
                startActivity(i);

            }
        });

    }

    private void configureHighScore() {

        ImageView highScoreTxt = (ImageView) findViewById(R.id.high_score);
        highScoreTxt.setImageResource(R.drawable.high_score);

        ImageView digit = (ImageView) findViewById(R.id.digit1);
        digit.setImageResource(getDigit(highScore.charAt(0)));


        digit = (ImageView) findViewById(R.id.digit2);
        digit.setImageResource(getDigit(highScore.charAt(1)));

        digit = (ImageView) findViewById(R.id.digit3);
        digit.setImageResource(getDigit(highScore.charAt(2)));

        digit = (ImageView) findViewById(R.id.digit4);
        digit.setImageResource(getDigit(highScore.charAt(3)));

        digit = (ImageView) findViewById(R.id.digit5);
        digit.setImageResource(getDigit(highScore.charAt(4)));

    }

    private int getDigit(char digit) {

        switch (digit) {
            case '0':
                return R.drawable.zero;
            case '1':
                return R.drawable.one;
            case '2':
                return R.drawable.two;
            case '3':
                return R.drawable.three;
            case '4':
                return R.drawable.four;
            case '5':
                return R.drawable.five;
            case '6':
                return R.drawable.six;
            case '7':
                return R.drawable.seven;
            case '8':
                return R.drawable.eight;
            case '9':
                return R.drawable.nine;
            default:
                return R.drawable.empty_number;
        }
    }

    @Override
    protected void onResume() {

        super.onResume();

        /* Load high score when activity resumes */
        highScore = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getString("highScore", "0xxxx");
        configureHighScore();

    }


}
