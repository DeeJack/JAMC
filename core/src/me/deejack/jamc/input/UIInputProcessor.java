package me.deejack.jamc.input;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Input.Keys;

import me.deejack.jamc.game.UserInterface;

public class UIInputProcessor implements InputProcessor {
    private UserInterface ui;

    public UIInputProcessor(UserInterface userInterface) {
        this.ui = userInterface;
    }

    @Override
    public boolean keyDown(int keyCode) {
        switch (keyCode) {
            case Keys.ESCAPE:
                ui.escMenu();
                return true;
        }
        return ui.isGamePaused();
    }

    @Override
    public boolean keyUp(int keycode) {
        return ui.isGamePaused();
    }

    @Override
    public boolean keyTyped(char character) {
        return ui.isGamePaused();
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return ui.isGamePaused();
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return ui.isGamePaused();
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return ui.isGamePaused();
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return ui.isGamePaused();
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return ui.isGamePaused();
    }

}