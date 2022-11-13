package com.holub.ui.cell;

import com.holub.model.cell.Cell;
import com.holub.model.cell.Neighborhood;
import com.holub.model.cell.Resident;

public class CellUIFactory {
  private CellUIFactory() {}
  private static CellUIFactory instance;

  public static synchronized CellUIFactory getInstance() {
    if (instance == null) {
      instance = new CellUIFactory();
    }
    return instance;
  }

  public CellUI createCellUI(Cell cell) {
    if (cell instanceof Neighborhood) {
      return new NeighborhoodUI((Neighborhood) cell);
    } else if (cell instanceof Resident) {
      return new ResidentUI((Resident) cell);
    }
    throw new UnsupportedOperationException("not supported type");
  }
}
