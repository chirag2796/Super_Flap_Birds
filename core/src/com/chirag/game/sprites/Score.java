package com.chirag.game.sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

/**
 * Created by CHIRAG on 28-08-2016.
 */
public class Score {
    private Array<Texture> numbers;
    private static final int frameCount = 10;


    public Score(){
        numbers = new Array<Texture>();
        for(int i=0; i<frameCount; i++){
            if(i==0)
                numbers.add(new Texture("Graphics/Numbers/zero.png"));
            else if(i==1)
                numbers.add(new Texture("Graphics/Numbers/one.png"));
            else if(i==2)
                numbers.add(new Texture("Graphics/Numbers/two.png"));
            else if(i==3)
                numbers.add(new Texture("Graphics/Numbers/three.png"));
            else if(i==4)
                numbers.add(new Texture("Graphics/Numbers/four.png"));
            else if(i==5)
                numbers.add(new Texture("Graphics/Numbers/five.png"));
            else if(i==6)
                numbers.add(new Texture("Graphics/Numbers/six.png"));
            else if(i==7)
                numbers.add(new Texture("Graphics/Numbers/seven.png"));
            else if(i==8)
                numbers.add(new Texture("Graphics/Numbers/eight.png"));
            else if(i==9)
                numbers.add(new Texture("Graphics/Numbers/nine.png"));
        }
    }


    public Texture getScoreOne(int scoreValue){
        String s = Integer.toString(scoreValue);
        scoreValue = (s.charAt(s.length()-1)) - '0';
        return numbers.get(scoreValue);
    }
    public Texture getScoreTen(int scoreValue){
        String s = Integer.toString(scoreValue);
        scoreValue = (s.charAt(s.length()-2)) - '0';
        return numbers.get(scoreValue);
    }
    public Texture getScoreHundered(int scoreValue){
        String s = Integer.toString(scoreValue);
        scoreValue = (s.charAt(s.length()-3)) - '0';
        return numbers.get(scoreValue);
    }
    public Texture getScoreThousand(int scoreValue){
        String s = Integer.toString(scoreValue);
        scoreValue = (s.charAt(s.length()-4)) - '0';
        return numbers.get(scoreValue);
    }
    public Texture getScoreTenThousand(int scoreValue){
        String s = Integer.toString(scoreValue);
        scoreValue = (s.charAt(s.length()-5)) - '0';
        return numbers.get(scoreValue);
    }

}
