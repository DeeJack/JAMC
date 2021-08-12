package me.deejack.jamc.hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import me.deejack.jamc.entities.player.Inventory;

public class InventoryHud {
  private final static int ITEMS_PER_ROW = 8;
  private final static int SLOT_SIZE = 70;

  private final Inventory inventory;
  private final InventoryBar.Slot[] slots = new InventoryBar.Slot[40];
  private final Vector2 startingPosition = new Vector2(100, 100);
  private final Texture inventoryBackground;
  private boolean open = false;
  private InventoryBar.Slot previousSelectedSlot = null;

  public InventoryHud(Inventory inventory) {
    this.inventory = inventory;
    resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    Pixmap pixmap = new Pixmap(ITEMS_PER_ROW * (SLOT_SIZE), (slots.length / ITEMS_PER_ROW) * (SLOT_SIZE) + 20, Format.RGBA4444);
    //pixmap.setColor(Color.rgba4444(Color.GRAY));
    pixmap.setColor(Color.GRAY.r, Color.GRAY.g, Color.GRAY.b, 0.8F);
    pixmap.fill();
    inventoryBackground = new Texture(pixmap);
    create();
  }

  public void open() {
    open = true;
    Gdx.input.setCursorCatched(false);
  }

  public void close() {
    open = false;
    Gdx.input.setCursorCatched(true);
  }

  private void create() {
    Pixmap initial = InventoryBar.Slot.drawSlot(1);
    Pixmap selected = InventoryBar.Slot.drawSlot(3);
    for (int rows = 0; rows < slots.length / ITEMS_PER_ROW; rows++) {
      for (int slotIndex = 0; slotIndex < ITEMS_PER_ROW; slotIndex++) {
        slots[rows * ITEMS_PER_ROW + slotIndex] = new InventoryBar.Slot(initial, selected);
      }
    }
  }

  public void render(SpriteBatch hudBatch) {
    if (!open)
      return;
    hudBatch.draw(inventoryBackground, startingPosition.x, startingPosition.y - 20);

    for (int rows = 0; rows < slots.length / ITEMS_PER_ROW; rows++) {
      for (int slotIndex = 0; slotIndex < ITEMS_PER_ROW; slotIndex++) {
        var index = rows * ITEMS_PER_ROW + slotIndex;
        var currentSlot = slots[index];

        var inventoryBarPadding = (rows == 0 ? -20 : 0);

        hudBatch.draw(currentSlot.getTexture(), startingPosition.x + (slotIndex * SLOT_SIZE), startingPosition.y + (rows * SLOT_SIZE) + inventoryBarPadding, SLOT_SIZE, SLOT_SIZE);
        if (inventory.getItem(index) != null) {
          var paddingX = 3.5F;
          var paddingY = 6F;
          var size = SLOT_SIZE - 10;
          hudBatch.draw(inventory.getItem(index).getImage(), startingPosition.x + (slotIndex * SLOT_SIZE) + paddingX, startingPosition.y + paddingY + (rows * SLOT_SIZE) + inventoryBarPadding, size, size);
        }
      }
    }
  }

  public void updateMouseCursor(int x, int y) {
    if (previousSelectedSlot != null)
      previousSelectedSlot.unselect();

    x -= startingPosition.x; // I use the starting position of the inventory to determine whether the cursor is currently hovering over the inventory
    y -= startingPosition.y;
    var NUM_OF_ROWS = slots.length / ITEMS_PER_ROW;
    if (x < 0 || y < 0 || x > (SLOT_SIZE * ITEMS_PER_ROW) || y > (SLOT_SIZE * NUM_OF_ROWS))
      return;

    y = y / SLOT_SIZE;

    y = NUM_OF_ROWS - y - 1; // The top left is Y:0, so I need to invert the index
    System.out.println("X: " + (x / SLOT_SIZE) + ", Y: " + y);
    //int slotIndex = slots.length - 1 - ((x / SLOT_SIZE) + (y / SLOT_SIZE));
    int slotIndex = (x / SLOT_SIZE) + (y * ITEMS_PER_ROW);

    if (slotIndex > slots.length || slotIndex < 0)
      return;

    var selectedSlot = slots[slotIndex];
    selectedSlot.select();
    previousSelectedSlot = selectedSlot;
  }

  public boolean isOpen() {
    return open;
  }

  public void resize(float newWidth, float newHeight) {
    var width = (newWidth / 2) - (ITEMS_PER_ROW * (SLOT_SIZE / 2));
    var height = (newHeight / 2) - ((slots.length / ITEMS_PER_ROW) * (SLOT_SIZE / 2));
    startingPosition.set(width, height);
  }
}
