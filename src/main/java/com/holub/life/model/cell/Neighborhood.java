package com.holub.life.model.cell;

import com.holub.asynch.ConditionVariable;
import com.holub.life.Direction;
import com.holub.life.model.Point;
import com.holub.tools.Observer;
import com.holub.tools.Storable;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;

/***
 * A group of {@link Cell} objects. Cells are grouped into neighborhoods
 * to make board updates more efficient. When a neighborhood is
 * quiescent (none of the Cells it contains are active), then it
 * ignores any requests to update itself.
 *
 * <h3>History</h3>
 * <p>11-29-04
 * Alexy Marinichev fixed the disapearing-glider problem by clearing
 * the active edges in transistion() rather then figureNextState().
 * The original call is commented out and the new line is marked
 * with "(1)"
 */

public final class Neighborhood implements Cell {

  /**
   * Block if reading is not permitted because the grid is transitioning to
   * the next state. Only one lock is used (for the outermost neighborhood)
   * since all updates must be requested through the outermost neighborhood.
   */
  private static final ConditionVariable READING_PERMITTED =
      new ConditionVariable(true);
  /**
   * The following variable is used only by the transition()
   * method. Since Java doesn't support static local variables,
   * I am forced to declare it in class scope, but I deliberately
   * don't put it up at the top of the class definition because
   * it's not really an attribute of the class---it's just
   * an implementation detail of the immediately preceding
   * method.
   */
  private static int nestingLevel = -1;
  /**
   * The actual grid of Cells contained within this neighborhood.
   */
  @Getter
  private final Cell[][] grid;
  /**
   * The neighborhood is square, so gridSize is both the horizontal and
   * vertical size.
   */
  @Getter
  private final int gridSize;
  /**
   *
   */
  private List<Observer> observers;
  /**
   * Returns true only if none of the cells in the Neighborhood changed
   * state during the last transition.
   */

  @Getter
  @Setter
  private boolean amActive = false;
  /**
   * Became stable on the last clock tick. One more refresh is required.
   */
  @Getter
  @Setter
  private boolean oneLastRefreshRequired = false;
  /**
   *
   */
  private final Direction activeEdges = new Direction(Direction.NONE);

  /**
   * Create a new Neighborhood containing gridSize-by-gridSize clones of the
   * prototype. The Prototype is deliberately not put into the grid, so you
   * can reuse it if you like.
   *
   * @param gs
   * @param prototype
   */

  public Neighborhood(final int gs, final Cell prototype) {
    this.gridSize = gs;
    this.grid = new Cell[gridSize][gridSize];
    this.observers = new LinkedList<>();

    for (int row = 0; row < gridSize; ++row) {
      for (int column = 0; column < gridSize; ++column) {
        grid[row][column] = prototype.create();
      }
    }
  }

  /**
   * @return reading permission
   */
  public ConditionVariable getReadingPermitted() {
    return READING_PERMITTED;
  }

  /**
   *
   * @return how many cell
   */

  public Cell create() {
    return new Neighborhood(gridSize, grid[0][0]);
  }

  /**
   * Shows the direction of the cells along the edge of the block that will
   * change state in the next transition. For example, if the upper-left corner
   * has changed, then the current Cell is disruptive in the NORTH, WEST, and
   * NORTHWEST directions. If this is the case, the neighboring cells may need
   * to be updated, even if they were previously stable.
   * @return activeEdges;
   */
  public Direction isDisruptiveTo() {
    return activeEdges;
  }

  /**
   * Figures the next state of the current neighborhood and the contained
   * neighborhoods (or cells).
   * Does not transition to the next state, however. Note that the neighboring
   * cells are passed in as arguments rather than being stored internally
   * ---an example of the Flyweight pattern.
   *
   * @param dto
   * @return true if this neighborhood (i.e. any of it's cells) will change
   * state in the next transition.
   * @see #transition
   */

  @Override
  public boolean figureNextState(final NearestCellsDTO dto) {
    StateDiscriminator discriminator = new StateDiscriminator(dto);
    return discriminator.figureNextState(this);
  }

  /**
   * Transition the neighborhood to the previously-computed state.
   *
   * @return true if the transition actually changed anything.
   * @see #figureNextState
   */
  public boolean transition() {
    // The condition variable is set and reset only by the
    // outermost neighborhood. It's actually incorrect
    // for an inner block to touch it because the whole
    // board has to be locked for edge cells in a subblock
    // to compute their next state correctly. There's no
    // race condition since the only place that transition()
    // is called is from the clock tick, and recursively
    // from here. As long as the recompute time is less
    // than the tick interval, everything's copacetic.

    boolean someSubcellChangedState = false;

    nestingLevel++;
    if (nestingLevel == 0) {
      READING_PERMITTED.set(false);
    }

    activeEdges.clear();              /*(1)*/

    for (int row = 0; row < gridSize; ++row) {
      for (int column = 0; column < gridSize; ++column) {
        if (grid[row][column].transition()) {
          rememberThatCellAtEdgeChangedState(row, column);
          someSubcellChangedState = true;
        }
      }
    }

    nestingLevel--;
    if (nestingLevel < 0) {
      READING_PERMITTED.set(true);
    }

    return someSubcellChangedState;
  }

  /**
   * Modifies activeEdges to indicate whether the addition of the cell at
   * (row,column) makes an edge active.
   * @param row
   * @param column
   */
  public void rememberThatCellAtEdgeChangedState(
      final int row, final int column) {
    if (row == 0) {
      activeEdges.add(Direction.NORTH);

      if (column == 0) {
        activeEdges.add(Direction.NORTHWEST);
      } else if (column == gridSize - 1) {
        activeEdges.add(Direction.NORTHEAST);
      }
    } else if (row == gridSize - 1) {
      activeEdges.add(Direction.SOUTH);

      if (column == 0) {
        activeEdges.add(Direction.SOUTHWEST);
      } else if (column == gridSize - 1) {
        activeEdges.add(Direction.SOUTHEAST);
      }
    }

    if (column == 0) {
      activeEdges.add(Direction.WEST);
    } else if (column == gridSize - 1) {
      activeEdges.add(Direction.EAST);
    }
    // else it's an internal cell. Do nothing.
  }

  /**
   * Return the edge cell in the indicated row and column.
   * @param row
   * @param column
   *
   * @return target grid cell
   */
  public Cell edge(final int row, final int column) {
    if ((row != 0 && row != gridSize - 1)
        && (column != 0 && column != gridSize - 1)) {
      throw new AssertionError("central cell requested from edge()");
    }

    return grid[row][column];
  }

  /**
   * @return always true
   */
  public boolean isAlive() {
    return true;
  }

  /**
   * @return total widthInCell
   */
  public int widthInCells() {
    return gridSize * grid[0][0].widthInCells();
  }

  /**
   *
   */
  public void clear() {
    activeEdges.clear();

    for (int row = 0; row < gridSize; ++row) {
      for (int column = 0; column < gridSize; ++column) {
        grid[row][column].clear();
      }
    }

    amActive = false;
  }

  /**
   * Cause subcells to add an annotation to the indicated memento if they
   * happen to be alive.
   *
   * @param memento
   * @param corner
   * @param load
   * @return next state
   */

  public boolean transfer(final Storable memento, final Point corner,
      final boolean load) {
    int subcellWidth = grid[0][0].widthInCells();
    int myWidth = widthInCells();
    Point upperLeft = corner.toBuilder().build();

    for (int row = 0; row < gridSize; ++row) {
      for (int column = 0; column < gridSize; ++column) {
        if (grid[row][column].transfer(memento, upperLeft, load)) {
          amActive = true;
        }

        Direction d = grid[row][column].isDisruptiveTo();

        if (!d.equals(Direction.NONE)) {
          activeEdges.add(d);
        }

        upperLeft.translate(subcellWidth, 0);
      }
      upperLeft.translate(-myWidth, subcellWidth);
    }
    return amActive;
  }

  /**
   * @return Neighborhood dedicated Memento
   */
  public Storable createMemento() {
    Memento m = new NeighborhoodState();
    transfer(m, new Point(0, 0), Cell.STORE);
    return m;
  }

  /**
   *
   */
  @Override
  public void update() {
    for (Observer observer : observers) {
      observer.detectUpdate(this);
    }
  }

  /**
   *
   * @param observer
   */
  @Override
  public void attach(final Observer observer) {
    observers.add(observer);
  }

  /**
   *
   * @param observer
   */
  @Override
  public void detach(final Observer observer) {
    observers.remove(observer);
  }

  /**
   * The NeighborhoodState stores the state of this neighborhood and all its
   * sub-neighborhoods. For the moment, I'm storing state with serialization,
   * but a future modification might rewrite load() and flush() to use XML.
   */

  private static class NeighborhoodState implements Cell.Memento {

    /**
     *
     */
    private List<Point> liveCells = new LinkedList<>();

    NeighborhoodState(final InputStream in) throws IOException {
      load(in);
    }

    NeighborhoodState() {
    }

    public void load(final InputStream in) throws IOException {
      try {
        ObjectInputStream source = new ObjectInputStream(in);
        Object sourceObject = source.readObject();
        if (sourceObject instanceof List) {
          /* 하위 호환성 유지 */
            liveCells = (List<Point>) ((List) sourceObject).stream()
                .map(Point::new).collect(Collectors.toList());
        }
      } catch (ClassNotFoundException e) {
        // This exception shouldn't be rethrown as
        // a ClassNotFoundException because the
        // outside world shouldn't know (or care) that we're
        // using serialization to load the object. Nothing
        // wrong with treating it as an I/O error, however.

        throw new IOException(
            "Internal Error: Class not found on load");
      }
    }

    public void flush(final OutputStream out) throws IOException {
      ObjectOutputStream sink = new ObjectOutputStream(out);
      sink.writeObject(liveCells);
    }

    public void markAsAlive(final Point location) {
      liveCells.add(location.toBuilder().build());
    }

    public boolean isAlive(final Point location) {
      return liveCells.contains(location);
    }

    public String toString() {
      StringBuilder b = new StringBuilder();

      b.append("NeighborhoodState:\n");
      for (Point p : liveCells) {
        b.append(p.toString()).append("\n");
      }
      return b.toString();
    }
  }
}
