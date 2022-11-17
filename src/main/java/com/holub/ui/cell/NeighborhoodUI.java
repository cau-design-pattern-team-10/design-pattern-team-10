package com.holub.ui.cell;

import com.holub.asynch.ConditionVariable;
import com.holub.model.Point;
import com.holub.model.cell.Neighborhood;
import com.holub.tools.Observable;
import com.holub.ui.Colors;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

public class NeighborhoodUI implements CellUI {
  Neighborhood cell;
  private final CellUI[][] grid;
  public NeighborhoodUI(Neighborhood cell) {
    this.cell = cell;
    final int gridSize = cell.getGridSize();
    this.grid = new CellUI[gridSize][gridSize];
    for (int row = 0; row < gridSize; ++row) {
      for (int column = 0; column < gridSize; ++column) {
        grid[row][column] = CellUIFactory.getInstance().createCellUI(cell.grid[row][column]);
      }
    }
  }

  /**
   * Redraw the current neighborhood only if necessary (something changed in the last transition).
   *
   * @param g       Draw onto this graphics.
   * @param here    Bounding rectangle for current Neighborhood.
   * @param drawAll force a redraw, even if nothing has changed.
   * @see #transition
   */
  @Override
  public void redraw(Graphics g, Rectangle here, boolean drawAll) {
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

      if (!readingPermitted.isTrue())  //{=Neighborhood.reading.not.permitted}
      {
        return;
      }

      readingPermitted.waitForTrue();

      for (int row = 0; row < gridSize; ++row) {
        for (int column = 0; column < gridSize; ++column) {
          grid[row][column].redraw(g, subcell, drawAll);  // {=Neighborhood.redraw3}
          subcell.translate(subcell.width, 0);
        }
        subcell.translate(-compoundWidth, subcell.height);
      }

      g = g.create();
      g.setColor(Colors.LIGHT_ORANGE);
      g.drawRect(here.x, here.y, here.width, here.height);

      if (cell.isAmActive()) {
        g.setColor(Color.BLUE);
        g.drawRect(here.x + 1, here.y + 1,
            here.width - 2, here.height - 2);
      }

      g.dispose();
    } catch (InterruptedException e) {  // thrown from waitForTrue. Just
      // ignore it, since not printing is a
      // reasonable reaction to an interrupt.
    }
  }


  /**
   * Notification of a mouse click. The point is relative to the upper-left corner of the surface.
   */
  @Override
  public void click(Point here) {
    Point p = new Point();
    int unitSize = this.cell.widthInCells() / this.cell.getGridSize();
    p.x = here.x % unitSize;
    p.y = here.y % unitSize;
    int row = here.y / unitSize;
    int column = here.x / unitSize;
    grid[row][column].click(p);
    cell.setAmActive(true);
    cell.rememberThatCellAtEdgeChangedState(row, column);
    cell.update();
  }

  @Override
  public void detectUpdate(Observable o) {
  }
}
