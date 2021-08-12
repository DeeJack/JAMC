package me.deejack.jamc.entities.player;

import me.deejack.jamc.items.Item;

public class Inventory {
  private final Item[] items;
  private int selectedSlot = 1;

  public Inventory(int slots) {
    items = new Item[slots];
  }

  public int getSlots() {
    return items.length;
  }

  public Item getItem(int slot) {
    if (slot > items.length || slot < 0)
      throw new IllegalArgumentException("Slots out of bounds: " + slot + "/" + items.length);
    return items[slot];
  }

  public void addItem(Item item, int slot) {
    if (slot > items.length || slot < 0)
      throw new IllegalArgumentException("Slots out of bounds: " + slot + "/" + items.length);
    items[slot] = item;
  }

  public int getSelectedSlot() {
    return selectedSlot;
  }

  public void setSelectedSlot(int slot) {
    if (slot < 1)
      slot = 1;
    if (slot > 9)
      slot = 9;
    this.selectedSlot = slot;
  }

  public Item getSelectedItem() {
    return getItem(getSelectedSlot() - 1);
  }
}
