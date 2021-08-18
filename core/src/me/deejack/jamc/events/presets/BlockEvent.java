package me.deejack.jamc.events.presets;

import me.deejack.jamc.events.EventCollection;
import me.deejack.jamc.events.EventData;
import me.deejack.jamc.events.EventType;
import me.deejack.jamc.world.Block;

public interface BlockEvent extends EventCollection {
  @EventType(eventType = EventType.EventTypes.BLOCK_BREAK)
  void onBlockBreak(BlockData blockData);

  @EventType(eventType = EventType.EventTypes.BLOCK_PLACE)
  void onBlockPlaced(BlockData blockData);

  void onBlockClicked(BlockClickedData blockData);

  class BlockData extends EventData {
    // TODO: give a DTO block, not the one with the texture etc.
    private final Block block;

    public BlockData(Block block) {
      this.block = block;
    }

    public Block getBlock() {
      return block;
    }
  }

  @EventType(eventType = EventType.EventTypes.BLOCK_CLICK)
  final class BlockClickedData extends BlockData {
    private final int button;

    public BlockClickedData(Block block, int button) {
      super(block);
      this.button = button;
    }

    public int getButton() {
      return button;
    }
  }
}
