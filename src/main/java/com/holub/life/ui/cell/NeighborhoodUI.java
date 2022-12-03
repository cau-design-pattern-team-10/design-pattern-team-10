package com.holub.life.ui.cell;

import com.holub.asynch.ConditionVariable;
import com.holub.life.model.Point;
import com.holub.life.model.cell.Neighborhood;
import com.holub.tools.Observable;
import com.holub.life.ui.Colors;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;

public class NeighborhoodUI implements CellUI {

  /**
   *
   */
  private final CellUI[][] grid;
  /**
   *
   */
  private final Component parent;
  /**
   *
   */
  private Neighborhood cell;

  /**
   *
   * @param c
   * @param p
   */
  public NeighborhoodUI(final Neighborhood c, final Component p) {
    this.cell = c;
    this.parent = p;
    this.cell.attach(this);
    final int gridSize = this.cell.getGridSize();
    this.grid = new CellUI[gridSize][gridSize];
    for (int row = 0; row < gridSize; ++row) {
      for (int column = 0; column < gridSize; ++column) {
        this.grid[row][column] = CellUIFactory.getInstance()
            .createCellUI(this.cell.getGrid()[row][column], this.parent);
      }
    }
  }

  /**
   * Redraw the current neighborhood only if necessary
   * (something changed in the last transition).
   *
   * @param g Draw onto this graphics.
   * @param here Bounding rectangle for current Neighborhood.
   * @param drawAll force a redraw, even if nothing has changed.
   */
  @Override
  public void redraw(final Graphics g, final Rectangle here,
      final boolean drawAll) {
    // If the current neighborhood is stable (nothing changed
    // in the last transition stage), then there's nothing
    // to do. Just return. Otherwise, update the current block
    // and all sub-blocks. Since this algorithm is applied
    // recursively to sublocks, only those blocks that actually
    // need to update will actually do so.

    if (!cell.isAmActive() && !cell.isOneLastRefreshRequired() && !drawAll) {
      return;
    }
    try {
      cell.setOneLastRefreshRequired(false);
      final int gridSize = cell.getGridSize();
      ConditionVariable readingPermitted = cell.getReadingPermitted();
      int compoundWidth = here.width;
      Rectangle subcell = new Rectangle(here.x, here.y,
          here.width / gridSize,
          here.height / gridSize);

      // Check to see if we can paint. If not, just return. If
      // so, actually wait for permission (in case there's
      // a race condition, then paint.

      if (!readingPermitted.isTrue()) {
        return;
      }

      readingPermitted.waitForTrue();

      for (int row = 0; row < gridSize; ++row) {
        for (int column = 0; column < gridSize; ++column) {
          grid[row][column].redraw(g, subcell, drawAll);
          subcell.translate(subcell.width, 0);
        }
        subcell.translate(-compoundWidth, subcell.height);
      }

      Graphics gReplica = g.create();
      gReplica.setColor(Colors.LIGHT_ORANGE);
      gReplica.drawRect(here.x, here.y, here.width, here.height);

      if (cell.isAmActive()) {
        gReplica.setColor(Color.BLUE);
        gReplica.drawRect(here.x + 1, here.y + 1,
            here.width - 2, here.height - 2);
      }

      gReplica.dispose();
    } catch (InterruptedException e) {  // thrown from waitForTrue. Just
      // ignore it, since not printing is a
      // reasonable reaction to an interrupt.
    }
  }


  /**
   *
   * @param here
   */
  @Override
  public void click(final Point here) {
    Point p = new Point();
    int unitSize = this.cell.widthInCells() / this.cell.getGridSize();
    p.setX(here.getX() % unitSize);
    p.setY(here.getY() % unitSize);
    int row = here.getY() / unitSize;
    int column = here.getX() / unitSize;
    grid[row][column].click(p);
    cell.setAmActive(true);
    cell.rememberThatCellAtEdgeChangedState(row, column);
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
