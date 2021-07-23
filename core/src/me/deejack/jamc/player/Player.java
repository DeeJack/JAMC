package me.deejack.jamc.player;

import com.badlogic.gdx.graphics.Camera;

public class Player {
    private final Camera camera;

    public Player(Camera camera) {
        this.camera = camera;
    }

    public Camera getCamera() {
        return camera;
    }
}
