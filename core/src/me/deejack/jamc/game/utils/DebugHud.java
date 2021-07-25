package me.deejack.jamc.game.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class DebugHud {
    public final static DebugHud INSTANCE = new DebugHud();
    private ShapeRenderer shapeRenderer;
    private Array<Line> lines;

    private DebugHud() {
        if (INSTANCE != null)
            throw new AssertionError();
        shapeRenderer = new ShapeRenderer();
        lines = new Array<>();
    }

    public void renderLine(Camera camera, Line line) {
        Gdx.gl.glLineWidth(2);
        shapeRenderer.begin(ShapeType.Line);
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.setColor(Color.WHITE);
        var end = new Vector3(line.end);
        //System.out.println(end);
        shapeRenderer.line(line.start, end);
        shapeRenderer.end();
        Gdx.gl.glLineWidth(1);
    }

    public record Line(Vector3 start, Vector3 end) {
    }
}
