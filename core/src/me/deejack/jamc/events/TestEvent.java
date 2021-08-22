package me.deejack.jamc.events;

import me.deejack.jamc.events.presets.Listener;

public class TestEvent implements Listener {
  @Override
  public void onBlockBreak(BlockData blockData) {
    System.out.println("Block break: " + blockData.getBlock().getId() + ", coordinates: " + blockData.getBlock().getCoordinates());
  }

  @Override
  public void onBlockPlaced(BlockData blockData) {
    System.out.println("Block place: " + blockData.getBlock().getId() + ", coordinates: " + blockData.getBlock().getCoordinates());
  }

  @Override
  public void onBlockClicked(BlockClickedData blockData) {
    System.out.println("Block clicked: " + blockData.getBlock().getId() + ", coordinates: " + blockData.getBlock().getCoordinates() + ", button: " + blockData.getButton());
  }

  @Override
  public void onKeyPress(KeyboardData keyData) {
    System.out.println("Key pressed " + keyData.getKeyCode());
  }

  @Override
  public void onKeyDown(KeyboardData keyData) {
    System.out.println("Key down: " + keyData.getKeyCode());
  }

  @Override
  public void onKeyUp(KeyboardData keyData) {
    System.out.println("Key up normal: " + keyData.getKeyCode());
  }

  @EventType(eventType = EventType.EventTypes.KEY_UP)
  @Priority(priority = Priority.Priorities.HIGH)
  public void onKeyUp2(KeyboardData keyData) {
    System.out.println("Key up high: " + keyData.getKeyCode());
  }

  @EventType(eventType = EventType.EventTypes.KEY_UP)
  @Priority(priority = Priority.Priorities.LOW)
  public void onKeyUp3(KeyboardData keyData) {
    System.out.println("Key up low: " + keyData.getKeyCode());
  }

  @Override
  public void onMove(PlayerEventData eventData) {
    System.out.println("Player moved to " + eventData.getTargetPosition());
  }

  @Override
  public void onMouseButtonPressed(MousePressData buttonPressedData) {
    System.out.println("Mouse button pressed: " + buttonPressedData.getButtonPressed());
  }

  @Override
  public void onMouseMoved(MouseMoveData mouseMoveData) {
  }

  @Override
  public void onScrolled(MouseScrollData mouseScrollData) {
    mouseScrollData.setCancelled(true);
  }
}
