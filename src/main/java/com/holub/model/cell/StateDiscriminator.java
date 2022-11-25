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
  Cell north;
  Cell south;
  Cell east;
  Cell west;
  Cell northeast;
  Cell northwest;
  Cell southeast;
  Cell southwest;

  /**
   *
   * @param dto
   *
   */
  public StateDiscriminator(final NearestCellsDTO dto) {
    north = dto.getNorth();
    south = dto.getSouth();
    east = dto.getEast();
    west = dto.getWest();
    northeast = dto.getNortheast();
    northwest = dto.getNorthwest();
    southeast = dto.getSoutheast();
    southwest = dto.getSouthwest();
  }

  private static final int ALIVE_NEIGHBOR_NUM = 2;
  /**
   *
   */
  private static final int GENERATING_NEIGHBOR_NUM = 3;
  /**
   *
   * @param resident
   * @return
   */
  public boolean figureNextState(Resident resident) {
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

    resident.setWillBeAlive( (neighbors == GENERATING_NEIGHBOR_NUM
        || (resident.isAlive() && neighbors == ALIVE_NEIGHBOR_NUM))
    );
    return resident.isAlive() == resident.isWillBeAlive();
  }

  private void verify(final Cell c, final String direction) {
    Cell dummy = DummyCell.getInstance();
    assert (c instanceof Resident) || (c == dummy)
        : "incorrect type for " + direction + ": " + c.getClass().getName();
  }
}
