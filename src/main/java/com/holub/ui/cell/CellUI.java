package com.holub.ui.cell;

import com.holub.model.Point;
import com.holub.tools.Observer;
import java.awt.Graphics;
import java.awt.Rectangle;

public interface CellUI extends Observer {

  /**
   * Redraw yourself in the indicated rectangle on the indicated Graphics
   * object if ncessary. This method is meant for a conditional redraw,
   * where some of the cells might not be refreshed (if they haven't changed
   * state, for example).
   *
   * @param g redraw using this graphics,
   * @param here a rectangle that describes the bounds of the current cell.
   * @param drawAll if true, draw an entire compound cell; otherwise, draw
   * only the subcells that need to be redrawn.
   */

  void redraw(Graphics g, Rectangle here, boolean drawAll);

  /**
   * @param p
   */
  void click(Point p);
}
