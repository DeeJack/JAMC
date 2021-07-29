package me.deejack.jamc.gameobjects;

import com.badlogic.gdx.graphics.g3d.ModelInstance;

@FunctionalInterface
public interface Drawable {
  ModelInstance getModel();
}
