package com.holub.model;

import java.io.Serializable;

public class Point implements Serializable {
  public int x, y;
  public Point() {}
  public Point(Point p) {
    this.x = p.x;
    this.y = p.y;
  }
  public Point(int x, int y) {
    this.x = x;
    this.y = y;
  }
  public void translate(int dx, int dy) {
    this.x += dx;
    this.y += dy;
  }
}
