package me.deejack.jamc.entities;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;

public interface Entity {
  float getGravityCoefficient();

  void move(Vector3 translation);

  Vector3 getPosition();

  void setPosition(Vector3 add);
}
