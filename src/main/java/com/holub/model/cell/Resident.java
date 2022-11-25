package com.holub.model.cell;

import com.holub.life.Direction;
import com.holub.model.Point;
import com.holub.tools.Observer;
import com.holub.tools.Storable;
import java.util.LinkedList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/*** ****************************************************************
 * The Resident class implements a single cell---a "resident" of a
 * block.
 */

public final class Resident implements Cell {
  /**
   *
   */
  private static final int ALIVE_NEIGHBOR_NUM = 2;
  /**
   *
   */
  private static final int GENERATING_NEIGHBOR_NUM = 3;
  /**
   *
   */
  private List<Observer> observers;
  /**
   *
   */
  @Getter
  @Setter
  private boolean alive = false;
  /**
   *
   */
  @Getter
  @Setter
  private boolean willBeAlive = false;

  /**
   *
   */
  public Resident() {
    super();
    this.observers = new LinkedList<>();
  }

  /**
   *
   */
  public void toggle() {
    alive = !alive;
  }

  /**
   *
   * @return currentState equals nextState
   */
  private boolean isStable() {
    return alive == willBeAlive;
  }

  /**
   * figure the next state.
   *
   * @param dto
   * @return true if the cell is not stable (will change state on the next
   * transition().
   */
  public boolean figureNextState(final NearestCellsDTO dto) {
    final Cell north = dto.getNorth();
    final Cell south = dto.getSouth();
    final Cell east = dto.getEast();
    final Cell west = dto.getWest();
    final Cell northeast = dto.getNortheast();
    final Cell northwest = dto.getNorthwest();
    final Cell southeast = dto.getSoutheast();
    final Cell southwest = dto.getSouthwest();
    verify(north, "north");
    verify(south, "south");
    verify(east, "east");
    verify(west, "west");
    verify(northeast, "northeast");
    verify(northwest, "northwest");
    verify(southeast, "southeast");
    verify(southwest, "southwest");

    int neighbors = 0;

    if (north.isAlive()) {
      ++neighbors;
    }
    if (south.isAlive()) {
      ++neighbors;
    }
    if (east.isAlive()) {
      ++neighbors;
    }
    if (west.isAlive()) {
      ++neighbors;
    }
    if (northeast.isAlive()) {
      ++neighbors;
    }
    if (northwest.isAlive()) {
      ++neighbors;
    }
    if (southeast.isAlive()) {
      ++neighbors;
    }
    if (southwest.isAlive()) {
      ++neighbors;
    }

    willBeAlive = (neighbors == GENERATING_NEIGHBOR_NUM
        || (alive && neighbors == ALIVE_NEIGHBOR_NUM));
    return !isStable();
  }

  private void verify(final Cell c, final String direction) {
    Cell dummy = DummyCell.getInstance();
    assert (c instanceof Resident) || (c == dummy)
        : "incorrect type for " + direction + ": " + c.getClass().getName();
  }

  /**
   * This cell is monetary, so it's at every edge of itself. It's an internal
   * error for any position except for (0,0) to be requested since the width
   * is 1.
   *
   * @param row
   * @param column
   * @return always this
   */
  public Cell edge(final int row, final int column) {
    if (row != 0 || column != 0) {
      throw new AssertionError();
    }
    return this;
  }

  /**
   *
   * @return changed state
   */
  public boolean transition() {
    boolean changed = isStable();
    alive = willBeAlive;
    return changed;
  }

  /**
   *
   */
  public void clear() {
    alive = false;
    willBeAlive = false;
  }

  /**
   *
   * @return replica
   */
  public Cell create() {
    return new Resident();
  }

  /**
   *
   * @return always 1
   */
  public int widthInCells() {
    return 1;
  }

  /**
   *
   * @return disruptive
   */
  public Direction isDisruptiveTo() {
    return isStable() ? Direction.NONE : Direction.ALL;
  }

  /**
   *
   * @param blob
   * @param upperLeft
   * @param doLoad
   * @return next state
   */
  public boolean transfer(final Storable blob,
      final Point upperLeft, final boolean doLoad) {
    Memento memento = (Memento) blob;
    if (doLoad) {
      alive = memento.isAlive(upperLeft);
      willBeAlive = alive;
      if (alive) {
        return true;
      }
    } else if (alive) {
      memento.markAsAlive(upperLeft);
    }
    update();

    return false;
  }

  /**
   * Mementos must be created by Neighborhood objects. Throw an exception if
   * anybody tries to do it here.
   * @return always throw Exception
   */
  public Storable createMemento() {
    throw new UnsupportedOperationException(
        "May not create memento of a unitary cell");
  }

  @Override
  public void update() {
    for (Observer o : observers) {
      o.detectUpdate(this);
    }
  }

  @Override
  public void attach(final Observer observer) {
    observers.add(observer);
  }

  @Override
  public void detach(final Observer observer) {
    observers.remove(observer);
  }
}
