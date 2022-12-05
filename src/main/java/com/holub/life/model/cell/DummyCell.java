package com.holub.life.model.cell;

import com.holub.life.model.Direction;
import com.holub.tools.Observer;
import com.holub.tools.Storable;

public class DummyCell implements Cell {

  /**
   *
   */
  static final String UNIMPLEMENTED_EXCEPTION_MSG = "not implemented";
  /**
   *
   */
  private static Cell instance;

  /**
   *
   * @return DummyCell singleton instance
   */
  public static synchronized Cell getInstance() {
    if (instance == null) {
      instance = new DummyCell();
    }
    return instance;
  }

  /**
   *
   * @param dto
   *
   * @return nextState
   */
  @Override
  public boolean figureNextState(final NearestCellsDTO dto) {
    return true;
  }

  /**
   *
   * @param r
   * @param c
   * @return
   */
  @Override
  public Cell edge(final int r, final int c) {
    return this;
  }

  /**
   *
   * @return this cell is alive
   */
  public boolean isAlive() {
    return false;
  }

  /**
   *
   * @return return replica
   */
  public Cell create() {
    return this;
  }

  /**
   *
   * @return not usable
   */
  public Direction isDisruptiveTo() {
    return Direction.NONE;
  }

  /**
   *
   */
  @Override
  public void clear() {
    // Do nothing
  }

  /**
   *
   * @param memento
   * @param upperLeftCorner
   * @param doLoad
   * @return always false
   */
  @Override
  public boolean transfer(final Storable memento,
      final com.holub.life.model.Point upperLeftCorner,
      final boolean doLoad) {
    return false;
  }

  /**
   *
   * @return always 0
   */
  @Override
  public int widthInCells() {
    return 0;
  }

  /**
   *
   * @return always false
   */
  public boolean transition() {
    return false;
  }

  /**
   *
   * @return always throw exception
   */
  public Storable createMemento() {
    throw new UnsupportedOperationException(
        "Cannot create memento of dummy block");
  }

  /**
   *
   */
  @Override
  public void update() {
    throw new UnsupportedOperationException(UNIMPLEMENTED_EXCEPTION_MSG);
  }

  /**
   *
   * @param observer
   */
  @Override
  public void attach(final Observer observer) {
    throw new UnsupportedOperationException(UNIMPLEMENTED_EXCEPTION_MSG);
  }

  /**
   *
   * @param observer
   */
  @Override
  public void detach(final Observer observer) {
    throw new UnsupportedOperationException(UNIMPLEMENTED_EXCEPTION_MSG);
  }
}
