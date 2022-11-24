package com.holub.model;

import java.io.Serializable;
import java.util.Objects;

public class Point implements Serializable {

  /**
   *
   */
  private int x;
  /**
   *
   */
  private int y;

  /**
   *
   */
  public Point() {
    // do nothing
  }

  /**
   * for compatibility with java.awt.Point.
   * @param pt
   */
  public Point(final java.awt.Point pt) {
    this.x = pt.x;
    this.y = pt.y;
  }

  /**
   * copy constructor.
   * @param p
   */
  public Point(final Point p) {
    this.x = p.x;
    this.y = p.y;
  }

  /**
   * basic constructor.
   * @param newX
   * @param newY
   */
  public Point(final int newX, final int newY) {
    this.x = newX;
    this.y = newY;
  }

  /**
   * translate using parameters as offsets.
   * @param dx
   * @param dy
   */
  public void translate(final int dx, final int dy) {
    this.x += dx;
    this.y += dy;
  }

  /**
   *
   * @param obj
   * @return
   */
  @Override
  public boolean equals(final Object obj) {
    if (obj instanceof Point) {
      Point pt = (Point) obj;
      return (x == pt.x) && (y == pt.y);
    } else if (obj instanceof java.awt.Point) {
      java.awt.Point pt = (java.awt.Point) obj;
      return (x == pt.x) && (y == pt.y);
    }
    return super.equals(obj);
  }

  /**
   *
   * @return
   */
  @Override
  public int hashCode() {
    return Objects.hash(this.x, this.y);
  }

  /**
   *
   * @return x
   */
  public int getX() {
    return x;
  }

  /**
   *
   * @return y
   */
  public int getY() {
    return y;
  }

  /**
   *
   * @param newX
   */
  public void setX(final int newX) {
    this.x = newX;
  }

  /**
   *
   * @param newY
   */
  public void setY(final int newY) {
    this.y = newY;
  }
}
