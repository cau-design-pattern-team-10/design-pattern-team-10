package com.holub.life.ui.cell;

import com.holub.life.model.Point;
import com.holub.life.model.cell.Resident;
import com.holub.tools.Observable;
import com.holub.life.ui.Colors;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;

public class ResidentUI implements CellUI {

  /**
   *
   */
  private static final Color BORDER_COLOR = Colors.DARK_YELLOW;
  /**
   *
   */
  private static final Color LIVE_COLOR = Color.RED;
  /**
   *
   */
  private static final Color DEAD_COLOR = Colors.LIGHT_YELLOW;
  /**
   *
   */
  private final Component parent;
  /**
   *
   */
  private Resident cell;

  /**
   *
   * @param c
   * @param p
   */
  public ResidentUI(final Resident c, final Component p) {
    this.cell = c;
    this.parent = p;
    cell.attach(this);
  }

  /**
   *
   * @param g redraw using this graphics,
   * @param here a rectangle that describes the bounds of the current cell.
   * @param drawAll if true, draw an entire compound cell; otherwise, draw
   */
  public void redraw(final Graphics g, final Rectangle here,
      final boolean drawAll) {
    Graphics gReplica = g.create();
    gReplica.setColor(this.cell.isAlive() ? LIVE_COLOR : DEAD_COLOR);
    gReplica.fillRect(here.x + 1, here.y + 1, here.width - 1, here.height - 1);

    // Doesn't draw a line on the far right and bottom of the
    // grid, but that's life, so to speak. It's not worth the
    // code for the special case.

    gReplica.setColor(BORDER_COLOR);
    gReplica.drawLine(here.x, here.y, here.x, here.y + here.height);
    gReplica.drawLine(here.x, here.y, here.x + here.width, here.y);
    gReplica.dispose();
  }

  /**
   *
   * @param p
   */
  @Override
  public void click(final Point p) {
    cell.toggle();
    cell.update();
  }

  /**
   *
   * @param o
   */
  @Override
  public void detectUpdate(final Observable o) {
    parent.repaint();
  }
}
