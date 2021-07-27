package me.deejack.jamc.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Cursor.SystemCursor;

public class UserInterface {
    private boolean paused = false;

    public void escMenu() {
        paused = !paused;
        Gdx.input.setCursorCatched(!paused);
        Gdx.input.setCursorPosition(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
        if (paused)
    		Gdx.graphics.setSystemCursor(SystemCursor.Arrow);
        else
    		Gdx.graphics.setSystemCursor(SystemCursor.Crosshair);

    }

    public boolean isGamePaused() {
        return paused;
    }
}