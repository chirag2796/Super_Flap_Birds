package com.chirag.game.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Random;

/**
 * Created by CHIRAG on 06-09-2016.
 */
public class MysteryBox {

    private Animation mysteryBoxAnimation;
    private Texture texture;
    private Vector3 position;
    public Rectangle boundsMysteryBox;
    private int powerIndex;
    public static boolean setPowerIndex = true;
    private boolean Lazarus, countLazarus = true;
    private String powerValue;
    private Sound powerUp;

    private Dictionary<Integer, String>powers = new Hashtable<Integer, String>();

    public MysteryBox(){
        position = new Vector3(0, 0, 0);
        if(Math.random()<0.5) {
            texture = new Texture("mystery_box_2s.png");
            mysteryBoxAnimation = new Animation(new TextureRegion(texture), 2, 0.5f);
            powerUp = Gdx.audio.newSound(Gdx.files.internal("Sounds/powerUp.ogg"));
            Lazarus = false;
        }
        else{
            texture = new Texture("mystery_box_2s.png");
            mysteryBoxAnimation = new Animation(new TextureRegion(texture), 2, 0.5f);
            powerUp = Gdx.audio.newSound(Gdx.files.internal("Sounds/powerUp.ogg"));
            Lazarus = true;
        }
    }

    public void checkLazarus(){
        if(Math.random()<0.5) {
            texture = new Texture("mystery_box_2s.png");
            mysteryBoxAnimation = new Animation(new TextureRegion(texture), 2, 0.5f);
            powerUp = Gdx.audio.newSound(Gdx.files.internal("Sounds/powerUp.ogg"));
            Lazarus = false;
        }
        else{
            texture = new Texture("mystery_box_2s.png");
            mysteryBoxAnimation = new Animation(new TextureRegion(texture), 2, 0.5f);
            powerUp = Gdx.audio.newSound(Gdx.files.internal("Sounds/powerUp.ogg"));
            Lazarus = true;
        }
    }

    public Vector3 getPosition() {
        return position;
    }
    public Rectangle getBounds(){
        return boundsMysteryBox;
    }
    public TextureRegion getTexture() {
        return mysteryBoxAnimation.getFrame();
    }

    public void setPosition(float x, float y){
        this.position.x = x;
        this.position.y = y;
        boundsMysteryBox = new Rectangle(position.x, position.y, texture.getWidth()/2, texture.getHeight());
    }

    public void update(float dt){
        mysteryBoxAnimation.update(dt);
        System.out.println(Lazarus);
    }
    public void dispose(){
        texture.dispose();
        powerUp.dispose();
    }

    public boolean collides(Rectangle player){
        return player.overlaps(boundsMysteryBox);
    }

    public void initializePowers(){
        powers.put(1, "rotate");
        powers.put(2, "gravity");
        powers.put(3, "speed");
        powers.put(4, "boost_small");
        powers.put(5, "boost_large");
        powers.put(6, "small");
        powers.put(7, "large");
    }

    public void setPowerIndex(Library library){
        if(Lazarus) {
            if(countLazarus) {
                library.setLives(library.getLives() + 1);
                countLazarus = false;
            }
        }
        else {
            Random rn = new Random();
            //powerIndex = rn.nextInt(7) + 1;
            powerIndex = 5;
            setPowerIndex = false;
            countLazarus = true;
        }
    }

    public String getPower(){
        return powers.get(powerIndex);
    }

    public void setPower(Bird bird){
        if(!Lazarus) {
            powerValue = this.getPower();
            bird.setPowerInBird(powerValue);
            powerUp.play(1.0f);
        }
    }

    public String getPowerValue(){
        return powerValue;
    }

    public void setPowerValue(String string){
        this.powerValue = string;
    }

}
