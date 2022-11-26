package com.holub.system;

import com.holub.io.Files;
import com.holub.model.Point;
import com.holub.model.cell.Cell;
import com.holub.model.cell.DummyCell;
import com.holub.model.cell.NearestCellsDTO;
import com.holub.model.cell.Neighborhood;
import com.holub.model.cell.Resident;
import com.holub.tools.Observable;
import com.holub.tools.Observer;
import com.holub.tools.Storable;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import lombok.Getter;

public class Universe implements Observable {

  /**
   * The default height and width of a Neighborhood in cells. If it's too big,
   * you'll run too slowly because you have to update the entire block as
   * a unit, so there's more to do. If it's too small, you have too many blocks
   * to check. I've found that 8 is a good compromise.
   */
  private static final int DEFAULT_GRID_SIZE = 8;
  /**
   *
   */
  @Getter
  private final Neighborhood outermostCell;
  /**
   *
   */
  private final Clock clock;
  /**
   *
   */
  private final List<Observer> observers;

  /**
   * To control, resident
   * Created by Min Uk Lee
   */
  private final ResidentService residentService;

  /**
   *
   * @param c
   */
  public Universe(final Clock c) {
    this.observers = new LinkedList<>();
    this.clock = c;
    this.residentService = new ResidentService();
    outermostCell = new Neighborhood(DEFAULT_GRID_SIZE,
            new Neighborhood(DEFAULT_GRID_SIZE, new Resident()));

    Cell[][] neighborhood = outermostCell.getGrid();
    for (int x = 0; x < DEFAULT_GRID_SIZE; x ++) {
      for (int y = 0; y < DEFAULT_GRID_SIZE; y ++) {
        Cell[][] residents = ((Neighborhood)neighborhood[x][y]).getGrid();
        for (int innerX = 0; innerX < DEFAULT_GRID_SIZE; innerX ++) {
          for (int innerY = 0; innerY < DEFAULT_GRID_SIZE; innerY ++) {
            residentService.register((Resident)residents[innerY][innerX],
                Point.builder().x(x * DEFAULT_GRID_SIZE + innerX)
                    .y(y * DEFAULT_GRID_SIZE + innerY).build());
          }
        }
      }
    }

    clock.addClockListener(() -> {
      Cell dummy = DummyCell.getInstance();
      boolean nextState = outermostCell.figureNextState(
         NearestCellsDTO.builder()
              .north(dummy)
              .south(dummy)
              .east(dummy)
              .west(dummy)
              .northeast(dummy)
              .northwest(dummy)
              .southeast(dummy)
              .southwest(dummy).build());
      if (nextState && outermostCell.transition()) {
          update();
      }
    }
    );
  }

  /**
   *
   */
  public void clear() {
    outermostCell.clear();
  }

  /**
   *
   * @throws IOException
   */
  public void doLoad() throws IOException {
    FileInputStream in = new FileInputStream(
        Files.userSelected(".", ".life", "Life File", "Load"));

    clock.stop();    // stop the game and
    outermostCell.clear();      // clear the board.

    Storable memento = outermostCell.createMemento();
    memento.load(in);
    outermostCell.transfer(memento, new Point(0, 0), Cell.LOAD);

    in.close();
    update();
  }

  /**
   *
   * @throws IOException
   */
  public void doStore() throws IOException {
    FileOutputStream out = new FileOutputStream(
        Files.userSelected(".", ".life", "Life File", "Write"));

    clock.stop();    // stop the game

    Storable memento = outermostCell.createMemento();
    outermostCell.transfer(memento, new Point(0, 0), Cell.STORE);
    memento.flush(out);

    out.close();
  }

  /**
   *
   * @return total cell size
   */
  public int widthInCells() {
    return outermostCell.widthInCells();
  }

  /**
   *
   */
  @Override
  public void update() {
    for (Observer observer : observers) {
      observer.detectUpdate(this);
    }
  }

  /**
   *
   * @param observer
   */
  @Override
  public void attach(final Observer observer) {
    observers.add(observer);
  }

  /**
   *
   * @param observer
   */
  @Override
  public void detach(final Observer observer) {
    observers.remove(observer);
  }
}
