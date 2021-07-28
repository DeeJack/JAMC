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
    private Array<Slot> slots;

    public void create() {
        slots = new Array<>();

        createSlots();
        slots.get(0).select();
        /*
         * final int height = 20; final int width = 199; var inventoryBarImage = new
         * Pixmap(200, 20, Format.RGBA8888); inventoryBarImage.setColor(1, 1, 1, 1);
         * 
         * // Vertical inventoryBarImage.drawLine(0, height, 0, 0);
         * inventoryBarImage.drawLine(width, height, width, 0); // Horizontal
         * inventoryBarImage.drawLine(0, height, width, height);
         * inventoryBarImage.drawLine(0, 2, width, 2); inventoryBarTexture = new
         * Texture(inventoryBarImage);
         */
    }

    private void createSlots() {
        Pixmap initial = Slot.drawSlot(1);
        Pixmap selected = Slot.drawSlot(3);
        for (int i = 0; i < 9; ++i) {
            slots.add(new Slot(initial, selected));
        }
    }

    public void selectSlot(int slot) {
        if (slot > 9 || slot < 0)
            throw new AssertionError("Slot between 0 and 9");

        slots.get(selectedSlot).unselect();
        selectedSlot = slot;
        slots.get(selectedSlot).select();
    }

    public void render(SpriteBatch hudBatch, float viewportWidth) {
        var slotSize = 40;

        final float startingX = (viewportWidth / 2) - (slots.size / 2 * slotSize);
        for (int i = 0; i < slots.size; ++i) {
            hudBatch.draw(slots.get(i).getTexture(), startingX + (i * slotSize), 10);
        }
    }

    public void resize(float width, float height) {

    }

    private static class Slot {
        private final Texture texture;
        private final Pixmap initial;
        private final Pixmap selected;

        public Slot(Pixmap initial, Pixmap selected) {
            this.initial = initial;
            this.selected = selected;
            this.texture = new Texture(initial);
        }

        public Texture getTexture() {
            return texture;
        }

        public void select() {
            texture.draw(selected, 0, 0);
        }

        public void unselect() {
            texture.draw(initial, 0, 0);
        }

        public static Pixmap drawSlot(int width) {
            final int size = 40;

            var slotImage = new Pixmap(size + 4, size + 4, Format.RGBA8888);
            slotImage.setColor(1, 1, 1, 1);
            // Y0 è in alto, (0, 0) è top left

            // Vertical
            slotImage.drawRectangle(0, 0, width, size);
            slotImage.drawRectangle(size, 0, width, size);
            // Horizontal
            slotImage.drawRectangle(0, 0, size, width);
            slotImage.drawRectangle(0, size, size, width);
            return slotImage;
        }
    }
}
