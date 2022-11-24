package com.holub.system;

import com.holub.io.Files;
import com.holub.model.Point;
import com.holub.model.cell.Cell;
import com.holub.model.cell.DummyCell;
import com.holub.model.cell.NearestCellsDTO.NearestCellsDTOBuilder;
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
   *
   * @param c
   */
  public Universe(final Clock c) {
    this.observers = new LinkedList<>();
    this.clock = c;
    Neighborhood neighborhood = new Neighborhood(DEFAULT_GRID_SIZE,
            new Neighborhood(DEFAULT_GRID_SIZE, new Resident()));
    outermostCell = neighborhood;

    clock.addClockListener(() -> {
      Cell dummy = DummyCell.getInstance();
      boolean nextState = outermostCell.figureNextState(
          new NearestCellsDTOBuilder()
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
   * @return outermostCell;
   */
  public Cell getOutermostCell() {
    return outermostCell;
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
