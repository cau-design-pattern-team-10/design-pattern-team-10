package com.holub.model;

import java.io.Serializable;

public class Point implements Serializable {
  public int x, y;
  public Point() {}
  public Point(java.awt.Point pt) {
    this.x = pt.x;
    this.y = pt.y;
  }
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
  public boolean equals(Object obj) {
    if (obj instanceof Point) {
      Point pt = (Point)obj;
      return (x == pt.x) && (y == pt.y);
    } else if (obj instanceof java.awt.Point) {
      java.awt.Point pt = (java.awt.Point)obj;
      return (x == pt.x) && (y == pt.y);
    }
    return super.equals(obj);
  }
}
