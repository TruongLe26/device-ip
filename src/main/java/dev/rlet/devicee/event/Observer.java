package dev.rlet.devicee.event;

public interface Observer<T> {
  void update(T data);
}
