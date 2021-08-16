package me.deejack.jamc.events;

@FunctionalInterface
public interface Event<T extends EventData> {
    void onEvent(T eventData);
}


