package com.holub.model.cell;

import com.holub.life.Direction;
import com.holub.life.Storable;
import com.holub.tools.Observer;
public class DummyCell implements Cell {

  @Override
  public boolean isUpdated() {
    return false;
  }

  public boolean figureNextState(Cell n, Cell s, Cell e, Cell w,
      Cell ne, Cell nw, Cell se, Cell sw) {
    return true;
  }

  public Cell edge(int r, int c) {
    return this;
  }

  public boolean isAlive() {
    return false;
  }

  public Cell create() {
    return this;
  }

  public Direction isDisruptiveTo() {
    return Direction.NONE;
  }

  public void clear() {
  }

  @Override
  public boolean transfer(Storable memento, com.holub.model.Point upperLeftCorner, boolean doLoad) {
    return false;
  }

  public int widthInCells() {
    return 0;
  }

  public boolean transition() {
    return false;
  }

  public Storable createMemento() {
    throw new UnsupportedOperationException(
        "Cannot create memento of dummy block");
  }

  @Override
  public void update() {
    throw new UnsupportedOperationException("not implemented");
  }

  @Override
  public void attach(Observer observer) {
    throw new UnsupportedOperationException("not implemented");
  }

  @Override
  public void detach(Observer observer) {
    throw new UnsupportedOperationException("not implemented");
  }
}
