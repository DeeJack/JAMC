package me.deejack.jamc.input;

import java.util.Optional;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;

import me.deejack.jamc.player.Player;
import me.deejack.jamc.world.Block;
import me.deejack.jamc.world.Blocks;
import me.deejack.jamc.world.Coordinates;
import me.deejack.jamc.world.World;

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

    private Optional<Block> findPickedBlock(Vector3 intersection) {
        var position = player.getCamera().position;
        var pickRay = player.getCamera().getPickRay(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
        for (var block : world.getNearBlocks(position)) {
            var direction = pickRay.direction.scl(13);
            pickRay.set(player.getCamera().position, direction);

            var boundingBox = block.getBoundingBox();
            block.getModel().calculateBoundingBox(boundingBox);
            boundingBox.mul(block.getModel().transform);

            /*
             * if (Intersector.intersectRayBoundsFast(pickRay, boundingBox))
             * block.toggleSelection();
             */

            if (Intersector.intersectRayBounds(pickRay, boundingBox, intersection)) {
                return Optional.of(block);
            }
        }
        return Optional.empty();
    }

    private Coordinates findNextFreeBlock(Block pointedBlock, Vector3 intersection) {
        var pointedCoords = pointedBlock.getCoordinates();
        System.out.println(pointedCoords + " - " + intersection);
        if (pointedCoords.z() - 2 == intersection.z) { // Front
            System.out.println("Front");
            return new Coordinates(pointedCoords.x(), pointedCoords.y(), pointedCoords.z() - 5);
        } else if (pointedCoords.z() + 2 == intersection.z) { // Back
            System.out.println("Back");
        } else if (pointedCoords.x() + 2 == intersection.x) { // left
            System.out.println("Left");
        } else if (pointedCoords.x() - 2 == intersection.x) { // right
            System.out.println("Right");
        } else if (pointedCoords.y() - 2 == intersection.y) { // Bot
            System.out.println("Bot");
        } else { // TOp
            System.out.println("Top");
        }
        return null;
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
            var newBlock = Blocks.GRASS;
            world.placeBlock(newBlock, nextCoords);
            /*var pickRay = player.getCamera().getPickRay(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
            var block = findPickedBlock(intersection, pickRay).orElse(null);
            if (block == null)
                return false;
            
            System.out.println("YEP");
            float height = block.getBoundingBox().getHeight();
            float width = block.getBoundingBox().getWidth();
            var blockPosition = block.getCoordinates();
            var meshPart = block.getModel().model.meshParts.get(0);

            for (int side = 0; side < 4; side++) {
                var sideBox = new BoundingBox();
                meshPart.mesh.calculateBoundingBox(sideBox, 4 * side, 4);
                sideBox.mul(block.getModel().transform);

                Vector3 newIntersection = new Vector3();
                if (Intersector.intersectRayBounds(pickRay, sideBox, newIntersection) && newIntersection.equals(intersection))
                    System.out.println("IT works1");
                else
                    System.out.println("Fuck I quit");
            }*/
            /*
             * System.out.println(block.getModel().model.meshes.size);
             * System.out.println(block.getModel().model.meshParts.size); var nod1e =
             * block.getModel().getNode("box"); System.out.println(nod1e); for (var node :
             * block.getModel().nodes) { System.out.println(node.parts.size); for (var part
             * : node.parts) { var mesh = part.meshPart.mesh; short indices[] = new
             * short[mesh.getNumIndices()]; mesh.getIndices(part.meshPart.offset,
             * mesh.getNumIndices(), indices, 0);
             * 
             * float vertices[] = {}; mesh.getVertices(vertices);
             * 
             * Vector3 intersection = new Vector3(); if
             * (Intersector.intersectRayTriangles(pickRay, vertices, indices,
             * mesh.getVertexSize(), intersection)) System.out.println("ASDDDD");
             * 
             * BoundingBox nodeBox = part.meshPart.mesh.calculateBoundingBox();
             * System.out.println(nodeBox); } }
             */

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
