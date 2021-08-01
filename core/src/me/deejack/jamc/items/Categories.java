package me.deejack.jamc.items;

public enum Categories {
  COMBAT("Combat"),
  FOOD("Food"),
  BLOCKS("Blocks")
  ;
  private String name;

  private Categories(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }
}
