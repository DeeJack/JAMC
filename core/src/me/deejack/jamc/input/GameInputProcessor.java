package me.deejack.jamc.input;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import me.deejack.jamc.entities.player.Player;
import me.deejack.jamc.events.EventHandler;
import me.deejack.jamc.events.EventType;
import me.deejack.jamc.events.presets.BlockEvent;
import me.deejack.jamc.items.Items;
import me.deejack.jamc.world.Block;
import me.deejack.jamc.world.Blocks;
import me.deejack.jamc.world.World;

import java.util.Arrays;
import java.util.Optional;

public class GameInputProcessor implements InputProcessor {
  private final World world;
  private final Player player;

  public GameInputProcessor(World world, Player player) {
    this.world = world;
    this.player = player;
  }

  @Override
  public boolean keyDown(int keyCode) {
    return false;
  }

  @Override
  public boolean keyUp(int keycode) {
    return false;
  }

  @Override
  public boolean keyTyped(char character) {
    return false;
  }

  /**
   * Find the block that is pointed by the camera (in the middle of the screen)
   * from the near blocks, return {@link Optional#empty()} there isn't a block
   *
   * @param outIntersection A vector3 in which will be put the intersection point between the ray of the camera and the block
   * @return The block pointed by the camera, or {@link Optional#empty()}
   */
  private Optional<Block> findPickedBlock(Vector3 outIntersection) {
    var position = player.getPosition();
    Block hitBlock = null;
    for (var block : world.getNearBlocks(position)) {
      var pickRay = player.getCamera().getPickRay(player.getCamera().viewportWidth / 2F, player.getCamera().viewportHeight / 2F);
      var ray2 = new Ray(pickRay.origin.cpy(), pickRay.direction.cpy().nor());

      var boundingBox = block.getBoundingBox();
      block.getModel().calculateBoundingBox(boundingBox);
      boundingBox.mul(block.getModel().transform);

      if (Intersector.intersectRayBounds(ray2, boundingBox, outIntersection)) {
        hitBlock = block;
        break;
      }
    }
    return Optional.ofNullable(hitBlock);
  }

  /**
   * Find the coordinate in which the new block will be placed, based on the intersection with the block clicked
   *
   * @param pointedBlock The block pointed by the camera
   * @param intersection The intersection point between the camera and the block
   * @return The coordinates in which the new block will be placed
   */
  private Vector3 findNextFreeBlock(Block pointedBlock, Vector3 intersection) {
    Vector3 pointedCoords = pointedBlock.getCoordinates().cpy();
    Vector3 pointedCoords2 = pointedBlock.getCoordinates().cpy();
    pointedCoords2.scl(World.BLOCK_DISTANCE);

    if (pointedCoords2.z + World.BLOCK_DISTANCE == intersection.z) { // Front
      return new Vector3(pointedCoords.x, pointedCoords.y, pointedCoords.z + 1);
    } else if (pointedCoords2.z == intersection.z) { // Back
      return new Vector3(pointedCoords.x, pointedCoords.y, pointedCoords.z - 1);
    } else if (pointedCoords2.x == intersection.x) { // left
      return new Vector3(pointedCoords.x - 1, pointedCoords.y, pointedCoords.z);
    } else if (pointedCoords2.x + World.BLOCK_DISTANCE == intersection.x) { // right
      return new Vector3(pointedCoords.x + 1, pointedCoords.y, pointedCoords.z);
    } else if (pointedCoords2.y == intersection.y) { // Bot
      return new Vector3(pointedCoords.x, pointedCoords.y - 1, pointedCoords.z);
    } else { // Top
      return new Vector3(pointedCoords.x, pointedCoords.y + 1, pointedCoords.z);
    }
  }

  @Override
  public boolean touchDown(int screenX, int screenY, int pointer, int button) {
    Vector3 intersection = new Vector3();
    var block = findPickedBlock(intersection).orElse(null); // TODO: instead of null, use 'AIR'
    if (block == null)
      return false;

    var eventData = new BlockEvent.BlockClickedData(block, button);
    EventHandler.call(EventType.EventTypes.BLOCK_CLICK, eventData);
    if (eventData.isCancelled())
      return false;

    if (button == Input.Buttons.LEFT) {
      var blockData = new BlockEvent.BlockData(block);
      EventHandler.call(EventType.EventTypes.BLOCK_BREAK, blockData);
      if (blockData.isCancelled())
        return false;

      world.destroyBlock(block);
      var itemType = Arrays.stream(Items.values()).filter(item -> item.getId() == block.getId()).findFirst();
      itemType.ifPresent(item -> player.getInventory().addItem(item.createItem()));
    } else if (button == Input.Buttons.RIGHT) {
      var nextCoords = findNextFreeBlock(block, intersection);
      var targetBlock = world.getBlock(nextCoords);
      if (targetBlock != null) // A block is already present in the coordinates
        return false;

      var currentItem = player.getInventory().getSelectedItem();
      if (currentItem != null) {
        var newBlockType = Blocks.fromId(currentItem.getId());
        if (currentItem.getQuantity() == 1)
          player.getInventory().addItem(null, player.getInventory().getSelectedSlot() - 1);
        currentItem.setQuantity(currentItem.getQuantity() - 1);
        if (newBlockType.isEmpty())
          return false;
        var newBlock = world.placeBlock(newBlockType.get(), nextCoords);

        var placeData = new BlockEvent.BlockData(newBlock);
        EventHandler.call(EventType.EventTypes.BLOCK_PLACE, placeData);
        if (placeData.isCancelled())
          return false;
      }
    } else if (button == Input.Buttons.MIDDLE) { // Middle click
      var selectedItem = player.getInventory().getSelectedItem();
      if (selectedItem != null) // If the player has something in his hand stop
        return false;
      var selectedSlot = player.getInventory().getSelectedSlot();
      var itemType = Arrays.stream(Items.values()).filter(item -> item.getId() == block.getId()).findFirst();
      itemType.ifPresent(items -> player.getInventory().addItem(items.createItem(), selectedSlot - 1));
    }
    return false;
  }

  @Override
  public boolean touchUp(int screenX, int screenY, int pointer, int button) {
    return false;
  }

  @Override
  public boolean touchDragged(int screenX, int screenY, int pointer) {
    return false;
  }

  @Override
  public boolean mouseMoved(int screenX, int screenY) {
    return false;
  }

  @Override
  public boolean scrolled(float amountX, float amountY) {
    return false;
  }
}
