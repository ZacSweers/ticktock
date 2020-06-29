package dev.zacsweers.ticktock.lazyrules.runtime;

import java.io.Serializable;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

/** Utilities for {@link Supplier} copied from Guava. */
final class Suppliers {

  private Suppliers() {

  }

  /**
   * Returns a supplier which caches the instance retrieved during the first
   * call to {@code get()} and returns that value on subsequent calls to
   * {@code get()}. See:
   * <a href="http://en.wikipedia.org/wiki/Memoization">memoization</a>
   *
   * <p>The returned supplier is thread-safe. The delegate's {@code get()}
   * method will be invoked at most once. The supplier's serialized form does
   * not contain the cached value, which will be recalculated when {@code get()}
   * is called on the reserialized instance.
   *
   * <p>If {@code delegate} is an instance created by an earlier call to {@code
   * memoize}, it is returned directly.
   */
  static <T> Supplier<T> memoize(Supplier<T> delegate) {
    return (delegate instanceof MemoizingSupplier)
        ? delegate
        : new MemoizingSupplier<>(requireNonNull(delegate));
  }

  static class MemoizingSupplier<T> implements Supplier<T>, Serializable {
    final Supplier<T> delegate;
    transient volatile boolean initialized;
    // "value" does not need to be volatile; visibility piggy-backs
    // on volatile read of "initialized".
    transient T value;

    MemoizingSupplier(Supplier<T> delegate) {
      this.delegate = delegate;
    }

    @Override
    public T get() {
      // A 2-field variant of Double Checked Locking.
      if (!initialized) {
        synchronized (this) {
          if (!initialized) {
            T t = delegate.get();
            value = t;
            initialized = true;
            return t;
          }
        }
      }
      return value;
    }

    @Override
    public String toString() {
      return "Suppliers.memoize(" + delegate + ")";
    }

    private static final long serialVersionUID = 0;
  }
}
