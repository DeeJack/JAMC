package me.deejack.jamc.game;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;

import me.deejack.jamc.player.Inventory;

public class InventoryBar {
    private Inventory inventory;
    private int selectedSlot = 0;
    private Texture inventoryBarTexture;
	private Array<Texture> slots;

    public void create() {
        slots = new Array<>();

        for (int i = 0; i < 10; ++i) {
            createSlot(i);
        }
/*
		final int height = 20;
		final int width = 199;
		var inventoryBarImage = new Pixmap(200, 20, Format.RGBA8888);
		inventoryBarImage.setColor(1, 1, 1, 1);
		
		// Vertical
		inventoryBarImage.drawLine(0, height, 0, 0);
		inventoryBarImage.drawLine(width, height, width, 0);
		// Horizontal
		inventoryBarImage.drawLine(0, height, width, height);
		inventoryBarImage.drawLine(0, 2, width, 2);
		inventoryBarTexture = new Texture(inventoryBarImage);*/
    }

    private void createSlot(int slot) {
        var height = 40;
        var width = 40;

        var slotImage = new Pixmap(width, height, Format.RGBA8888);
        slotImage.setColor(1, 1, 1, 1);
        // Y0 è in alto, (0, 0) è top left

		// Vertical
        slotImage.drawRectangle(0, 0, 1, height);
        slotImage.drawRectangle(width - 1, 0, 1, height);
		//slotImage.drawLine(0, height - 1, 0, 0);
		//slotImage.drawLine(width - 1, 0, width - 1, height - 1);
		// Horizontal
		//slotImage.drawLine(0, height - 1, width, height - 1);
        slotImage.drawRectangle(0, 0, width, 1);
        slotImage.drawRectangle(0, height - 1, width, 1);
		//slotImage.drawLine(0, 1, width - 1, 1);
		slots.add(new Texture(slotImage));
	}

    public void selectSlot(int slot) {
        if (slot > 9 || slot < 0)
            throw new AssertionError("Slot between 0 and 9");

        selectedSlot = slot;
    }

    public void render(SpriteBatch hudBatch, float viewportWidth) {
		//hudBatch.draw(inventoryBarTexture, (viewportWidth / 2) - (inventoryBarTexture.getWidth() / 2), 10);
		var slotSize = 40;

        final float startingX = (viewportWidth / 2) - (slots.size / 2 * slotSize);
        for (int i = 0; i < slots.size; ++i) {
            hudBatch.draw(slots.get(i), startingX + (i * slotSize), 10);
        }
    }

    public void resize(float width, float height) {

    }
}
