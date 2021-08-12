package me.deejack.jamc.entities;

import com.badlogic.gdx.math.Vector3;

public interface Entity {
  float getGravityCoefficient();

  void move(Vector3 translation);

  Vector3 getPosition();

  void setPosition(Vector3 add);

  float getVelocity();

  void setVelocity(float velocity);
}
