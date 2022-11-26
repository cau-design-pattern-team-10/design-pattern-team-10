package com.holub.model.cell;

import com.holub.life.Direction;
import com.holub.model.Point;
import com.holub.tools.Observer;
import com.holub.tools.Storable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class NeighborhoodTest {

  /**
   * Neighborhood cell size for test.
   */
  static final int TEST_GRID_SIZE = 3;

  @Test
  void testConstructor() {
    int gs = TEST_GRID_SIZE;
    Cell testCell = new CellForTest();
    Neighborhood neighborhood = new Neighborhood(TEST_GRID_SIZE, testCell);

    Assertions.assertEquals(gs, neighborhood.getGridSize());
    Assertions.assertEquals(gs, neighborhood.getGrid().length);
  }

  static class CellForTest implements Cell {

    @Override
    public boolean figureNextState(final NearestCellsDTO dto) {
      return false;
    }

    @Override
    public Cell edge(final int row, final int column) {
      return null;
    }

    @Override
    public boolean transition() {
      return false;
    }

    @Override
    public boolean isAlive() {
      return false;
    }

    @Override
    public int widthInCells() {
      return 0;
    }

    @Override
    public Cell create() {
      return new CellForTest();
    }

    @Override
    public Direction isDisruptiveTo() {
      return null;
    }

    @Override
    public void clear() {

    }

    @Override
    public boolean transfer(final Storable memento,
        final Point upperLeftCorner,
        final boolean doLoad) {
      return false;
    }

    @Override
    public Storable createMemento() {
      return null;
    }

    @Override
    public void update() {

    }

    @Override
    public void attach(final Observer observer) {

    }

    @Override
    public void detach(final Observer observer) {

    }
  }
}
