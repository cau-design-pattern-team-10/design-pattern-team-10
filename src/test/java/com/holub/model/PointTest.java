package com.holub.model;

import java.util.Random;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class PointTest {
  @Test
  void equalityTest() {
    Random random = new Random();
    final int x = random.nextInt();
    final int y = random.nextInt();
    Point a = new Point(x, y);
    Point b = new Point(x, y);

    Assertions.assertEquals(a, b);
  }
}
