package com.chirag.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.chirag.game.AdHandler;
import com.chirag.game.FlappyDemo;

public class DesktopLauncher {

	public static AdHandler handler = new AdHandler() {
		@Override
		public void showAds(boolean show) {

		}

		@Override
		public void showOrLoadInterstital() {

		}

		@Override
		public void loadInterstitial() {

		}

		@Override
		public void showInterstitial() {

		}
	};

	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = FlappyDemo.WIDTH;
		config.height = FlappyDemo.HEIGHT;
		config.title = FlappyDemo.TITLE;
		new LwjglApplication(new FlappyDemo(handler), config);
	}
}
