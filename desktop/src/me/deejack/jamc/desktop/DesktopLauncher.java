package me.deejack.jamc.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

import me.deejack.drop.Drop;
import me.deejack.drop.DropGame;
import me.deejack.jamc.JAMC;

public class DesktopLauncher {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setTitle("JAMC");
		config.useVsync(false);
		//config.vSyncEnabled = false;
		//config.pauseWhenBackground = false;
		//config.pauseWhenMinimized = true;
		new Lwjgl3Application(new JAMC(), config);
		//new LwjglApplication(new DropGame(), config);
	}
}
