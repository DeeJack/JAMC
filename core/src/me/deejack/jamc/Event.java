package me.deejack.jamc;

public interface Event<P> {
  void onEvent(P parameter);
}
