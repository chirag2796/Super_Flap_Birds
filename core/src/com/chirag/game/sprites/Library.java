package com.chirag.game.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.chirag.game.AdHandler;
import com.chirag.game.states.State;

import java.util.Random;

/**
 * Created by CHIRAG on 28-09-2016.
 */
public class Library {
   public static boolean rocketSound = false;
    private static boolean soundCounter = true;
    private Sound rocket, highscore;
    private Random rn;
    private Animation highscoreAnimation;
    private Texture highscoreTexture;
    private boolean showAdCounter = true, loadAdCounter=true;
    private int lives = 0;

    public Library(){
        rocket = Gdx.audio.newSound(Gdx.files.internal("Sounds/rocket.ogg"));
        highscore = Gdx.audio.newSound(Gdx.files.internal("Sounds/highscore.ogg"));
        rn = new Random();
        highscoreTexture = new Texture("highscore_label.png");
        highscoreAnimation = new Animation(new TextureRegion(highscoreTexture), 3, 0.5f);
    }

    public void update(Library library, float dt){
        if(rocketSound)
            if(soundCounter) {
                soundCounter = false;
                library.rocket.loop(0.4f);
            }
        highscoreAnimation.update(dt);
    }

    public static void resetSounds(Library library){
        rocketSound = false;
        soundCounter = true;
        library.rocket.stop();
    }

    public void highscoreSound(){
            highscore.play(0.85f);
    }

    public int getRandom(int min, int max){
        int n = rn.nextInt(max - min + 1) + min;
        return n;
    }

    public TextureRegion getHighscoreTexture(){
        return highscoreAnimation.getFrame();
    }

    public Texture getBgTexture(){
        Texture bg;
        int n = getRandom(1, 9);
        switch (n){
            case 1: bg = new Texture("backgrounds/bg_sky_1.png");
                break;
            case 2: bg = new Texture("backgrounds/bg_sky_2.png");
                break;
            case 3: bg = new Texture("backgrounds/bg_forest_1.png");
                break;
            case 4: bg = new Texture("backgrounds/bg_forest_2.png");
                break;
            case 5: bg = new Texture("backgrounds/bg_forest_3.png");
                break;
            case 6: bg = new Texture("backgrounds/bg_castle_1.png");
                break;
            case 7: bg = new Texture("backgrounds/bg_castle_2.png");
                break;
            case 8: bg = new Texture("backgrounds/bg_castle_3.png");
                break;
            case 9: bg = new Texture("backgrounds/bg_castle_4.png");
                break;
            default:
                bg = new Texture("backgrounds/bg_sky_1.png");
        }
        return bg;
    }

    public void loadInterstitialAd(AdHandler handler){
        if(State.gameCounter == 10 && showAdCounter){
            handler.showInterstitial();
            showAdCounter = false;
            loadAdCounter = true;
            State.gameCounter = 0;
        }
        if((State.gameCounter == 1) && loadAdCounter) {
            loadAdCounter = false;
            showAdCounter = true;
            handler.loadInterstitial();
        }
    }

    public boolean isShowAdCounter() {
        return showAdCounter;
    }

    public Texture getMedalTexture(int scoreValue){
        if(scoreValue == 0)
            return new Texture("Graphics/Medals/paper.png");
        else if(scoreValue >= 1 && scoreValue < 5)
            return new Texture("Graphics/Medals/wood.png");
        else if(scoreValue >= 5 && scoreValue <11)
            return new Texture("Graphics/Medals/apple.png");
        else if(scoreValue >= 11 && scoreValue <17)
            return new Texture("Graphics/Medals/iron.png");
        else if(scoreValue >= 17 && scoreValue <24)
            return new Texture("Graphics/Medals/bronze.png");
        else if(scoreValue >= 24 && scoreValue <34)
            return new Texture("Graphics/Medals/pearl.png");
        else if(scoreValue >= 34 && scoreValue <46)
            return new Texture("Graphics/Medals/silver.png");
        else if(scoreValue >= 46 && scoreValue <70)
            return new Texture("Graphics/Medals/ruby.png");
        else if(scoreValue >= 70 && scoreValue <100)
            return new Texture("Graphics/Medals/gold.png");
        else if(scoreValue >= 100)
            return new Texture("Graphics/Medals/diamond.png");
        else
            return new Texture("Graphics/Medals/paper.png");
    }


    public void dispose(){
        rocket.dispose();
        highscore.dispose();
        highscoreTexture.dispose();
    }

    public int getLives() {
        return lives;
    }

    public void setLives(int lives) {
        this.lives = lives;
    }
}
