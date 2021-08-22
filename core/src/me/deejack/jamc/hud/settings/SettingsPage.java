package me.deejack.jamc.hud.settings;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import me.deejack.jamc.JAMC;
import me.deejack.jamc.entities.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class SettingsPage implements Screen {
  private final Stage stage = new Stage();
  private final Table table = new Table();
  private final Player player;
  private final Music music;
  private boolean opened = false;
  private List<Widget> widgets = new ArrayList<>();

  public SettingsPage(Player player, Music music) {
    this.player = player;
    this.music = music;
    Skin skin = new Skin(Gdx.files.internal("data/uiskin.json"));
    var fovValue = ((PerspectiveCamera) player.getCamera()).fieldOfView;

    /*
    Container<Slider> container = new Container<>(slider);
    container.setTransform(true);   // for enabling scaling and rotation
    container.size(100, 60);
    container.setOrigin(container.getWidth() / 2, container.getHeight() / 2);
    container.setPosition(200, 200);
    container.setScale(3);  //scale according to your requirement*/
    table.setColor(Color.GRAY);

    table.align(Align.top);
    table.setTransform(true);
    table.center();
    table.setSize(500, 500);
    //table.setDebug(true);
    table.setPosition((Gdx.graphics.getWidth() / 2F) - (table.getWidth() / 2F), (Gdx.graphics.getHeight() / 2F) - (table.getHeight() / 2F));

    stage.addActor(table);

    table.row().pad(10, 0, 10, 10);
    table.align(Align.topLeft);
    createSlider(skin, fovValue, 70, 120, "FOV", (event, actor) -> {
      var perspectiveCamera = (PerspectiveCamera) player.getCamera();
      perspectiveCamera.fieldOfView = ((Slider) actor).getValue();
    });
    table.align(Align.topRight);
    createSlider(skin, 50, 0, 100, "Music", (event, actor) -> {
      JAMC.MASTER_VOLUME = ((Slider) actor).getValue();
      music.setVolume(((Slider) actor).getValue() / 100F);
    });
    table.row();
    table.align(Align.topLeft);
    createSlider(skin, 10, 0, 10, "Chunks to render", (event, actor) -> {
      System.out.println("Render " + ((Slider) actor).getValue() + " chunks");
    });
    table.add(new Label("New line", skin));
  }

  private void createSlider(Skin skin, float startingValue, float min, float max, String labelText,
                            BiConsumer<ChangeListener.ChangeEvent, Actor> onChange) {
    Slider slider = new Slider(min, max, 1, false, skin);
    slider.setValue(startingValue);
    var sliderLabel = new Label(labelText + " (" + startingValue + "): ", skin);

    slider.addCaptureListener(new ChangeListener() {
      @Override
      public void changed(ChangeEvent event, Actor actor) {
        onChange.accept(event, actor);
        sliderLabel.setText(labelText + " (" + slider.getValue() + "): ");
      }
    });
    table.add(sliderLabel);
    table.add(slider).fillX().uniformX();
  }

  public void render() {
    //Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
    stage.draw();
  }

  public Stage getStage() {
    return stage;
  }

  public void open() {
    opened = true;
  }

  public void close() {
    opened = false;
  }

  public boolean isOpened() {
    return opened;
  }

  @Override
  public void show() {

  }

  @Override
  public void render(float delta) {

  }

  @Override
  public void resize(int width, int height) {
    table.setPosition((Gdx.graphics.getWidth() / 2F) - (table.getWidth() / 2F), (Gdx.graphics.getHeight() / 2F) - (table.getHeight() / 2F));
  }

  @Override
  public void pause() {

  }

  @Override
  public void resume() {

  }

  @Override
  public void hide() {

  }

  @Override
  public void dispose() {

  }
}
