package me.deejack.jamc;

import me.deejack.jamc.gameobjects.Drawable;

public interface Block extends Drawable {
    void onRightClick();

    void onBreak();

    void onPlaced();
}
