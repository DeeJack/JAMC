package me.deejack.jamc.events;

public interface Event<P> {
  void onEvent(P parameter);
}
