package me.deejack.jamc.input;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import me.deejack.jamc.entities.player.Player;
import me.deejack.jamc.world.Block;
import me.deejack.jamc.world.Blocks;
import me.deejack.jamc.world.World;

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
    //var asd = position.cpy();
    //var pickRay = player.getCamera().getPickRay(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
    //var blocksHit = new ArrayList<Block>();
    //var intersections = new ArrayList<Vector3>();
    Block hitBlock = null;
    for (var block : world.getNearBlocks(position)) {
      var pickRay = player.getCamera().getPickRay(player.getCamera().viewportWidth / 2F, player.getCamera().viewportHeight / 2F);
      //var ray = new Ray(position.cpy(), pickRay.direction.cpy().setLength(1).nor().scl(1));
      var ray2 = new Ray(pickRay.origin.cpy(), pickRay.direction.cpy().nor());
      //var direction = pickRay.direction.scl(10).scl(-1, 1, -1);
      //pickRay.set(player.getCamera().position, direction);
      //System.out.println("Dir: "  + pickRay.direction);
      //pickRay.set(position, pickRay.direction.cpy().scl(-1F, 1F, -1F));
      //var end = new Vector3();
      //pickRay.getEndPoint(end, 6);
      // pickRay.set(player.getPosition(), player.getCamera().direction);
      //System.out.println("Dir: " + ray.direction + ", origin: " + ray.origin);

      var boundingBox = block.getBoundingBox();
      block.getModel().calculateBoundingBox(boundingBox);
      boundingBox.mul(block.getModel().transform);

      if (Intersector.intersectRayBounds(ray2, boundingBox, outIntersection)) {
        //System.out.println("Ray: " + ray2 + "; bounding box: " + boundingBox);
        //System.out.println("Coords: " + block.getCoordinates());
        hitBlock = block;
        //blocksHit.add(block);
        //intersections.add(outIntersection);
        break;
        //return Optional.of(block);
      }
    }
    //Block firstBlock = blocksHit.size() == 0 ? null : blocksHit.get(0);
    //float distance = 100;
    //position = player.getPosition();
    //for (int i = 0; i < blocksHit.size(); i++) {
    //  var block = blocksHit.get(i);
    //  if (block.distanceFrom(position.x, position.y, position.z) < distance) {
    //    distance = block.distanceFrom(position.x, position.y, position.z);
    //    firstBlock = block;
    //     outIntersection.set(intersections.get(i));
    //  }
    // }
    /*for (var block : blocksHit) {
      if (block.distanceFrom(position.x, position.y, position.z) < distance) {
        distance = block.distanceFrom(position.x, position.y, position.z);
        firstBlock = block;
        outIntersection.set(intersections.get(blocksHit.indexOf(block)));
      }
    }*/
    //blocksHit.sort((first, second) -> (int) Math.min(first.distanceFrom(position.x, position.y, position.z), second.distanceFrom(position.x, position.y, position.z)));
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
    System.out.println(pointedCoords2 + " - " + intersection);

    if (pointedCoords2.z + World.BLOCK_DISTANCE == intersection.z) { // Front
      System.out.println("Front");
      return new Vector3(pointedCoords.x, pointedCoords.y, pointedCoords.z + 1);
    } else if (pointedCoords2.z == intersection.z) { // Back
      System.out.println("Back");
      return new Vector3(pointedCoords.x, pointedCoords.y, pointedCoords.z - 1);
    } else if (pointedCoords2.x == intersection.x) { // left
      System.out.println("Left");
      return new Vector3(pointedCoords.x - 1, pointedCoords.y, pointedCoords.z);
    } else if (pointedCoords2.x + World.BLOCK_DISTANCE == intersection.x) { // right
      System.out.println("Right");
      return new Vector3(pointedCoords.x + 1, pointedCoords.y, pointedCoords.z);
    } else if (pointedCoords2.y == intersection.y) { // Bot
      System.out.println("Bot");
      return new Vector3(pointedCoords.x, pointedCoords.y - 1, pointedCoords.z);
    } else { // Top
      System.out.println("Top");
      return new Vector3(pointedCoords.x, pointedCoords.y + 1, pointedCoords.z);
    }
  }

  @Override
  public boolean touchDown(int screenX, int screenY, int pointer, int button) {
    if (button == Input.Buttons.LEFT) {
      var block = findPickedBlock(new Vector3());
      block.ifPresent(world::destroyBlock);
    } else if (button == Input.Buttons.RIGHT) {
      Vector3 intersection = new Vector3();
      var block = findPickedBlock(intersection).orElse(null);
      if (block == null)
        return false;
      var nextCoords = findNextFreeBlock(block, intersection);
      var currentItem = player.getInventory().getSelectedItem();
      if (currentItem != null) {
        var newBlock = Blocks.fromId(currentItem.getId());
        newBlock.ifPresent(type -> world.placeBlock(type, nextCoords));
      }
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
