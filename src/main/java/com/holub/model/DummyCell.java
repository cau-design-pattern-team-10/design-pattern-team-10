package com.holub.model;

import com.holub.life.Direction;
import com.holub.life.Storable;
import com.holub.tools.Observer;
import com.holub.ui.CellUI;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;

public class DummyCell implements Cell {
    @Override
    public CellUI getCellUI() {
      throw new IllegalCallerException("not implemented");
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

    public int widthInCells() {
      return 0;
    }

    public boolean transition() {
      return false;
    }

    public void userClicked(Point h, Rectangle s) {
    }

    public void redraw(Graphics g, Rectangle here,
        boolean drawAll) {
    }

    public boolean transfer(Storable m, Point ul, boolean load) {
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
