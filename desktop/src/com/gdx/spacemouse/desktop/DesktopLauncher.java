package com.gdx.spacemouse.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.gdx.spacemouse.MyGdxGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 1600;
		config.height = 1024;
		config.samples = 2; // MSAA
		config.depth = 24; // To avoid z-fighting https://stackoverflow.com/questions/42175078/unwanted-triangles-in-opengl-z-fighting
		config.forceExit = true;
		new LwjglApplication(new MyGdxGame(), config);
	}
}
