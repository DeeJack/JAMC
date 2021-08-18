package me.deejack.jamc.hud.settings;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;

import java.util.ArrayList;
import java.util.List;

public class SettingsPage {
  private final SpriteBatch batch = new SpriteBatch();
  private List<Widget> widgets = new ArrayList<>();

  public SettingsPage() {
    var slider = new Slider(50, 100, 1, false, new Slider.SliderStyle());
    widgets.add(slider);
    batch.begin();
    slider.draw(batch, 0.1F);
    batch.end();
  }
}
