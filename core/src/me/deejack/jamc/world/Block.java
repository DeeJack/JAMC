package me.deejack.jamc.world;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.utils.Array;

import me.deejack.jamc.Event;
import me.deejack.jamc.gameobjects.Drawable;

public class Block implements Drawable {
    private final Array<Event> events = new Array<>();
    private final String name;
    private final int id;
    //private Texture texture;
    private Coordinates coordinates;
    private ModelInstance modelInstance;

    public Block(String name, int id, Coordinates coordinates, Model model) {
        this.name = name;
        this.id = id;
        this.coordinates = coordinates;
        this.modelInstance = new ModelInstance(model);
        modelInstance.transform.translate(coordinates.x(), coordinates.y(), coordinates.z());
    }

    @Override
    public ModelInstance getModel() {
        return modelInstance;
    }
}
