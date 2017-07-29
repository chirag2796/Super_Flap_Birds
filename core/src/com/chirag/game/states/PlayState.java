package com.chirag.game.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.chirag.game.AdHandler;
import com.chirag.game.Elements.Hud;
import com.chirag.game.FlappyDemo;
import com.chirag.game.sprites.Bird;
import com.chirag.game.sprites.Library;
import com.chirag.game.sprites.MysteryBox;
import com.chirag.game.sprites.Plank;
import com.chirag.game.sprites.Score;
import com.chirag.game.sprites.Tube;

import java.util.Random;

/**
 * Created by CHIRAG on 27-08-2016.
 */
public class PlayState extends State{

    private static final int TUBE_SPACING = 90;
    private static final int TUBE_COUNT = 4;
    private static final int GROUND_Y_OFFSET = -30;

    private Bird bird;
    private MysteryBox mBox;
    private Library library;
    private Plank plank;
    private Hud hud;
    private Texture bg;
    private Texture ground;
    private Texture birdDead, birdDeadStain;
    private Texture gameOverScreen;
    private Vector2 groundPos1, groundPos2;
    private Score score;
    private int scoreValue = 0;
    private int mysteryBoxScoreTemp = 0;
    private int powerCount = 0;
    private Sound point, stab, groundHit;
    private boolean gameOver = false;
    private boolean birdGround = false;
    private boolean stabSet = Math.random()<0.5;
    private boolean mBoxSet = false;
    private boolean mBRandomCheck = true;
    private boolean renderMysteryBox = true;
    private boolean highscoreCounter = true, highScoreValueCounter = true;
    private int birdCount = 0;
    private int highScore, highScorePrev;
    private float x, y;
    private Random rn = new Random();
    private int bloodStainRotation;
    private Preferences prefs;
    private Array<Tube> tubes;

    private AdHandler handler;

    public PlayState(GameStateManager gsm, AdHandler handler) {
        super(gsm);

        this.handler = handler;
        handler.showAds(false);

        bird = new Bird(50, 300);
        hud = new Hud();
        mBox = new MysteryBox();
        mBox.initializePowers();
        library = new Library();
        plank = new Plank();

        cam.setToOrtho(false, FlappyDemo.WIDTH / 2, FlappyDemo.HEIGHT / 2);
        bg = library.getBgTexture();
        ground = new Texture("ground.png");
        birdDead = new Texture("bird_dead2.png");
        birdDeadStain = new Texture("bloodStain_1.png");
        gameOverScreen = new Texture("gameOverScreen_hs.png");

        groundPos1 = new Vector2(cam.position.x - cam.viewportWidth / 2, GROUND_Y_OFFSET);
        groundPos2 = new Vector2((cam.position.x - cam.viewportWidth / 2) + ground.getWidth(), GROUND_Y_OFFSET);

        tubes = new Array<Tube>();
        score = new Score();

        point = Gdx.audio.newSound(Gdx.files.internal("Sounds/sfx_point.mp3"));
        groundHit = Gdx.audio.newSound(Gdx.files.internal("Sounds/groundHit.ogg"));
        if(stabSet)
            stab = Gdx.audio.newSound(Gdx.files.internal("Sounds/stab1.ogg"));
        else
            stab = Gdx.audio.newSound(Gdx.files.internal("Sounds/stab2.ogg"));

        for(int i=1; i<=TUBE_COUNT; i++){
            tubes.add(new Tube(i*(TUBE_SPACING + Tube.TUBE_WIDTH)));
        }

        bloodStainRotation = library.getRandom(0, 360);

        prefs = Gdx.app.getPreferences("chirag.flap.HighScore");
      //  highScore = prefs.getInteger("score");
    }

    @Override
    protected void handleInput() {
        if(!gameOver) {
            if (Gdx.input.justTouched())
                bird.jump();
        }
    }

    @Override
    public void update(float dt) {

        library.loadInterstitialAd(handler);
        if(!library.isShowAdCounter())
            gameOver = true;

        handleInput();

        if (Gdx.input.isKeyPressed(Input.Keys.BACK)){
            FlappyDemo.toggleBack = false;
            gsm.set(new MenuState(gsm, handler));
        }

        updateGround();
        bird.update(dt);
        library.update(library, dt);

        if(plank.loadPlank(scoreValue, mBox)) {
            plank.setPLank(bird, ground, GROUND_Y_OFFSET - 35);
        }

        if(!gameOver) {
            cam.position.x = bird.getPosition().x + 80;

            for (int i = 0; i < tubes.size; i++) {
                Tube tube = tubes.get(i);
                if (cam.position.x - (cam.viewportWidth / 2) > tube.getPosTopTube().x + tube.getTopTube().getWidth()) {
                    tube.reposition(tube.getPosTopTube().x + ((Tube.TUBE_WIDTH + TUBE_SPACING) * TUBE_COUNT));
                }
                if (tube.collides(bird.getBounds())  && bird.getEnableCollision()) {
                    Gdx.input.vibrate(200);
                    // gsm.set(new PlayState(gsm));
                    stab.play(0.35f);
                    gameOver = true;
                }
                if (tube.passes(bird.getBounds())) {
                    scoreValue++;
                    point.play(0.2f);
                }
            }

            if (bird.getPosition().y <= ground.getHeight() - 23 + GROUND_Y_OFFSET) {
                birdGround = true;
                gameOver = true;
                Gdx.input.vibrate(200);
                groundHit.play(1.0f);
            }

            int n = rn.nextInt(6) + 2;
            if(scoreValue % n == 0){
                if(Math.random()<0.7 && mBRandomCheck == true) {
                    int n1 = rn.nextInt(30) - 14;
                    if(n1==0)
                        n1=1;
                    mBox.setPosition(bird.getPosition().x + (4 * cam.viewportWidth) / 2 - mBox.getTexture().getRegionWidth() / 2, cam.viewportHeight / 2 - mBox.getTexture().getRegionHeight() / 2 + cam.viewportHeight/n1);
                    mBoxSet = true;
                    renderMysteryBox = true;
                    mysteryBoxScoreTemp = scoreValue;
                }
                mBRandomCheck = false;
            }
            else if(scoreValue  == mysteryBoxScoreTemp + 3) {
                mBox.checkLazarus();
                mysteryBoxScoreTemp = scoreValue;
                mBoxSet = false;
                mBRandomCheck = true;
            }

            if(mBoxSet) {
                if (mBox.collides(bird.getBounds())) {
                    renderMysteryBox = false;
                    if(!bird.getPowerEnable()) {
                        powerCount = scoreValue;
                        mBox.setPowerIndex(library);
                        mBox.setPower(bird);
                    }
                }
            }

            bird.powerResetCheck(mBox, scoreValue, powerCount, library, plank);

            if(highscoreCounter) {
                if (scoreValue > 2 && scoreValue == prefs.getInteger("score") + 1) {
                    library.highscoreSound();
                    highscoreCounter = false;
                }
            }

            if(gameOver)
                library.resetSounds(library);

            plank.update(dt, bird, mBox, library ,scoreValue);

            mBox.update(dt);
            cam.update();
        }
    }

    @Override
    public void render(SpriteBatch sb) {
            sb.setProjectionMatrix(cam.combined);
            sb.begin();

            sb.draw(bg, cam.position.x - (cam.viewportWidth / 2), 0);
            for (Tube tube : tubes) {
                sb.draw(tube.getTopTube(), tube.getPosTopTube().x, tube.getPosTopTube().y);
                sb.draw(tube.getBottomTube(), tube.getPosBotTube().x, tube.getPosBotTube().y);
            }

            sb.draw(ground, groundPos1.x, groundPos1.y);
            sb.draw(ground, groundPos2.x, groundPos2.y);

            if(scoreValue<10)
                sb.draw(score.getScoreOne(scoreValue), cam.position.x - score.getScoreOne(scoreValue).getWidth()/4, cam.viewportHeight / 1.15f, score.getScoreOne(scoreValue).getWidth() / 2, score.getScoreOne(scoreValue).getHeight() / 2);
            else if(scoreValue>=10 && scoreValue <100){
                sb.draw(score.getScoreTen(scoreValue), cam.position.x - score.getScoreTen(scoreValue).getWidth()/2, cam.viewportHeight / 1.15f, score.getScoreTen(scoreValue).getWidth() / 2, score.getScoreTen(scoreValue).getHeight() / 2);
                sb.draw(score.getScoreOne(scoreValue), cam.position.x, cam.viewportHeight / 1.15f, score.getScoreOne(scoreValue).getWidth() / 2, score.getScoreOne(scoreValue).getHeight() / 2);
            }
            else if(scoreValue>=100 && scoreValue <1000){
                sb.draw(score.getScoreHundered(scoreValue), cam.position.x - (3*score.getScoreHundered(scoreValue).getWidth())/4, cam.viewportHeight / 1.15f, score.getScoreHundered(scoreValue).getWidth() / 2, score.getScoreHundered(scoreValue).getHeight() / 2);
                sb.draw(score.getScoreTen(scoreValue), cam.position.x - score.getScoreTen(scoreValue).getWidth()/4, cam.viewportHeight / 1.15f, score.getScoreTen(scoreValue).getWidth() / 2, score.getScoreTen(scoreValue).getHeight() / 2);
                sb.draw(score.getScoreOne(scoreValue), cam.position.x + score.getScoreOne(scoreValue).getWidth()/4, cam.viewportHeight / 1.15f, score.getScoreOne(scoreValue).getWidth() / 2, score.getScoreOne(scoreValue).getHeight() / 2);
            }
            else if(scoreValue>=1000 && scoreValue <10000){
                sb.draw(score.getScoreThousand(scoreValue), cam.position.x -(score.getScoreThousand(scoreValue).getWidth()/2+score.getScoreHundered(scoreValue).getWidth()/2),cam.viewportHeight / 1.15f, score.getScoreThousand(scoreValue).getWidth()/2, score.getScoreThousand(scoreValue).getHeight()/2);
                sb.draw(score.getScoreHundered(scoreValue), cam.position.x - (score.getScoreHundered(scoreValue).getWidth())/2, cam.viewportHeight / 1.15f, score.getScoreHundered(scoreValue).getWidth() / 2, score.getScoreHundered(scoreValue).getHeight() / 2);
                sb.draw(score.getScoreTen(scoreValue), cam.position.x, cam.viewportHeight / 1.15f, score.getScoreTen(scoreValue).getWidth() / 2, score.getScoreTen(scoreValue).getHeight() / 2);
                sb.draw(score.getScoreOne(scoreValue), cam.position.x + score.getScoreTen(scoreValue).getWidth()/2, cam.viewportHeight / 1.15f, score.getScoreOne(scoreValue).getWidth() / 2, score.getScoreOne(scoreValue).getHeight() / 2);
            }
            else if(scoreValue>=10000 && scoreValue <100000){
                sb.draw(score.getScoreTenThousand(scoreValue), cam.position.x - (score.getScoreTenThousand(scoreValue).getWidth()/2+score.getScoreThousand(scoreValue).getWidth()/2 + score.getScoreHundered(scoreValue).getWidth()/4),cam.viewportHeight / 1.15f, score.getScoreTenThousand(scoreValue).getWidth()/2, score.getScoreTenThousand(scoreValue).getHeight()/2);
                sb.draw(score.getScoreThousand(scoreValue), cam.position.x -(score.getScoreThousand(scoreValue).getWidth()/2+score.getScoreHundered(scoreValue).getWidth()/4),cam.viewportHeight / 1.15f, score.getScoreThousand(scoreValue).getWidth()/2, score.getScoreThousand(scoreValue).getHeight()/2);
                sb.draw(score.getScoreHundered(scoreValue), cam.position.x - (score.getScoreHundered(scoreValue).getWidth())/4, cam.viewportHeight / 1.15f, score.getScoreHundered(scoreValue).getWidth() / 2, score.getScoreHundered(scoreValue).getHeight() / 2);
                sb.draw(score.getScoreTen(scoreValue), cam.position.x + score.getScoreHundered(scoreValue).getWidth()/4, cam.viewportHeight / 1.15f, score.getScoreTen(scoreValue).getWidth() / 2, score.getScoreTen(scoreValue).getHeight() / 2);
                sb.draw(score.getScoreOne(scoreValue), cam.position.x + score.getScoreHundered(scoreValue).getWidth()/4 + score.getScoreTen(scoreValue).getWidth()/2, cam.viewportHeight / 1.15f, score.getScoreOne(scoreValue).getWidth() / 2, score.getScoreOne(scoreValue).getHeight() / 2);
            }

            if(plank.getPlankLoadEnabled()) {
                sb.draw(plank.getTexture(), plank.getPlankX(), ground.getHeight() - 35 + GROUND_Y_OFFSET);
            }

            if(!gameOver) {
                sb.draw(bird.getTexture(), bird.getPosition().x, bird.getPosition().y, bird.getTexture().getRegionWidth() / 2, bird.getTexture().getRegionHeight() / 2, bird.getTexture().getRegionWidth(), bird.getTexture().getRegionHeight(), 1.0f, 1.0f, bird.getRotation());
                if(mBoxSet && renderMysteryBox) {
                    sb.draw(mBox.getTexture(), mBox.getPosition().x, mBox.getPosition().y, mBox.getTexture().getRegionWidth(), mBox.getTexture().getRegionHeight());
                }

            }

            else{

                handler.showAds(true);

                if(highScoreValueCounter) {
                    highScorePrev = prefs.getInteger("score");
                    highScoreValueCounter = false;
                }

                if(scoreValue > highScorePrev) {
                    sb.draw(library.getHighscoreTexture(), cam.position.x - cam.viewportWidth / 2 + 15, cam.viewportHeight / 1.24f, 0, 0, library.getHighscoreTexture().getRegionWidth(), library.getHighscoreTexture().getRegionHeight(), 0.42f, 0.42f, 0);
                    sb.draw(library.getHighscoreTexture(), cam.position.x - cam.viewportWidth / 2 + 15, cam.viewportHeight / 1.24f - gameOverScreen.getHeight()/1.55f, 0, 0, library.getHighscoreTexture().getRegionWidth(), library.getHighscoreTexture().getRegionHeight(), 0.42f, 0.42f, 0);
                }
                if(birdCount==0) {
                    x = bird.getPosition().x;
                    y = bird.getPosition().y;
                    bird.dispose();
                    highScore = prefs.getInteger("score");
                    if(highScore<scoreValue) {
                        prefs.putInteger("score", scoreValue);
                        prefs.flush();
                        highScore = scoreValue;
                    }
                    prefs.putFloat("birdDeathX", x);
                    prefs.putFloat("birdDeathY", y);
                    prefs.flush();
                    birdCount++;
                }

                if(birdGround) {
                    sb.draw(birdDead, x, y - 3, birdDead.getWidth() / 2, birdDead.getHeight() / 2, birdDead.getWidth(), birdDead.getHeight(), 1.0f, 1.0f, -90, 0, 0, birdDead.getWidth(), birdDead.getHeight(), false, false);
                }
                 else
                    sb.draw(birdDead, x, y);

                sb.draw(gameOverScreen, cam.position.x - gameOverScreen.getWidth()/4, cam.viewportHeight/2, gameOverScreen.getWidth()/2, gameOverScreen.getHeight()/2);
                sb.draw(library.getMedalTexture(scoreValue), cam.position.x + gameOverScreen.getWidth()/5 - library.getMedalTexture(scoreValue).getWidth(), cam.viewportHeight/2 + gameOverScreen.getHeight()/6);

                if(scoreValue<10)
                    sb.draw(score.getScoreOne(scoreValue), cam.position.x - gameOverScreen.getWidth()/12 , cam.viewportHeight/1.98f + gameOverScreen.getHeight()/4, score.getScoreOne(scoreValue).getWidth() / 4.5f, score.getScoreOne(scoreValue).getHeight() / 4.5f);
                else if(scoreValue>=10 && scoreValue <100){
                    sb.draw(score.getScoreTen(scoreValue), cam.position.x - gameOverScreen.getWidth()/12 , cam.viewportHeight/1.98f + gameOverScreen.getHeight()/4, score.getScoreOne(scoreValue).getWidth() / 4.5f, score.getScoreTen(scoreValue).getHeight() / 4.5f);
                    sb.draw(score.getScoreOne(scoreValue), cam.position.x - gameOverScreen.getWidth()/12 + score.getScoreTen(scoreValue).getWidth() / 4.5f, cam.viewportHeight/1.98f + gameOverScreen.getHeight()/4, score.getScoreOne(scoreValue).getWidth() / 4.5f, score.getScoreOne(scoreValue).getHeight() / 4.5f);
                }
                else if(scoreValue>=100 && scoreValue <1000){
                    sb.draw(score.getScoreHundered(scoreValue), cam.position.x - gameOverScreen.getWidth()/12 , cam.viewportHeight/1.98f + gameOverScreen.getHeight()/4, score.getScoreHundered(scoreValue).getWidth() / 4.5f, score.getScoreHundered(scoreValue).getHeight() / 4.5f);
                    sb.draw(score.getScoreTen(scoreValue), cam.position.x - gameOverScreen.getWidth()/12 + score.getScoreHundered(scoreValue).getWidth()/4.5f, cam.viewportHeight/1.98f + gameOverScreen.getHeight()/4, score.getScoreTen(scoreValue).getWidth() / 4.5f, score.getScoreTen(scoreValue).getHeight() / 4.5f);
                    sb.draw(score.getScoreOne(scoreValue), cam.position.x - gameOverScreen.getWidth()/12 + (score.getScoreHundered(scoreValue).getWidth()+(score.getScoreTen(scoreValue).getWidth())) / 4.5f, cam.viewportHeight/1.98f + gameOverScreen.getHeight()/4, score.getScoreOne(scoreValue).getWidth() / 4.5f, score.getScoreOne(scoreValue).getHeight() / 4.5f);
                }
                else if(scoreValue>=1000 && scoreValue <10000){
                    sb.draw(score.getScoreThousand(scoreValue), cam.position.x - gameOverScreen.getWidth()/12 , cam.viewportHeight/1.98f + gameOverScreen.getHeight()/4, score.getScoreThousand(scoreValue).getWidth() / 4.5f, score.getScoreThousand(scoreValue).getHeight() / 4.5f);
                    sb.draw(score.getScoreHundered(scoreValue), cam.position.x - gameOverScreen.getWidth()/12 + (score.getScoreThousand(scoreValue).getWidth()/4.5f), cam.viewportHeight/1.98f + gameOverScreen.getHeight()/4, score.getScoreHundered(scoreValue).getWidth() / 4.5f, score.getScoreHundered(scoreValue).getHeight() / 4.5f);
                    sb.draw(score.getScoreTen(scoreValue), cam.position.x - gameOverScreen.getWidth()/12 + ((score.getScoreThousand(scoreValue).getWidth() + score.getScoreHundered(scoreValue).getWidth())/4.5f), cam.viewportHeight/1.98f + gameOverScreen.getHeight()/4, score.getScoreTen(scoreValue).getWidth() / 4.5f, score.getScoreTen(scoreValue).getHeight() / 4.5f);
                    sb.draw(score.getScoreOne(scoreValue), cam.position.x - gameOverScreen.getWidth()/12 + (score.getScoreThousand(scoreValue).getWidth()+score.getScoreHundered(scoreValue).getWidth()+(score.getScoreTen(scoreValue).getWidth())) / 4.5f, cam.viewportHeight/1.98f + gameOverScreen.getHeight()/4, score.getScoreOne(scoreValue).getWidth() / 4.5f, score.getScoreOne(scoreValue).getHeight() / 4.5f);
                }
                else if(scoreValue>=10000 && scoreValue <100000){
                    sb.draw(score.getScoreTenThousand(scoreValue), cam.position.x - gameOverScreen.getWidth()/12, cam.viewportHeight/1.98f + gameOverScreen.getHeight()/4, score.getScoreThousand(scoreValue).getWidth() / 4.5f, score.getScoreThousand(scoreValue).getHeight() / 4.5f);
                    sb.draw(score.getScoreThousand(scoreValue), cam.position.x - gameOverScreen.getWidth()/12 + score.getScoreTenThousand(scoreValue).getWidth()/4.5f , cam.viewportHeight/1.98f + gameOverScreen.getHeight()/4, score.getScoreHundered(scoreValue).getWidth() / 4.5f, score.getScoreHundered(scoreValue).getHeight() / 4.5f);
                    sb.draw(score.getScoreHundered(scoreValue), cam.position.x - gameOverScreen.getWidth()/12 + ((score.getScoreThousand(scoreValue).getWidth()+score.getScoreTenThousand(scoreValue).getWidth())/4.5f), cam.viewportHeight/1.98f + gameOverScreen.getHeight()/4, score.getScoreHundered(scoreValue).getWidth() / 4.5f, score.getScoreHundered(scoreValue).getHeight() / 4.5f);
                    sb.draw(score.getScoreTen(scoreValue), cam.position.x - gameOverScreen.getWidth()/12 + ((score.getScoreTenThousand(scoreValue).getWidth() + score.getScoreThousand(scoreValue).getWidth() + score.getScoreHundered(scoreValue).getWidth())/4.5f), cam.viewportHeight/1.98f + gameOverScreen.getHeight()/4, score.getScoreTen(scoreValue).getWidth() / 4.5f, score.getScoreTen(scoreValue).getHeight() / 4.5f);
                    sb.draw(score.getScoreOne(scoreValue), cam.position.x - gameOverScreen.getWidth()/12 + (score.getScoreTenThousand(scoreValue).getWidth() + score.getScoreThousand(scoreValue).getWidth()+score.getScoreHundered(scoreValue).getWidth()+(score.getScoreTen(scoreValue).getWidth())) / 4.5f, cam.viewportHeight/1.98f + gameOverScreen.getHeight()/4, score.getScoreOne(scoreValue).getWidth() / 4.5f, score.getScoreOne(scoreValue).getHeight() / 4.5f);
                }

                if(highScore<10)
                    sb.draw(score.getScoreOne(highScore), cam.position.x - gameOverScreen.getWidth()/12 , cam.viewportHeight/2.22f + gameOverScreen.getHeight()/4, score.getScoreOne(highScore).getWidth() / 4.5f, score.getScoreOne(highScore).getHeight() / 4.5f);
                else if(highScore>=10 && highScore <100){
                    sb.draw(score.getScoreTen(highScore), cam.position.x - gameOverScreen.getWidth()/12 , cam.viewportHeight/2.22f + gameOverScreen.getHeight()/4, score.getScoreOne(highScore).getWidth() / 4.5f, score.getScoreTen(highScore).getHeight() / 4.5f);
                    sb.draw(score.getScoreOne(highScore), cam.position.x - gameOverScreen.getWidth()/12+score.getScoreTen(highScore).getWidth() / 4.5f , cam.viewportHeight/2.22f + gameOverScreen.getHeight()/4, score.getScoreOne(highScore).getWidth() / 4.5f, score.getScoreOne(highScore).getHeight() / 4.5f);
                }
                else if(highScore>=100 && highScore <1000){
                    sb.draw(score.getScoreHundered(highScore), cam.position.x - gameOverScreen.getWidth()/12 , cam.viewportHeight/2.22f + gameOverScreen.getHeight()/4, score.getScoreHundered(highScore).getWidth() / 4.5f, score.getScoreHundered(highScore).getHeight() / 4.5f);
                    sb.draw(score.getScoreTen(highScore), cam.position.x - gameOverScreen.getWidth()/12 + score.getScoreHundered(highScore).getWidth() / 4.5f, cam.viewportHeight/2.22f + gameOverScreen.getHeight()/4, score.getScoreTen(highScore).getWidth() / 4.5f, score.getScoreTen(highScore).getHeight() / 4.5f);
                    sb.draw(score.getScoreOne(highScore), cam.position.x - gameOverScreen.getWidth()/12 +(score.getScoreHundered(highScore).getWidth()+(score.getScoreTen(highScore).getWidth())) / 4.5f, cam.viewportHeight/2.22f + gameOverScreen.getHeight()/4, score.getScoreOne(highScore).getWidth() / 4.5f, score.getScoreOne(highScore).getHeight() / 4.5f);
                }
                else if(highScore>=1000 && highScore <10000){
                    sb.draw(score.getScoreThousand(highScore), cam.position.x - gameOverScreen.getWidth()/12 , cam.viewportHeight/2.22f + gameOverScreen.getHeight()/4, score.getScoreThousand(highScore).getWidth() / 4.5f, score.getScoreThousand(highScore).getHeight() / 4.5f);
                    sb.draw(score.getScoreHundered(highScore), cam.position.x - gameOverScreen.getWidth()/12 + (score.getScoreThousand(highScore).getWidth()/4.5f), cam.viewportHeight/2.22f + gameOverScreen.getHeight()/4, score.getScoreHundered(highScore).getWidth() / 4.5f, score.getScoreHundered(highScore).getHeight() / 4.5f);
                    sb.draw(score.getScoreTen(highScore), cam.position.x - gameOverScreen.getWidth()/12 + ((score.getScoreThousand(highScore).getWidth() + score.getScoreHundered(highScore).getWidth())/4.5f), cam.viewportHeight/2.22f + gameOverScreen.getHeight()/4, score.getScoreTen(highScore).getWidth() / 4.5f, score.getScoreTen(highScore).getHeight() / 4.5f);
                    sb.draw(score.getScoreOne(highScore), cam.position.x - gameOverScreen.getWidth()/12 + (score.getScoreThousand(highScore).getWidth()+score.getScoreHundered(highScore).getWidth()+(score.getScoreTen(highScore).getWidth())) / 4.5f, cam.viewportHeight/2.22f + gameOverScreen.getHeight()/4, score.getScoreOne(highScore).getWidth() / 4.5f, score.getScoreOne(highScore).getHeight() / 4.5f);
                }
                else if(highScore>=10000 && highScore <100000){
                    sb.draw(score.getScoreTenThousand(highScore), cam.position.x - gameOverScreen.getWidth()/12, cam.viewportHeight/2.22f + gameOverScreen.getHeight()/4, score.getScoreThousand(highScore).getWidth() / 4.5f, score.getScoreThousand(highScore).getHeight() / 4.5f);
                    sb.draw(score.getScoreThousand(highScore), cam.position.x - gameOverScreen.getWidth()/12 + score.getScoreTenThousand(highScore).getWidth()/4.5f , cam.viewportHeight/2.22f + gameOverScreen.getHeight()/4, score.getScoreHundered(highScore).getWidth() / 4.5f, score.getScoreHundered(highScore).getHeight() / 4.5f);
                    sb.draw(score.getScoreHundered(highScore), cam.position.x - gameOverScreen.getWidth()/12 + ((score.getScoreThousand(highScore).getWidth()+score.getScoreTenThousand(highScore).getWidth())/4.5f), cam.viewportHeight/2.22f + gameOverScreen.getHeight()/4, score.getScoreHundered(highScore).getWidth() / 4.5f, score.getScoreHundered(highScore).getHeight() / 4.5f);
                    sb.draw(score.getScoreTen(highScore), cam.position.x - gameOverScreen.getWidth()/12 + ((score.getScoreTenThousand(highScore).getWidth() + score.getScoreThousand(highScore).getWidth() + score.getScoreHundered(highScore).getWidth())/4.5f), cam.viewportHeight/2.22f + gameOverScreen.getHeight()/4, score.getScoreTen(highScore).getWidth() / 4.5f, score.getScoreTen(highScore).getHeight() / 4.5f);
                    sb.draw(score.getScoreOne(highScore), cam.position.x - gameOverScreen.getWidth()/12 + (score.getScoreTenThousand(highScore).getWidth() + score.getScoreThousand(highScore).getWidth()+score.getScoreHundered(highScore).getWidth()+(score.getScoreTen(highScore).getWidth())) / 4.5f, cam.viewportHeight/2.22f + gameOverScreen.getHeight()/4, score.getScoreOne(highScore).getWidth() / 4.5f, score.getScoreOne(highScore).getHeight() / 4.5f);
                }


                if(Gdx.input.justTouched()) {
                    State.gameCounter++;
                    gsm.set(new PlayState(gsm, handler));
                }
            }

        hud.drawHud(sb, cam.position.x + cam.viewportWidth/2, cam.viewportHeight, library);

        if(!gameOver && (cam.position.x >= prefs.getFloat("birdDeathX") - cam.viewportWidth - 50)) {
            sb.draw(birdDeadStain, prefs.getFloat("birdDeathX"), prefs.getFloat("birdDeathY"), birdDeadStain.getWidth()/2, birdDeadStain.getHeight()/2, birdDeadStain.getWidth(), birdDeadStain.getHeight(),1,1, bloodStainRotation, 0, 0, birdDeadStain.getWidth(), birdDeadStain.getHeight(), false, false);
        }

                sb.end();

        }

    @Override
    public void dispose() {
        bg.dispose();
        bird.dispose();
        plank.dispose();
        gameOverScreen.dispose();
        birdDead.dispose();
        birdDeadStain.dispose();
        for(Tube tube: tubes)
            tube.dispose();
        ground.dispose();
        point.dispose();
        stab.dispose();
        groundHit.dispose();
        mBox.dispose();
        hud.dispose();
        library.dispose();
    }

    private void updateGround(){
        if(cam.position.x - (cam.viewportWidth / 2) > groundPos1.x + ground.getWidth())
            groundPos1.add(ground.getWidth()*2, 0);
        if(cam.position.x - (cam.viewportWidth / 2) > groundPos2.x + ground.getWidth())
            groundPos2.add(ground.getWidth()*2, 0);
    }
}