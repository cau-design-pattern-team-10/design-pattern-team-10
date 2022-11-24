package com.holub.ui.cell;

import com.holub.model.cell.Cell;
import com.holub.model.cell.Neighborhood;
import com.holub.model.cell.Resident;
import java.awt.Component;

public final class CellUIFactory {

  /**
   * Unique instance.
   */
  private static CellUIFactory instance;

  /**
   * Singleton pattern.
   */
  private CellUIFactory() {
  }

  /**
   * Singleton pattern.
   * @return singleton instance
   */
  public static synchronized CellUIFactory getInstance() {
    if (instance == null) {
      instance = new CellUIFactory();
    }
    return instance;
  }

  /**
   *
   * @param cell
   * @param parent
   * @return linked CellUI
   */
  public CellUI createCellUI(final Cell cell, final Component parent) {
    if (cell instanceof Neighborhood) {
      return new NeighborhoodUI((Neighborhood) cell, parent);
    } else if (cell instanceof Resident) {
      return new ResidentUI((Resident) cell, parent);
    }
    throw new UnsupportedOperationException("not supported type");
  }
}
