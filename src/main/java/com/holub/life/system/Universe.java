package com.holub.life.system;

import com.holub.io.Files;
import com.holub.life.model.Point;
import com.holub.life.model.cell.Cell;
import com.holub.life.model.cell.Neighborhood;
import com.holub.life.model.cell.Resident;
import com.holub.tools.Observable;
import com.holub.tools.Observer;
import com.holub.tools.Storable;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import lombok.Getter;

public class Universe implements Observable {

  /**
   * The default height and width of a Neighborhood in cells. If it's too big, you'll run too slowly
   * because you have to update the entire block as a unit, so there's more to do. If it's too
   * small, you have too many blocks to check. I've found that 8 is a good compromise.
   */
  private static final int DEFAULT_GRID_SIZE = 8;
  @Getter
  private final Neighborhood outermostCell;
  @Getter
  private final Clock clock;
  private final List<Observer> observers;
  private final ResidentService residentService;
  private Stack<Storable> pastTickStore = new Stack<>();
  @Getter
  private TickSystem tickSystem;

  public Universe() {
    this.observers = new LinkedList<>();
    this.clock =  new Clock();
    this.tickSystem = new TickSystem(clock);
    this.residentService = ResidentService.getInstance();
    outermostCell = new Neighborhood(DEFAULT_GRID_SIZE,
            new Neighborhood(DEFAULT_GRID_SIZE, new Resident()));

    Cell[][] neighborhood = outermostCell.getGrid();
    for (int x = 0; x < DEFAULT_GRID_SIZE; x ++) {
      for (int y = 0; y < DEFAULT_GRID_SIZE; y ++) {
        Cell[][] residents = ((Neighborhood)neighborhood[y][x]).getGrid();
        for (int innerX = 0; innerX < DEFAULT_GRID_SIZE; innerX ++) {
          for (int innerY = 0; innerY < DEFAULT_GRID_SIZE; innerY ++) {
            residentService.register((Resident)residents[innerY][innerX],
                Point.builder().x(x * DEFAULT_GRID_SIZE + innerX)
                    .y(y * DEFAULT_GRID_SIZE + innerY).build());
          }
        }
      }
    }
    pastTickStore.push(outermostCell.createMemento());

    clock.addClockListener(() -> {
      boolean nextState = outermostCell.figureNextState();
      pastTickStore.push(outermostCell.createMemento());
      if (nextState && outermostCell.transition()) {
        update();
      }
    });
  }

  /**
   *
   */
  public void clear() {
    outermostCell.clear();
  }

  /**
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
  public void doOverlapLoad() throws IOException {
    FileInputStream in = new FileInputStream(
        Files.userSelected(".", ".life", "Life File", "Load"));

    clock.stop();    // stop the game and

    Storable memento = outermostCell.createMemento();
    memento.load(in);
    outermostCell.transfer(memento, new Point(0, 0), Cell.LOAD);

    in.close();
    update();
  }

  /**
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
   * @throws IOException
   */
  public void doRollback() throws IOException {
    if(!pastTickStore.isEmpty() && pastTickStore.size() > 1) {
      outermostCell.clear();
      outermostCell.transfer(pastTickStore.pop(), new Point(0, 0), Cell.LOAD);
      update();
    }
  }

  public void doOverlapLoad() throws IOException {
    FileInputStream in = new FileInputStream(
        Files.userSelected(".", ".life", "Life File", "Load"));

    clock.stop();    // stop the game and

    Storable memento = outermostCell.createMemento();
    memento.load(in);
    outermostCell.transfer(memento, new Point(0, 0), Cell.LOAD);

    in.close();
    update();
  }

  /**
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
   * @param observer
   */
  @Override
  public void attach(final Observer observer) {
    observers.add(observer);
  }

  /**
   * @param observer
   */
  @Override
  public void detach(final Observer observer) {
    observers.remove(observer);
  }

}
