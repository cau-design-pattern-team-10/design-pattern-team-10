package com.holub.ui.cell;

import com.holub.model.cell.Cell;
import com.holub.model.cell.Neighborhood;
import com.holub.model.cell.Resident;
import java.awt.Component;

public class CellUIFactory {
  private CellUIFactory() {}
  private static CellUIFactory instance;

  public static synchronized CellUIFactory getInstance() {
    if (instance == null) {
      instance = new CellUIFactory();
    }
    return instance;
  }

  public CellUI createCellUI(Cell cell, Component parent) {
    if (cell instanceof Neighborhood) {
      return new NeighborhoodUI((Neighborhood) cell, parent);
    } else if (cell instanceof Resident) {
      return new ResidentUI((Resident) cell, parent);
    }
    throw new UnsupportedOperationException("not supported type");
  }
}
