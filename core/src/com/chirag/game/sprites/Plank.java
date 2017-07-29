package com.chirag.game.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

/**
 * Created by CHIRAG on 03-10-2016.
 */
public class Plank {
    private Texture plankTexture;
    private Animation plankAnimation;
    private Rectangle bounds;
    private float plankX;
    private int loadPlankCounter;
    private boolean plankLoadEnable = true, animationEnable = false, soundEnable = true;
    private float updateX;
    private Sound springSound;
    private int plankGlitchPatch = -7;

    public Plank(){
        plankTexture = new Texture("Graphics/plankAnimation_small.png");
        plankAnimation = new Animation(new TextureRegion(plankTexture), 3, 0.4f);
        springSound = Gdx.audio.newSound(Gdx.files.internal("Sounds/Spring_Boing.ogg"));
    }

    public float getPlankX(){
        return (plankX + 180.0f);
    }

    public void setPLank(Bird bird, Texture ground, int offset){
        plankX = bird.getPosition().x;
        bounds = new Rectangle(bird.getPosition().x+180.0f, ground.getHeight() + offset, plankTexture.getWidth()/3, plankTexture.getHeight());
    }

    public TextureRegion getTexture() {
        return plankAnimation.getFrame();
    }

    public void update(float dt, Bird bird, MysteryBox mBox,Library library, int scoreValue){
        if(collides(bird.getBounds())) {
            animationEnable = true;
            mBox.setPowerValue("plankBoost");
            bird.setPowerInBird("plankBoost");
            updateX = bird.getPosition().x;
            if(soundEnable) {
                springSound.play(1.0f);
                soundEnable = false;

                plankGlitchPatch = scoreValue;
            }
        }
        else if(bird.getPosition().x > updateX+50)
            animationEnable = false;

        if(animationEnable)
            plankAnimation.update(dt);

        if(scoreValue == (plankGlitchPatch + 7) && bird.getEnableCollision() == false) {
            bird.slowDown();
        }

        if(scoreValue == (plankGlitchPatch + 8) && bird.getEnableCollision() == false) {
            bird.resetPower(library);
        }
    }

    public boolean loadPlank(int scoreValue, MysteryBox mBox){

        if(!plankLoadEnable && mBox.getPowerValue()!= "plankBoost" && scoreValue>(loadPlankCounter+3))
            plankLoadEnable = true;

        if(scoreValue!=0 && (scoreValue > (loadPlankCounter + 3)) && plankLoadEnable){
            if(Math.random()>0.6) {
                loadPlankCounter = scoreValue;
                plankLoadEnable = false;
                return true;
            }
            else
                loadPlankCounter = scoreValue;
        }
        else if(scoreValue == 0 && plankLoadEnable && loadPlankCounter==0){
            if(Math.random()<0.45){
                plankLoadEnable = false;
            }
            loadPlankCounter = 1;
            return true;
        }
        return false;
    }

    public void checkResetPlank(int scoreValue, Bird bird, Library library) {
        if (!plankLoadEnable) {
            if (scoreValue == loadPlankCounter + 5)
                bird.slowDown();

            else if (scoreValue == loadPlankCounter + 6) {
                plankLoadEnable = true;
                animationEnable = false;
                soundEnable = true;
                bird.resetPower(library);
            }
        }
    }

    public boolean collides(Rectangle player){
        return player.overlaps(bounds);
    }

    public boolean getPlankLoadEnabled(){
        return !plankLoadEnable;
    }

    public void dispose(){
        plankTexture.dispose();
    }
}
