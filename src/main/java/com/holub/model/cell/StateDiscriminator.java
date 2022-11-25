package com.holub.model.cell;


/**
 * @fileName : StateDiscriminator.java
 * @Description : {@link Resident}와 {@link Neighborhood}의 figureNextState 메서드를
 * 분리하는 클래스
 */

public class StateDiscriminator {
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
   * @param dto
   * @return
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
}
