package me.deejack.jamc.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import me.deejack.drop.Drop;
import me.deejack.drop.DropGame;
import me.deejack.jamc.JAMC;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 800;
		config.height = 480;
		config.title = "JAMC";
		//config.vSyncEnabled = false;
		//config.pauseWhenBackground = false;
		//config.pauseWhenMinimized = true;
		new LwjglApplication(new JAMC(), config);
		//new LwjglApplication(new DropGame(), config);
	}
}
