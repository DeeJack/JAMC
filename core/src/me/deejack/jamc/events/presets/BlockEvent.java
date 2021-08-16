package me.deejack.jamc.events.presets;

import me.deejack.jamc.events.EventData;
import me.deejack.jamc.world.Block;

public interface BlockEvent {
  void onBlockBreak(BlockData blockData);

  void onBlockPlaced(BlockData blockData);

  void onBlockClicked(BlockData blockData);

  final class BlockData extends EventData {
    private final Block block;

    public BlockData(Block block) {
      this.block = block;
    }

    public Block getBlock() {
      return block;
    }
  }

  final class BlockClickedData extends EventData {
    private final Block block;
    private final int button;

    public BlockClickedData(Block block, int button) {
      this.block = block;
      this.button = button;
    }

    public Block getBlock() {
      return block;
    }

    public int getButton() {
      return button;
    }
  }
}
