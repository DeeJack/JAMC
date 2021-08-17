package me.deejack.jamc.events;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Priority {
  Priorities priority() default Priorities.NORMAL;

  public enum Priorities {
    TOP,
    HIGH,
    NORMAL,
    LOW
  }
}
