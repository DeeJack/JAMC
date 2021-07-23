package me.deejack.jamc.input;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Camera;

import me.deejack.jamc.Event;

public class KeyPressEvent implements Event<KeyPressData> {
    private final Camera camera;

    public KeyPressEvent(Camera camera) {
        this.camera = camera;
    }

    @Override
    public void onEvent(KeyPressData data) {
        System.out.println(Keys.toString(data.getKeyCode()));
    }
}

class KeyPressData {
    private final int keyCode;

    public KeyPressData(int keyCode) {
        this.keyCode = keyCode;
    }

    public int getKeyCode() {
        return keyCode;
    }
}
