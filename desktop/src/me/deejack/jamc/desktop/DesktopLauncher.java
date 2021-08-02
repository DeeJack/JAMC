package me.deejack.jamc.desktop;

import badlogic.g3d.badcode.VoxelTest;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import me.deejack.jamc.JAMC;

public class DesktopLauncher {
  public static void main(String[] arg) {
    Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
    config.setTitle("JAMC");
    config.useVsync(false);
    config.setWindowedMode(1280, 720);
    //config.vSyncEnabled = false;
    //config.pauseWhenBackground = false;
    //config.pauseWhenMinimized = true;
    new Lwjgl3Application(new JAMC(), config);
    //new Lwjgl3Application(new VoxelTest(), config);
    //new LwjglApplication(new DropGame(), config);
  }
}
