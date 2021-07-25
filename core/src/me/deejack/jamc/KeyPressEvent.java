package me.deejack.jamc;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Camera;

public class KeyPressEvent implements Event<KeyPressData> {
    private final Camera camera;

    public KeyPressEvent(Camera camera) {
        this.camera = camera;
    }

    @Override
    public void onEvent(KeyPressData data) {
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
