package me.deejack.jamc;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader;
import com.badlogic.gdx.graphics.g3d.model.data.ModelTexture;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;

public class JAMC implements ApplicationListener {
	private PerspectiveCamera camera;
	// private Model model;
	private AssetManager assetManager;
	private Array<ModelInstance> instances;
	private ModelBatch batch;
	private Environment environment;
	private CameraInputController cameraController;
	private boolean loading = false;

	@Override
	public void create() {
		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1.f));
		environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

		batch = new ModelBatch();

		camera = new PerspectiveCamera(70, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()); // Set the width, height
																								// and a FOV of 70
		camera.position.set(10f, 10f, 10f); // we set the position 10 pixels to the right, 10 up and 10 to the back (z
											// is positive towards the viewer)
		camera.lookAt(0f, 0f, 0f); // We look at the origin, where the object will be placed
		camera.near = 1f; // we set the near and far values
		camera.far = 300f;
		camera.update();

		var modelBuilder = new ModelBuilder();
		var textureAtlas = new TextureAtlas(Gdx.files.internal("models/minecraft.atlas"));
		var cubeTextureRegion = textureAtlas.findRegion("minecraft");
		// var model = modelBuilder.createBox(5f, 5f, 5f, new Material(new
		// TextureAttribute(TextureAttribute.Diffuse, cubeTextureRegion)),
		// Usage.Position | Usage.Normal);
		int attributes = Usage.Position | Usage.Normal | Usage.TextureCoordinates;
		cubeTextureRegion.setRegionHeight(16);
		cubeTextureRegion.setRegionWidth(16);
		
		cubeTextureRegion.setRegionX(32);
		modelBuilder.begin();
		MeshPartBuilder meshBuilder = modelBuilder.part("box", GL20.GL_TRIANGLES, attributes, new Material(TextureAttribute.createDiffuse(cubeTextureRegion.getTexture())));
		meshBuilder.setUVRange(cubeTextureRegion);
		meshBuilder.rect(-2f,-2f,-2f, -2f,2f,-2f,  2f,2f,-2, 2f,-2f,-2f, 0,0,-1);
		meshBuilder.setUVRange(cubeTextureRegion);
		meshBuilder.rect(-2f,2f,2f, -2f,-2f,2f,  2f,-2f,2f, 2f,2f,2f, 0,0,1);
		meshBuilder.setUVRange(cubeTextureRegion);
		meshBuilder.rect(-2f,-2f,2f, -2f,-2f,-2f,  2f,-2f,-2f, 2f,-2f,2f, 0,-1,0);
		meshBuilder.setUVRange(cubeTextureRegion);
		meshBuilder.rect(-2f,2f,-2f, -2f,2f,2f,  2f,2f,2f, 2f,2f,-2f, 0,1,0);
		meshBuilder.setUVRange(cubeTextureRegion);
		meshBuilder.rect(-2f,-2f,2f, -2f,2f,2f,  -2f,2f,-2f, -2f,-2f,-2f, -1,0,0);
		meshBuilder.setUVRange(cubeTextureRegion);
		meshBuilder.rect(2f,-2f,-2f, 2f,2f,-2f,  2f,2f,2f, 2f,-2f,2f, 1,0,0);		
		var model = modelBuilder.end();
		instances = new Array<>();
		instances.add(new ModelInstance(model));
		// var modelLoader = new ObjLoader();
		// var texture = new TextureAtlas(Gdx.files.internal("models/cube.png"));
		// var textureProvider = new Texture(Gdx.files.internal("models/cube.png"));

		// model = modelLoader.loadModel(Gdx.files.internal("models/test-cube2.obj"));
		/*
		 * assetManager = new AssetManager(); loading = true;
		 * assetManager.load("models/test-cube2.g3db", Model.class);
		 */

		// modelInstance = new ModelInstance(model);
		// modelInstance.transform.translate(10, 10, 0);

		cameraController = new CameraInputController(camera);
		Gdx.input.setInputProcessor(cameraController);
	}

	private void doneLoading() {
		loading = false;
		Model cube = assetManager.get("models/test-cube2.g3db", Model.class);
		cube.materials.get(0).set(new TextureAttribute(TextureAttribute.Diffuse,
				(new Texture(Gdx.files.internal("models/test-cube3.png")))));
		instances.add(new ModelInstance(cube));
		instances.get(0).materials.get(0).set(new TextureAttribute(
				TextureAttribute.createNormal(new Texture(Gdx.files.internal("models/test-cube3.png")))));
	}

	@Override
	public void render() {
		if (loading && assetManager.update())
			doneLoading();

		cameraController.update();

		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		ScreenUtils.clear(1, 0, 0, 1);
		// Gdx.gl.glClearColor(0, 0, 0, 1);

		// camera.update();
		batch.begin(camera);
		batch.render(instances, environment);
		batch.end();
	}

	@Override
	public void dispose() {
		//assetManager.dispose();
		batch.dispose();
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}
}
