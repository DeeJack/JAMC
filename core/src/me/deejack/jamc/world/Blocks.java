package me.deejack.jamc.world;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;

public enum Blocks {
        STONE("Stone", 0, 1, 1, 1, 1, 1, 1),
        // STONE("Stone", 0, 1, 2, 3, 4, 5, 6);
        ASD("asd", 2, 0, 1, 2, 3, 4, 5), GRASS("Grass", 1, 0, 2, 3, 3, 3, 3);

        private final static int TEXTURE_SIZE = 16;

        private final String name;
        private final int id;
        private final int topTextureId;
        private final int bottomTextureId;
        private final int leftTextureId;
        private final int rightTextureId;
        private final int frontTextureId;
        private final int backTextureId;

        private Blocks(String name, int id, int topTextureId, int bottomTextureId, int leftTextureId,
                        int rightTextureId, int frontTextureId, int backTextureId) {
                this.name = name;
                this.id = id;
                this.topTextureId = topTextureId;
                this.bottomTextureId = bottomTextureId;
                this.frontTextureId = frontTextureId;
                this.backTextureId = backTextureId;
                this.leftTextureId = leftTextureId;
                this.rightTextureId = rightTextureId;
        }

        public Block createBlock(int x, int y, int z, Texture fullTexture, TextureRegion[][] tiles) {
                int attributes = Usage.Position | Usage.Normal | Usage.TextureCoordinates;
                var modelBuilder = new ModelBuilder();
                modelBuilder.begin();
                MeshPartBuilder meshBuilder = modelBuilder.part("box", GL20.GL_TRIANGLES, attributes,
                                new Material(TextureAttribute.createDiffuse(fullTexture)));
                meshBuilder.setUVRange(tiles[0][backTextureId]);
                meshBuilder.rect(new Vector3(2, -2, -2), new Vector3(-2, -2, -2), new Vector3(-2, 2, -2),
                                new Vector3(2, 2, -2), new Vector3(0, 0, -1));
                meshBuilder.setUVRange(tiles[0][frontTextureId]);
                meshBuilder.rect(new Vector3(-2, -2, 2), new Vector3(2, -2, 2), new Vector3(2, 2, 2),
                                new Vector3(-2, 2, 2), new Vector3(0, 0, -1));
                meshBuilder.setUVRange(tiles[0][bottomTextureId]);
                meshBuilder.rect(new Vector3(2, -2, 2), new Vector3(-2, -2, 2), new Vector3(-2, -2, -2),
                                new Vector3(2, -2, -2), new Vector3(0, -1, 0));
                meshBuilder.setUVRange(tiles[0][topTextureId]);
                meshBuilder.rect(new Vector3(-2, 2, -2), new Vector3(-2, 2, 2), new Vector3(2, 2, 2),
                                new Vector3(2, 2, -2), new Vector3(0, 1, 0));
                meshBuilder.setUVRange(tiles[0][leftTextureId]);
                meshBuilder.rect(new Vector3(-2, -2, -2), new Vector3(-2, -2, 2), new Vector3(-2, 2, 2),
                                new Vector3(-2, 2, -2), new Vector3(-1, 0, 0));
                meshBuilder.setUVRange(tiles[0][rightTextureId]);
                meshBuilder.rect(new Vector3(2, -2, 2), new Vector3(2, -2, -2), new Vector3(2, 2, -2),
                                new Vector3(2, 2, 2), new Vector3(1, 0, 0));
                var model = modelBuilder.end();

                return new Block(name, id, new Coordinates(x, y, z), model);
        }
}
