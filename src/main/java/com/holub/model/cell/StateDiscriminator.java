package com.holub.model.cell;


import com.holub.life.Direction;
import com.holub.model.cell.NearestCellsDTO.NearestCellsDTOBuilder;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * {@link Resident}와 {@link Neighborhood}의 figureNextState 메서드를
 * 분리하는 클래스
 */

public class StateDiscriminator {
  /**
   *
   */
  private List<Cell> neighborsList;
  /**
   *
   */
  private final List<String> directionList = new ArrayList<>(
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
   * Resident 인스턴스에 대한 메소드
   *
   * @param resident
   * @return
   */
  public boolean figureNextState(Resident resident) {
    for(int i = 0; i < neighborsList.size(); ++i){
      verify(neighborsList.get(i), directionList.get(i));
    }

    int neighbors = 0;

    for(Cell neighbor : neighborsList){
      if(neighbor.isAlive()){
        ++neighbors;
      }
    }

    resident.setWillBeAlive( (neighbors == GENERATING_NEIGHBOR_NUM
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
  public boolean figureNextState(Neighborhood neighborhood) {
    final Cell[][] grid = neighborhood.getGrid();
    final int gridSize = neighborhood.getGridSize();
    boolean nothingHappened = true;

    // Is some adjacent neighborhood active on the edge
    // that adjoins me?

    if (neighborhood.isAmActive()
        || neighborsList.get(0).isDisruptiveTo().the(Direction.SOUTH)
        || neighborsList.get(1).isDisruptiveTo().the(Direction.NORTH)
        || neighborsList.get(2).isDisruptiveTo().the(Direction.WEST)
        || neighborsList.get(3).isDisruptiveTo().the(Direction.EAST)
        || neighborsList.get(4).isDisruptiveTo().the(Direction.SOUTHWEST)
        || neighborsList.get(5).isDisruptiveTo().the(Direction.SOUTHEAST)
        || neighborsList.get(6).isDisruptiveTo().the(Direction.NORTHWEST)
        || neighborsList.get(7).isDisruptiveTo().the(Direction.NORTHEAST)
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
                ? neighborsList.get(5).edge(gridSize - 1, gridSize - 1)
                : neighborsList.get(0).edge(gridSize - 1, column - 1);

            northCell = neighborsList.get(0).edge(gridSize - 1, column);

            northeastCell = (column == gridSize - 1)
                ? neighborsList.get(4).edge(gridSize - 1, 0)
                : neighborsList.get(0).edge(gridSize - 1, column + 1);
          } else {
            northwestCell = (column == 0)
                ? neighborsList.get(3).edge(row - 1, gridSize - 1)
                : grid[row - 1][column - 1];

            northCell = grid[row - 1][column];

            northeastCell = (column == gridSize - 1)
                ? neighborsList.get(2).edge(row - 1, 0)
                : grid[row - 1][column + 1];
          }

          westCell = (column == 0)
              ? neighborsList.get(3).edge(row, gridSize - 1)
              : grid[row][column - 1];

          eastCell = (column == gridSize - 1)
              ? neighborsList.get(2).edge(row, 0)
              : grid[row][column + 1];

          if (row == gridSize - 1) {
            southwestCell = (column == 0)
                ? neighborsList.get(7).edge(0, gridSize - 1)
                : neighborsList.get(1).edge(0, column - 1);

            southCell = neighborsList.get(1).edge(0, column);

            southeastCell = (column == gridSize - 1)
                ? neighborsList.get(6).edge(0, 0)
                : neighborsList.get(1).edge(0, column + 1);
          } else {
            southwestCell = (column == 0)
                ? neighborsList.get(3).edge(row + 1, gridSize - 1)
                : grid[row + 1][column - 1];

            southCell = grid[row + 1][column];

            southeastCell = (column == gridSize - 1)
                ? neighborsList.get(2).edge(row + 1, 0)
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
