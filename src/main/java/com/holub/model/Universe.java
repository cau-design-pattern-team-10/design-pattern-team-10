package com.holub.model;

import com.holub.io.Files;
import com.holub.life.Storable;
import com.holub.system.Clock;
import com.holub.tools.Observable;
import com.holub.tools.Observer;
import java.awt.Point;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JOptionPane;

public class Universe implements Observable {
  private List<Observer> observers;
  private final Clock clock;
  public final Cell outermostCell;
  /**
   * The default height and width of a Neighborhood in cells. If it's too big, you'll run too slowly
   * because you have to update the entire block as a unit, so there's more to do. If it's too
   * small, you have too many blocks to check. I've found that 8 is a good compromise.
   */
  private static final int DEFAULT_GRID_SIZE = 8;

  /**
   * The size of the smallest "atomic" cell---a Resident object. This size is extrinsic to a
   * Resident (It's passed into the Resident's "draw yourself" method.
   */
  public static final int DEFAULT_CELL_SIZE = 8;

  public Universe(Clock clock) {
    this.observers = new LinkedList<>();
    this.clock = clock;
    Neighborhood neighborhood = new Neighborhood
        (DEFAULT_GRID_SIZE,
            new Neighborhood
                (DEFAULT_GRID_SIZE,
                    new Resident()
                )
        );
    outermostCell = neighborhood;

    clock.addClockListener //{=Universe.clock.subscribe}
        (new Clock.Listener() {
          public void tick() {
            // TODO: DUMMY to static final
            Cell DUMMY = new DummyCell();
            if (outermostCell.figureNextState
                (DUMMY, DUMMY, DUMMY, DUMMY,
                    DUMMY, DUMMY, DUMMY, DUMMY
                )
            ) {
              if (outermostCell.transition()) {
                //refreshNow();
                update();
              }
            }
          }
         }
        );
  }

  public void clear() {
    outermostCell.clear();
  }

  public void doLoad() {
    try {
      FileInputStream in = new FileInputStream(
          Files.userSelected(".", ".life", "Life File", "Load"));

      clock.stop();    // stop the game and
      outermostCell.clear();      // clear the board.

      Storable memento = outermostCell.createMemento();
      memento.load(in);
      outermostCell.transfer(memento, new Point(0, 0), Cell.LOAD);

      in.close();
    } catch (IOException theException) {
      JOptionPane.showMessageDialog(null, "Read Failed!",
          "The Game of Life", JOptionPane.ERROR_MESSAGE);
    }
    update();
  }

  public void doStore() {
    try {
      FileOutputStream out = new FileOutputStream(
          Files.userSelected(".", ".life", "Life File", "Write"));

      clock.stop();    // stop the game

      Storable memento = outermostCell.createMemento();
      outermostCell.transfer(memento, new Point(0, 0), Cell.STORE);
      memento.flush(out);

      out.close();
    } catch (IOException theException) {
      JOptionPane.showMessageDialog(null, "Write Failed!",
          "The Game of Life", JOptionPane.ERROR_MESSAGE);
    }
  }

  public int widthInCells() {
    return outermostCell.widthInCells();
  }

  @Override
  public void update() {
    for (Observer observer : observers) {
      observer.detectUpdate(this);
    }
  }

  @Override
  public void attach(Observer observer) {
    observers.add(observer);
  }

  @Override
  public void detach(Observer observer) {
    observers.remove(observer);
  }
}
