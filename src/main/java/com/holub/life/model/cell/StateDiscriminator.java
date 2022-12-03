package com.holub.life.model.cell;


import com.holub.life.Direction;
import com.holub.life.model.cell.NearestCellsDTO.NearestCellsDTOBuilder;
import java.util.ArrayList;
import java.util.List;

/**
 * {@link Resident}와 {@link Neighborhood}의 figureNextState 메서드를
 * 분리하는 클래스.
 */

public class StateDiscriminator {
  /**
   *
   */
  private NearestCellsDTO dto;
  /**
   *
   */
  private List<Cell> neighborsList;
  /**
   *
   */
  private final List<String> directionNameList = new ArrayList<>(
      List.of(
          "north", "south", "east", "west",
          "northeast", "northwest", "southeast", "southwest"
      ));

  /**
   *
   * @param dto
   *
   */
  public StateDiscriminator(final NearestCellsDTO dto) {
    this.dto = dto;
    neighborsList = new ArrayList<>(
        List.of(
            dto.getNorth(),
            dto.getSouth(),
            dto.getEast(),
            dto.getWest(),
            dto.getNortheast(),
            dto.getNorthwest(),
            dto.getSoutheast(),
            dto.getSouthwest()
        ));
  }

  /**
   *
   */
  private static final int ALIVE_NEIGHBOR_NUM = 2;
  /**
   *
   */
  private static final int GENERATING_NEIGHBOR_NUM = 3;
  /**
   * Resident 인스턴스에 대한 메소드.
   *
   * @param resident
   * @return true if the cell is not stable (will change state on the next
   * transition().
   */
  public boolean figureNextState(final Resident resident) {
    for (int i = 0; i < neighborsList.size(); ++i) {
      verify(neighborsList.get(i), directionNameList.get(i));
    }

    int neighbors = 0;

    for (Cell neighbor : neighborsList) {
      if (neighbor.isAlive()) {
        ++neighbors;
      }
    }

    resident.setWillBeAlive ((neighbors == GENERATING_NEIGHBOR_NUM
        || (resident.isAlive() && neighbors == ALIVE_NEIGHBOR_NUM))
    );

    return resident.isAlive() == resident.isWillBeAlive();
  }

  /**
   * Neighborhood 인스턴스에 대한 메소드
   *
   * @param neighborhood
   * @return
   */
  public boolean figureNextState(final Neighborhood neighborhood) {
    final Cell[][] grid = neighborhood.getGrid();
    final int gridSize = neighborhood.getGridSize();
    boolean nothingHappened = true;

    // Is some adjacent neighborhood active on the edge
    // that adjoins me?

    List<Direction> directionList= new ArrayList<>(
        List.of(
            Direction.SOUTH, Direction.NORTH, Direction.WEST, Direction.EAST,
            Direction.SOUTHWEST, Direction.SOUTHEAST, Direction.NORTHWEST, Direction.NORTHEAST
        ));

    boolean disrupted = false;
    for (int i = 0; i < 8; ++i) {
      disrupted = neighborsList.get(i).isDisruptiveTo().the(directionList.get(i));
      if (disrupted) {
        break;
      }
    }

    if (neighborhood.isAmActive() || disrupted) {
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
                ? dto.getNorthwest().edge(gridSize - 1, gridSize - 1)
                : dto.getNorth().edge(gridSize - 1, column - 1);

            northCell = neighborsList.get(0).edge(gridSize - 1, column);

            northeastCell = (column == gridSize - 1)
                ? dto.getNortheast().edge(gridSize - 1, 0)
                : dto.getNorth().edge(gridSize - 1, column + 1);
          } else {
            northwestCell = (column == 0)
                ? dto.getWest().edge(row - 1, gridSize - 1)
                : grid[row - 1][column - 1];

            northCell = grid[row - 1][column];

            northeastCell = (column == gridSize - 1)
                ? dto.getEast().edge(row - 1, 0)
                : grid[row - 1][column + 1];
          }

          westCell = (column == 0)
              ? dto.getWest().edge(row, gridSize - 1)
              : grid[row][column - 1];

          eastCell = (column == gridSize - 1)
              ? dto.getEast().edge(row, 0)
              : grid[row][column + 1];

          if (row == gridSize - 1) {
            southwestCell = (column == 0)
                ? dto.getSouthwest().edge(0, gridSize - 1)
                : dto.getSouth().edge(0, column - 1);

            southCell = neighborsList.get(1).edge(0, column);

            southeastCell = (column == gridSize - 1)
                ? dto.getSoutheast().edge(0, 0)
                : dto.getSouth().edge(0, column + 1);
          } else {
            southwestCell = (column == 0)
                ? dto.getWest().edge(row + 1, gridSize - 1)
                : grid[row + 1][column - 1];

            southCell = grid[row + 1][column];

            southeastCell = (column == gridSize - 1)
                ? dto.getEast().edge(row + 1, 0)
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
