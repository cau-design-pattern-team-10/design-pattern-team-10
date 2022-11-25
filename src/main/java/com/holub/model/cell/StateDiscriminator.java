package com.holub.model.cell;


import com.holub.life.Direction;
import com.holub.model.cell.NearestCellsDTO.NearestCellsDTOBuilder;

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

  public boolean figureNextState(Neighborhood neighborhood) {
    final Cell[][] grid = neighborhood.getGrid();
    final int gridSize = neighborhood.getGridSize();
    boolean nothingHappened = true;

    // Is some adjacent neighborhood active on the edge
    // that adjoins me?

    if (neighborhood.isAmActive()
        || north.isDisruptiveTo().the(Direction.SOUTH)
        || south.isDisruptiveTo().the(Direction.NORTH)
        || east.isDisruptiveTo().the(Direction.WEST)
        || west.isDisruptiveTo().the(Direction.EAST)
        || northeast.isDisruptiveTo().the(Direction.SOUTHWEST)
        || northwest.isDisruptiveTo().the(Direction.SOUTHEAST)
        || southeast.isDisruptiveTo().the(Direction.NORTHWEST)
        || southwest.isDisruptiveTo().the(Direction.NORTHEAST)
    ) {
      Cell northCell;
      Cell southCell;
      Cell eastCell;
      Cell westCell;
      Cell northeastCell;
      Cell northwestCell;
      Cell southeastCell;
      Cell southwestCell;

      for (int row = 0; row < gridSize; ++row) {
        for (int column = 0; column < gridSize; ++column) {
          // Get the current cell's eight neighbors

          if (row == 0) {
            northwestCell = (column == 0)
                ? northwest.edge(gridSize - 1, gridSize - 1)
                : north.edge(gridSize - 1, column - 1);

            northCell = north.edge(gridSize - 1, column);

            northeastCell = (column == gridSize - 1)
                ? northeast.edge(gridSize - 1, 0)
                : north.edge(gridSize - 1, column + 1);
          } else {
            northwestCell = (column == 0)
                ? west.edge(row - 1, gridSize - 1)
                : grid[row - 1][column - 1];

            northCell = grid[row - 1][column];

            northeastCell = (column == gridSize - 1)
                ? east.edge(row - 1, 0)
                : grid[row - 1][column + 1];
          }

          westCell = (column == 0)
              ? west.edge(row, gridSize - 1)
              : grid[row][column - 1];

          eastCell = (column == gridSize - 1)
              ? east.edge(row, 0)
              : grid[row][column + 1];

          if (row == gridSize - 1) {
            southwestCell = (column == 0)
                ? southwest.edge(0, gridSize - 1)
                : south.edge(0, column - 1);

            southCell = south.edge(0, column);

            southeastCell = (column == gridSize - 1)
                ? southeast.edge(0, 0)
                : south.edge(0, column + 1);
          } else {
            southwestCell = (column == 0)
                ? west.edge(row + 1, gridSize - 1)
                : grid[row + 1][column - 1];

            southCell = grid[row + 1][column];

            southeastCell = (column == gridSize - 1)
                ? east.edge(row + 1, 0)
                : grid[row + 1][column + 1];
          }

          // Tell the cell to change its state. If
          // the cell changed (the figureNextState request
          // returned false), then mark the current block as
          // unstable. Also, if the unstable cell is on the
          // edge of the block modify activeEdges to
          //  indicate which edge or edges changed.


          if (grid[row][column].figureNextState(
              new NearestCellsDTOBuilder()
                  .north(northCell)
                  .south(southCell)
                  .east(eastCell)
                  .west(westCell)
                  .northeast(northeastCell)
                  .northwest(northwestCell)
                  .southeast(southeastCell)
                  .southwest(southwestCell).build())) {
            nothingHappened = false;
          }
        }
      }
    }

    if (neighborhood.isAmActive() && nothingHappened) {
      neighborhood.setOneLastRefreshRequired(true);
    }

    neighborhood.setAmActive(!nothingHappened);
    return neighborhood.isAmActive();
  }

  private void verify(final Cell c, final String direction) {
    Cell dummy = DummyCell.getInstance();
    assert (c instanceof Resident) || (c == dummy)
        : "incorrect type for " + direction + ": " + c.getClass().getName();
  }
}
