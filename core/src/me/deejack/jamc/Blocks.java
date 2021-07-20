package me.deejack.jamc;

public enum Blocks {
    STONE("Stone", 1, 1, 1, 1, 1, 1);

    private final String name;
    private final int topTextureId;
    private final int bottomTextureId;
    private final int leftTextureId;
    private final int rightTextureId;
    private final int frontTextureId;
    private final int backTextureId;

    private Blocks(String name, int topTextureId, int bottomTextureId, int leftTextureId,
        int rightTextureId, int frontTextureId, int backTextureId) {
            this.name = name;
            this.topTextureId = topTextureId;
            this.bottomTextureId = bottomTextureId;
            this.frontTextureId = frontTextureId;
            this.backTextureId = backTextureId;
            this.leftTextureId = leftTextureId;
            this.rightTextureId = rightTextureId;
        }

    public Block toBlock() {
        return null;
    }
}
