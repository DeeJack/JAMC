package me.deejack.jamc.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.utils.Array;

public class World {
	private Environment environment;
	private ModelBatch batch;
	private Array<ModelInstance> instances;

    public void create() {
		batch = new ModelBatch();
        environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1.f));
		environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

        final int TEXTURE_SIZE = 16;
		var textureAtlas = new TextureAtlas(Gdx.files.internal("models/minecraft.atlas"));
		var cubeTextureRegion = textureAtlas.findRegion("minecraft");
		cubeTextureRegion.setRegionX(2);
		var tiles = cubeTextureRegion.split(TEXTURE_SIZE, TEXTURE_SIZE);
		Block stone = Blocks.GRASS.createBlock(0, 0, 0, cubeTextureRegion.getTexture(), tiles);
		Block asd = Blocks.ASD.createBlock(8, 0, 0, cubeTextureRegion.getTexture(), tiles);
		instances = new Array<>();
		instances.add(new ModelInstance(stone.getModel()));
		instances.add(new ModelInstance(asd.getModel()));
    }
    
    public void render(Camera camera) {
        batch.begin(camera);
		batch.render(instances, environment);
		batch.end();
    }

    public void dispose() {
		batch.dispose();
    }
}
