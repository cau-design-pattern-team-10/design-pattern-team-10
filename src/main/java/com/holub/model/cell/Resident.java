package com.holub.model.cell;

import com.holub.life.Direction;
import com.holub.tools.Storable;
import com.holub.model.Point;
import com.holub.tools.Observer;
import java.util.LinkedList;
import java.util.List;

/*** ****************************************************************
 * The Resident class implements a single cell---a "resident" of a
 * block.
 *
 * @include /etc/license.txt
 */

public final class Resident implements Cell {

  List<Observer> observers;
  boolean updated;

  public Resident() {
    super();
    this.observers = new LinkedList<>();
  }

  @Override
  public boolean isUpdated() {
    return updated;
  }

  public void toggle() {
    amAlive = !amAlive;
    updated = true;
  }

  private boolean amAlive = false;
  private boolean willBeAlive = false;

  private boolean isStable() {
    return amAlive == willBeAlive;
  }

  /**
   * figure the next state.
   *
   * @return true if the cell is not stable (will change state on the next transition().
   */
  public boolean figureNextState(
      Cell north, Cell south,
      Cell east, Cell west,
      Cell northeast, Cell northwest,
      Cell southeast, Cell southwest) {
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

    willBeAlive = (neighbors == 3 || (amAlive && neighbors == 2));
    return !isStable();
  }

  private void verify(Cell c, String direction) {
    // TODO: change dummy to final static
    Cell DUMMY = new DummyCell();
    assert (c instanceof Resident) || (c == DUMMY)
        : "incorrect type for " + direction + ": " +
        c.getClass().getName();
  }

  /**
   * This cell is monetary, so it's at every edge of itself. It's an internal error for any position
   * except for (0,0) to be requsted since the width is 1.
   */
  public Cell edge(int row, int column) {
    assert row == 0 && column == 0;
    return this;
  }

  public boolean transition() {
    boolean changed = isStable();
    amAlive = willBeAlive;
    return changed;
  }

  public void clear() {
    amAlive = willBeAlive = false;
  }

  public boolean isAlive() {
    return amAlive;
  }

  public Cell create() {
    return new Resident();
  }

  public int widthInCells() {
    return 1;
  }

  public Direction isDisruptiveTo() {
    return isStable() ? Direction.NONE : Direction.ALL;
  }

  public boolean transfer(Storable blob, Point upperLeft, boolean doLoad) {
    Memento memento = (Memento) blob;
    if (doLoad) {
      if (amAlive = willBeAlive = memento.isAlive(upperLeft)) {
        return true;
      }
    } else if (amAlive) {
      memento.markAsAlive(upperLeft);
    }
    update();

    return false;
  }

  /**
   * Mementos must be created by Neighborhood objects. Throw an exception if anybody tries to do it
   * here.
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
    updated = false;
  }

  @Override
  public void attach(Observer observer) {
    observers.add(observer);
  }

  @Override
  public void detach(Observer observer) {
    observers.remove(observer);
  }
}
