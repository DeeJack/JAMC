package me.deejack.jamc.game.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector3;

public class DebugHud {
  public final static DebugHud INSTANCE = new DebugHud();
  private ShapeRenderer shapeRenderer;

  private DebugHud() {
    if (INSTANCE != null)
      throw new AssertionError();
    shapeRenderer = new ShapeRenderer();
  }

  public void renderLine(Camera camera, Line line) {
    Gdx.gl.glLineWidth(2);
    shapeRenderer.begin(ShapeType.Line);
    shapeRenderer.setProjectionMatrix(camera.combined);
    shapeRenderer.setColor(Color.WHITE);
    shapeRenderer.line(line.start, line.end);
    shapeRenderer.end();
    Gdx.gl.glLineWidth(1);
  }

  public record Line(Vector3 start, Vector3 end) {
  }
}
