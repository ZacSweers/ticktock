package dev.zacsweers.ticktock.runtime;

/** Logger interface for any messages from ticktock. Mostly useful for debugging. */
public interface TickTockLogger {
  /** Logs a {@code message}. */
  void log(String message);

  TickTockLogger SYSTEM = System.out::println;
}
