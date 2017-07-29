package com.chirag.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.chirag.game.states.GameStateManager;
import com.chirag.game.states.MenuState;

import java.util.Random;
import java.util.logging.Handler;

public class FlappyDemo extends ApplicationAdapter {

	public static final int WIDTH = 480;
	public static final int HEIGHT = 800;

	public static final String TITLE = "Flappy Bird";
	private GameStateManager gsm;
	private SpriteBatch batch;

	private Music music;
	private Random rn;

	AdHandler handler;

	public static boolean toggleBack = true;

	public FlappyDemo(AdHandler handler){
		this.handler = handler;
	}

	
	@Override
	public void create () {

		Gdx.input.setCatchBackKey(true);

		batch = new SpriteBatch();
		gsm = new GameStateManager();
		rn = new Random();
		int n = rn.nextInt(3) + 1;
		switch(n){
			case 1: music = Gdx.audio.newMusic(Gdx.files.internal("Sounds/music_1.mp3"));
				break;
			case 2: music = Gdx.audio.newMusic(Gdx.files.internal("Sounds/music_2.mp3"));
				break;
			case 3: music = Gdx.audio.newMusic(Gdx.files.internal("Sounds/music_3.mp3"));
				break;
			default: music = Gdx.audio.newMusic(Gdx.files.internal("Sounds/music_1.mp3"));

		}
		music.setLooping(true);
		music.setVolume(0.5f);
		music.play();
		Gdx.gl.glClearColor(1, 0, 0, 1);
		gsm.push(new MenuState(gsm, handler));
	}

	@Override
	public void render () {

		/*if(Gdx.input.justTouched()){
			handler.showAds(toggle);
			toggle = !toggle;
		}*/

		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		gsm.update(Gdx.graphics.getDeltaTime());
		gsm.render(batch);

	}
	
	@Override
	public void dispose () {
		super.dispose();
		music.dispose();
	}
}
