package com.holub.ui;

import com.holub.model.Resident;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;

public class ResidentUI implements CellUI {
  Resident cell;
  private static final Color BORDER_COLOR = Colors.DARK_YELLOW;
  private static final Color LIVE_COLOR = Color.RED;
  private static final Color DEAD_COLOR = Colors.LIGHT_YELLOW;

  public ResidentUI(Resident cell) {
    this.cell = cell;
  }
  public void redraw(Graphics g, Rectangle here, boolean drawAll) {
    g = g.create();
    g.setColor(this.cell.isAlive() ? LIVE_COLOR : DEAD_COLOR);
    g.fillRect(here.x + 1, here.y + 1, here.width - 1, here.height - 1);

    // Doesn't draw a line on the far right and bottom of the
    // grid, but that's life, so to speak. It's not worth the
    // code for the special case.

    g.setColor(BORDER_COLOR);
    g.drawLine(here.x, here.y, here.x, here.y + here.height);
    g.drawLine(here.x, here.y, here.x + here.width, here.y);
    g.dispose();
  }

  public void userClicked(Point here, Rectangle surface) {
    cell.toggle();
  }
}
