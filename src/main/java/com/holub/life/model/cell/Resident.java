package com.holub.life.model.cell;

import com.holub.life.model.Point;
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
  private List<Observer> observers;
  @Getter @Setter
  private boolean alive = false;
  @Getter @Setter
  private boolean willBeAlive = false;

  public Resident() {
    super();
    this.observers = new LinkedList<>();
  }

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
   * @return true if the cell is not stable (will change state on the next
   * transition().
   */
  public boolean figureNextState() {
    StateDiscriminator discriminator = StateDiscriminator.getInstance();
    return discriminator.figureNextState(this);
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
   * @param blob
   * @param upperLeft
   * @param doLoad
   * @return next state
   */
  public boolean transfer(final Storable blob,
      final Point upperLeft, final boolean doLoad) {
    Memento memento = (Memento) blob;
    if (doLoad) {
      alive = alive || memento.isAlive(upperLeft);
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
