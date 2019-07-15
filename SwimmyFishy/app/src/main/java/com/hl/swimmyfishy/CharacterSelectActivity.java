package com.hl.swimmyfishy;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

public class CharacterSelectActivity extends Activity {

    public static final String PREFS_NAME = "Settings";
    private SharedPreferences settings;
    private boolean dogUnlocked;
    private int currentCharacter;

    private int windowWidth;
    private int windowHeight;

    private Drawable dogButtonImage;
    private Drawable catButtonImage;
    private Button catBtn;
    private Button dogBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        /* Remove status,title bar and lock screen to portrait */
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_character_select);


        /* Create settings to load character choice into */
        settings = this.getSharedPreferences(PREFS_NAME,MODE_PRIVATE);
        dogUnlocked = settings.getBoolean("dogUnlocked",true);
        currentCharacter = settings.getInt("character",R.drawable.catfish);

        /* The images for the buttons are dependent on the window size, use these variables to scale the images */
        windowHeight = this.getWindow().getDecorView().getHeight();
        windowWidth = this.getWindow().getDecorView().getWidth();

        setCurrentCharacter(currentCharacter);

        catBtn = (Button) findViewById(R.id.catButton);
        dogBtn = (Button) findViewById(R.id.dogButton);

        configureCatButton();
        configureDogButton();
    }


    public void configureCatButton(){

        setCurrentCharacter(currentCharacter);

        catBtn.setBackground(catButtonImage);

        catBtn.setHeight((int)(windowHeight*0.3));
        catBtn.setWidth((int)(windowWidth*0.6));
        catBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                settings.edit().putInt("character",R.drawable.catfish).apply();
                setCurrentCharacter(R.drawable.catfish);
                catBtn.setBackground(catButtonImage);
                dogBtn.setBackground(dogButtonImage);
            }
        });
    }

    public void configureDogButton(){

        setCurrentCharacter(currentCharacter);
        dogBtn.setBackground(dogButtonImage);

        dogBtn.setHeight((int)(windowHeight*0.3));
        dogBtn.setWidth((int)(windowWidth*0.6));

        dogBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if (dogUnlocked) {
                    settings.edit().putInt("character", R.drawable.dogfish).apply();
                    setCurrentCharacter(R.drawable.dogfish);
                    dogBtn.setBackground(dogButtonImage);
                    catBtn.setBackground(catButtonImage);
                }
            }
        });
    }


    private void setCurrentCharacter(int id){

        if (id == R.drawable.catfish){

            currentCharacter = R.drawable.catfish;
            if (dogUnlocked) {
                dogButtonImage = getResources().getDrawable(R.drawable.dogfish_unlocked);
            }
            else {
                dogButtonImage = getResources().getDrawable(R.drawable.dogfish_locked);
            }
            catButtonImage = getResources().getDrawable(R.drawable.catfish_button_selected);
        }

        else {
            currentCharacter = R.drawable.dogfish;
            dogButtonImage = getResources().getDrawable(R.drawable.dogfish_button_selected);
            catButtonImage = getResources().getDrawable(R.drawable.catfish_button);

        }
    }


}
