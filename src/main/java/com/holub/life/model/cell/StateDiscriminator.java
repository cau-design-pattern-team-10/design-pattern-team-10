package com.holub.life.model.cell;


import com.holub.life.model.cell.NearestCellsDTO.NearestCellsDTOBuilder;
import com.holub.life.system.ResidentService;
import java.util.ArrayList;
import java.util.List;

/**
 * {@link Resident}와 {@link Neighborhood}의 figureNextState 메서드를
 * 분리하는 클래스.
 */

public class StateDiscriminator {
  private static StateDiscriminator instance;
  private ResidentService residentService;

  public static synchronized StateDiscriminator getInstance() {
    if (instance == null) {
      instance = new StateDiscriminator(ResidentService.getInstance());
    }
    return instance;
  }

  private StateDiscriminator(ResidentService residentService) {
    this.residentService = residentService;
  }

  private static final int ALIVE_NEIGHBOR_NUM = 2;
  private static final int GENERATING_NEIGHBOR_NUM = 3;
  /**
   * Resident 인스턴스에 대한 메소드.
   *
   * @param resident
   * @return true if the cell is not stable (will change state on the next
   * transition().
   */
  public boolean figureNextState(final Resident resident) {
    int[] dr = {-1, -1, -1, 0, 0, 1, 1, 1};
    int[] dc = {-1, 0, 1, -1, 1, -1, 0, 1};

    int neighbors = 0;

    for (int i = 0; i < dr.length; i ++) {
      Resident neighbor = residentService.getResident(resident, dc[i], dr[i]);
      if (neighbor != null && neighbor.isAlive()) {
        ++neighbors;
      }
    }

    resident.setWillBeAlive ((neighbors == GENERATING_NEIGHBOR_NUM
        || (resident.isAlive() && neighbors == ALIVE_NEIGHBOR_NUM))
    );

    return resident.isAlive() != resident.isWillBeAlive();
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

    for (int r = 0; r < gridSize; r ++) {
      for (int c = 0; c < gridSize; c ++) {
        if (grid[r][c].figureNextState()) {
          nothingHappened = false;
        }
      }
    }

    if (neighborhood.isAmActive() && nothingHappened) {
      neighborhood.setOneLastRefreshRequired(true);
    }

    neighborhood.setAmActive(!nothingHappened);
    return neighborhood.isAmActive();
  }
}
