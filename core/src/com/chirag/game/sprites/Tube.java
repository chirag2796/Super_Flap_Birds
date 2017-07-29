package com.chirag.game.sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import java.util.Random;

/**
 * Created by CHIRAG on 27-08-2016.
 */
public class Tube {
    private static final int FLUCTUATION = 130;
    private static final int TUBE_GAP = 100;
    private static final int LOWEST_OPENING = 120;
    public static final int TUBE_WIDTH = 100;
    public static int count = 0;

    private Texture topTube, bottomTube;
    private Vector2 posTopTube, posBotTube;
    private Rectangle boundsTop, boundsTopTip, boundsBot, boundsBotTip, boundsGap;
    private Random rand;
    private boolean topTubeSet = Math.random()<0.5;
    private boolean bottomTubeset = Math.random()<0.5;

    public Tube(float x) {
        if(topTubeSet)
            topTube = new Texture("toptube.png");
        else
            topTube = new Texture("toptube2.png");

        if(bottomTubeset)
            bottomTube = new Texture("bottomtube.png");
        else
            bottomTube = new Texture("bottomtube2.png");

        rand = new Random();
        count = 0;

        posTopTube = new Vector2(x, rand.nextInt(FLUCTUATION) + TUBE_GAP + LOWEST_OPENING);
        posBotTube = new Vector2(x, posTopTube.y - TUBE_GAP - bottomTube.getHeight());

        //boundsTop = new Rectangle(posTopTube.x+14, posTopTube.y, topTube.getWidth()-15, topTube.getHeight());
        //boundsBot = new Rectangle(posBotTube.x+14, posBotTube.y, bottomTube.getWidth()-15, bottomTube.getHeight());
        boundsTop = new Rectangle(posTopTube.x+14, posTopTube.y + (topTube.getHeight()/17), topTube.getWidth()/2-15, topTube.getHeight());
        boundsBot = new Rectangle(posBotTube.x+14, posBotTube.y, bottomTube.getWidth()/4-15, bottomTube.getHeight()-(bottomTube.getHeight()/17));
        boundsTopTip = new Rectangle(posTopTube.x+(bottomTube.getWidth()/4)+14, posTopTube.y+7, topTube.getWidth()/8, topTube.getHeight()/17);
        boundsBotTip = new Rectangle(posBotTube.x+(bottomTube.getWidth()/4)+14, posBotTube.y+bottomTube.getHeight()-(bottomTube.getHeight()/17), bottomTube.getWidth()/8, bottomTube.getHeight()/17);
        boundsGap = new Rectangle(posBotTube.x+18, posBotTube.y, bottomTube.getWidth()-15, posTopTube.x + topTube.getHeight());
    }

    public void reposition(float x){
        posTopTube.set(x, rand.nextInt(FLUCTUATION) + TUBE_GAP + LOWEST_OPENING);
        posBotTube.set(x, posTopTube.y - TUBE_GAP - bottomTube.getHeight());
        boundsTop.setPosition(posTopTube.x+14, posTopTube.y);
        boundsBot.setPosition(posBotTube.x+14, posBotTube.y);
        boundsGap.setPosition(posBotTube.x+18, posBotTube.y);

        boundsTopTip.setPosition(posTopTube.x+(bottomTube.getWidth()/4)+14, posTopTube.y+7);
        boundsBotTip.setPosition(posBotTube.x+(bottomTube.getWidth()/4)+14, posBotTube.y+bottomTube.getHeight()-(bottomTube.getHeight()/17));

        count=0;
    }

    public boolean collides(Rectangle player){
        return player.overlaps(boundsTop) || player.overlaps(boundsBot) || player.overlaps(boundsBotTip) || player.overlaps(boundsTopTip);
    }

    public boolean passes(Rectangle player){
        if(count==0){
            if(player.overlaps(boundsGap)) {
                count++;
                return true;
            }
        }
        return false;
    }

    public void dispose(){
        topTube.dispose();
        bottomTube.dispose();
    }

    public Texture getTopTube() {
        return topTube;
    }

    public Texture getBottomTube() {
        return bottomTube;
    }

    public Vector2 getPosTopTube() {
        return posTopTube;
    }

    public Vector2 getPosBotTube() {
        return posBotTube;
    }

}
