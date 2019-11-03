package com.krloxz.auctionsniper.util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;

/**
 * @author Carlos Gomez
 *
 */
public class Announcer<T extends EventListener> {
  private final T proxy;
  private final List<T> listeners = new ArrayList<>();

  public Announcer(final Class<? extends T> listenerType) {
    this.proxy = listenerType.cast(Proxy.newProxyInstance(
        listenerType.getClassLoader(),
        new Class<?>[] {listenerType},
        new InvocationHandler() {
          @Override
          public Object invoke(final Object aProxy, final Method method, final Object[] args) throws Throwable {
            announce(method, args);
            return null;
          }
        }));
  }

  public void addListener(final T listener) {
    this.listeners.add(listener);
  }

  public void removeListener(final T listener) {
    this.listeners.remove(listener);
  }

  public T announce() {
    return this.proxy;
  }

  private void announce(final Method m, final Object[] args) {
    try {
      for (final T listener : this.listeners) {
        m.invoke(listener, args);
      }
    } catch (final IllegalAccessException e) {
      throw new IllegalArgumentException("could not invoke listener", e);
    } catch (final InvocationTargetException e) {
      final Throwable cause = e.getCause();

      if (cause instanceof RuntimeException) {
        throw (RuntimeException) cause;
      } else if (cause instanceof Error) {
        throw (Error) cause;
      } else {
        throw new UnsupportedOperationException("listener threw exception", cause);
      }
    }
  }

  public static <T extends EventListener> Announcer<T> to(final Class<? extends T> listenerType) {
    return new Announcer<>(listenerType);
  }
}
