package me.deejack.jamc.events.presets;

import com.badlogic.gdx.math.Vector3;
import me.deejack.jamc.entities.player.Player;
import me.deejack.jamc.events.EventData;

public interface PlayerEvent {
  void onMove(PlayerEventData eventData);

  public class PlayerEventData extends EventData {
    private final Vector3 targetPosition;
    private final Player player;

    public PlayerEventData(Vector3 targetPosition, Player player) {
      this.targetPosition = targetPosition;
      this.player = player;
    }

    public Player getPlayer() {
      return player;
    }

    public Vector3 getTargetPosition() {
      return targetPosition;
    }
  }
}
