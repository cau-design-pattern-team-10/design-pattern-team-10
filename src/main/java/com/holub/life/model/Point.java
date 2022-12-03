package com.holub.life.model;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Point implements Serializable {
  public Point(Object o) {
    if (o instanceof java.awt.Point) {
      java.awt.Point p = (java.awt.Point) o;
      x = p.x;
      y = p.y;
    } else if (o instanceof Point){
      Point p = (Point) o;
      x = p.x;
      y = p.y;
    } else {
      throw new UnsupportedOperationException();
    }
  }
  /**
   *
   */
  private int x;
  /**
   *
   */
  private int y;

  /**
   * translate using parameters as offsets.
   * @param dx
   * @param dy
   */
  public void translate(final int dx, final int dy) {
    this.x += dx;
    this.y += dy;
  }
}
