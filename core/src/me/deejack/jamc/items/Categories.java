package me.deejack.jamc.items;

public enum Categories {
  COMBAT("Combat"),
  FOOD("Food"),
  BLOCK("Block"),
  ENTITY("Entity");
  private final String name;

  Categories(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }
}
