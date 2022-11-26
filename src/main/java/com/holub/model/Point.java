package com.holub.model;

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

  public Point(java.awt.Point p) {
    x = p.x;
    y = p.y;
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
