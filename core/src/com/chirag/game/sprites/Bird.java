package com.chirag.game.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by CHIRAG on 27-08-2016.
 */
public class Bird {
    private static final int GRAVITY = -15;
    public static final int MOVEMENT = 100;
    public int realGravity = GRAVITY, realMovement = MOVEMENT;
    private float rotation;
    private float realRotation;
    private Vector3 position;
    private Vector3 velocity;
    private Rectangle bounds;
    private Animation birdAnimation;
    private Texture texture, birdTexture;
    private Texture speedTexture, gravityTexture, boostTexture, smallTexture, largeTexture;
    private Sound flap;
    private boolean powerRotationEnable = false, powerSpeedEnable = false, powerGravityEnable = false;
    private boolean powerEnable = false;
    private boolean setPowerOneCounter = true;
    private boolean birdAnimationCounter = true;
    private boolean enableCollision = true;

    public Bird(int x, int y){
        position = new Vector3(x, y, 0);
        velocity = new Vector3(0, 0, 0);
        texture = new Texture("birdanimation.png");
        birdAnimation = new Animation(new TextureRegion(texture), 3, 0.5f);
        bounds = new Rectangle(x, y, texture.getWidth()/3, texture.getHeight());
        flap = Gdx.audio.newSound(Gdx.files.internal("Sounds/sfx_wing.ogg"));

        speedTexture = new Texture("birdanimation_speed.png");
        gravityTexture = new Texture("birdanimation_gravity.png");
        boostTexture = new Texture("birdanimation_boost2.png");
        smallTexture = new Texture("birdanimation_small.png");
        largeTexture = new Texture("birdanimation_large.png");
    }

    public Vector3 getPosition() {
        return position;
    }

    public float getRotation() { return rotation; }

    public TextureRegion getTexture() {
        return birdAnimation.getFrame();
    }

    public void update(float dt){

        birdAnimation.update(dt);
        if(!powerEnable) {
            if (position.y > 0) {
                velocity.add(0, GRAVITY, 0);
                    if (rotation >= -10)
                        rotation -= 0.5f;
                    else if (rotation >= -30)
                        rotation -= 5;
                    else if (rotation >= -75)
                        rotation -= 10;
                }

            velocity.scl(dt);
            position.add(MOVEMENT * dt, velocity.y, 0);

            if (position.y < 0)
                position.y = 0;


            velocity.scl(1/dt);
            bounds.setPosition(position.x, position.y);
        }

        else {
            if (position.y > 0) {
                velocity.add(0, realGravity, 0);
                if (powerRotationEnable)
                    rotation -= 50;
                else{
                    if(realRotation == 0)
                        rotation = 0;
                    else {
                        if (rotation >= -10)
                            rotation -= 0.5f;
                        else if (rotation >= -30)
                            rotation -= 5;
                        else if (rotation >= -75)
                            rotation -= 10;
                    }
                }
            }


            velocity.scl(dt);
            position.add(realMovement * dt, velocity.y, 0);

            if (position.y < 0)
                position.y = 0;

            velocity.scl(1 / dt);
            bounds.setPosition(position.x, position.y);
        }
    }

    public void jump(){
        if(!powerEnable) {
            velocity.y = 250;
            rotation = 0;
        }
        else if(powerRotationEnable){
            velocity.y = 180;
        }
        else{
            velocity.y = 180;
            rotation = 0;
        }
        velocity.y = 250;
        rotation = 0;
        flap.play(0.35f);
    }

    public void setPowerInBird(String powerValue){
        powerEnable = true;
        if(setPowerOneCounter) {
            if (powerValue == "rotate") {
                birdTexture = texture;
                this.powerRotationEnable = true;
                realRotation = rotation;
                realGravity += 4;
                realMovement += 20;
            }
            else if (powerValue == "speed") {
                birdTexture = speedTexture;
                this.powerSpeedEnable = true;
                realMovement = (int) (MOVEMENT*2);
            }
            else if (powerValue == "gravity") {
                birdTexture = gravityTexture;
                bounds = new Rectangle(getPosition().x, getPosition().y + (birdTexture.getHeight()/2), texture.getWidth()/3, texture.getHeight()/2);
                this.powerGravityEnable = true;
                realGravity = (int) (GRAVITY*1.65);
            }
            else if (powerValue == "boost_small") {
                birdTexture = boostTexture;
                this.powerSpeedEnable = true;
                realMovement = (int) (MOVEMENT*5);
                enableCollision = false;
                realRotation = 0;
                Library.rocketSound = true;
            }
            else if (powerValue == "boost_large") {
                birdTexture = boostTexture;
                this.powerSpeedEnable = true;
                realMovement = (int) (MOVEMENT*8);
                enableCollision = false;
                realRotation = 0;
                Library.rocketSound = true;
            }
            else if (powerValue == "small"){
                birdTexture = smallTexture;
                bounds = new Rectangle(getPosition().x, getPosition().y, birdTexture.getWidth()/3, birdTexture.getHeight());
            }
            else if (powerValue == "large"){
                birdTexture = largeTexture;
                bounds = new Rectangle(getPosition().x, getPosition().y, birdTexture.getWidth()/3, birdTexture.getHeight());
                realRotation = 0;
            }
            else if(powerValue == "plankBoost"){
                birdTexture = texture;
                this.powerSpeedEnable = true;
                realMovement = (int) (MOVEMENT*8);
                enableCollision = false;
                velocity.y = 450;
            }

            updateBirdAnimation();
            birdAnimationCounter = true;
        }
        setPowerOneCounter = false;
    }

    public void resetPower(Library library){
        this.setPowerOneCounter = true;

        texture = new Texture("birdanimation.png");
        this.powerEnable = false;
        this.powerRotationEnable = false;
        this.powerGravityEnable = false;
        this.powerSpeedEnable = false;
        this.enableCollision = true;
        realGravity = GRAVITY;
        realMovement = MOVEMENT;
        realRotation = rotation;

        if(birdAnimationCounter) {
            updateBirdAnimation();
            birdAnimationCounter = false;
            bounds = new Rectangle(bounds.getX(), bounds.getY(), texture.getWidth()/3, texture.getHeight());
        }

        library.resetSounds(library);
    }

    public void slowDown(){
        realMovement = (MOVEMENT/2) + (MOVEMENT/4);
    }

    private Texture setTexture(){
        if(powerEnable)
            try {
                return birdTexture;
            }
            catch (IllegalArgumentException e){
                return new Texture("birdanimation.png");
            }
        else
            return new Texture("birdanimation.png");
    }

    private void updateBirdAnimation(){
            birdAnimation = new Animation(new TextureRegion(setTexture()), 3, 0.5f);
          //  bounds = new Rectangle(getPosition().x, getPosition().y, texture.getWidth()/3, texture.getHeight());
    }

    public void powerResetCheck(MysteryBox mBox, int scoreValue, int powerCount, Library library, Plank plank){
        if(scoreValue >= powerCount + 5) {
            if(mBox.getPowerValue() != "boost_small" && mBox.getPowerValue() != "boost_large" && mBox.getPowerValue()!="plankBoost") {
                this.resetPower(library);
            }
            else if(mBox.getPower() == "boost_small")
            {
                if (scoreValue == powerCount + 9) {
                    this.slowDown();
                }
                if(scoreValue == powerCount + 10)
                    this.resetPower(library);
            }

            else if(mBox.getPowerValue() == "plankBoost"){
                plank.checkResetPlank(scoreValue, this, library);
            }

            else
            {
                if (scoreValue == powerCount + 24) {
                    this.slowDown();
                }
                if(scoreValue == powerCount + 25)
                    this.resetPower(library);
            }
        }
    }

    public Rectangle getBounds(){
        return bounds;
    }

    public boolean getEnableCollision(){
        return enableCollision;
    }

    public boolean getPowerEnable(){
        return powerEnable;
    }

    public void dispose(){
        texture.dispose();
        if(birdTexture != null)
            birdTexture.dispose();
        flap.dispose();
    }
}
