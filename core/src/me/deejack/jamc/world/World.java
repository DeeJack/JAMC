package me.deejack.jamc.world;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import me.deejack.jamc.game.utils.DebugHud;
import me.deejack.jamc.game.utils.DebugHud.Line;

public class World {
	public static final int BLOCK_DISTANCE = 5;
	private Environment environment;
	private ModelBatch batch;
	private Array<ModelInstance> instances;
	private List<Block> blocks;

	private TextureRegion[][] tiles;
	private Texture fullTexture;

	public void create() {
		batch = new ModelBatch();
		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1.f));
		environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

		final int TEXTURE_SIZE = 16;
		var textureAtlas = new TextureAtlas(Gdx.files.internal("models/minecraft.atlas"));
		var cubeTextureRegion = textureAtlas.findRegion("minecraft");
		cubeTextureRegion.setRegionX(2);
		fullTexture = cubeTextureRegion.getTexture();
		tiles = cubeTextureRegion.split(TEXTURE_SIZE, TEXTURE_SIZE);
		blocks = new ArrayList<>();
		instances = new Array<>();

		for (int i = 0; i < 10; ++i) {
			for (int j = 0; j < 10; ++j) {
				//Block grass = Blocks.GRASS.createBlock(i * 5, 0, j * 5, cubeTextureRegion.getTexture(), tiles);
				placeBlock(Blocks.GRASS, new Coordinates(i * 5, 0, j * 5));
			}
		}
	}

	public void render(Camera camera) {
		camera.update(); // Update the player's camera
		batch.begin(camera);
		batch.render(instances, environment);

		var pickRay = camera.getPickRay(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
		var end = new Vector3();
		pickRay.getEndPoint(end, 10).sub(0, 0, 2);
		DebugHud.INSTANCE.renderLine(camera, new Line(pickRay.origin, end));

		batch.end();
	}

	public void dispose() {
		batch.dispose();
	}

	public void placeBlock( Blocks block, Coordinates coordinates) {
		Block newBlock = block.createBlock(coordinates.x(), coordinates.y(), coordinates.z(), fullTexture, tiles);
		blocks.add(newBlock);
		instances.add(newBlock.getModel());
	}

	public void destroyBlock(Block block) {
		instances.removeValue(block.getModel(), false);
		blocks.remove(block);
	}

	public Set<Block> getNearBlocks(Vector3 position) {
		final int maxDistance = 10;
		var nearBlocks = new HashSet<Block>(blocks);
		nearBlocks.removeIf(block -> block.distanceFrom(position.x, position.y, position.z) > maxDistance);
		return nearBlocks;
	}

	public List<Block> getBlocks() {
		return blocks;
	}
}
