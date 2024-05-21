package com.game;

import com.game.GolfGame;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {
	public static void main(String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setForegroundFPS(60);
		config.setTitle("project-1.2-ken-17");
		config.setWindowedMode(GolfGame.WIDTH, GolfGame.HEIGHT);
		new Lwjgl3Application(new GolfGame(), config);
	}
}
