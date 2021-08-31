package me.deejack.jamc.utils;

import java.util.Arrays;
import java.util.function.IntFunction;

/**
 * @param <T>
 */
public class ResizableArray<T> {
  private Object[] elements;
  private int size;
  private int capacity;

  public ResizableArray(int initialCapacity) {
    this.elements = new Object[initialCapacity];
    this.capacity = initialCapacity;
  }

  private void grow() {
    Object[] newArray = new Object[capacity * 2];
    System.arraycopy(elements, 0, newArray, 0, capacity);
    capacity *= 2;
    this.elements = newArray;
  }

  public void add(int index, T element) {
    while (index > capacity)
      grow();
    elements[index] = element;
    size++;
  }

  public void add(T element) {
    if (size == capacity)
      grow();
    elements[size++] = element;
  }

  /**
   * Suppressed warning because it is guaranteed by the {@link ResizableArray#add} method that the element will be of the 'T' type
   *
   * @param index
   * @return the element on the index
   * @throw IndexOutOfBoundsException if the index is bigger than the capacity
   */
  @SuppressWarnings("unchecked")
  public T get(int index) {
    if (index > capacity)
      return null;
    //throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size + ", Capacity: " + capacity);
    return (T) elements[index];
  }

  public int size() {
    return size;
  }

  public int capacity() {
    return capacity;
  }

  public T[] toArray(IntFunction<T[]> generator) {
    var targetArray = generator.apply(0);
    System.arraycopy(elements, 0, targetArray, 0, size);
    return targetArray;
  }

  public void clear() {
    Arrays.fill(elements, 0, size, null);
    size = 0;
  }
}
