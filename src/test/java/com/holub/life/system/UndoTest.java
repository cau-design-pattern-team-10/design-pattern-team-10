package com.holub.life.system;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import com.holub.life.model.Point;
import com.holub.life.model.cell.Cell;
import com.holub.life.model.cell.Neighborhood;
import org.junit.jupiter.api.Test;
import com.holub.tools.Storable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class UndoTest {

  @Test
  void doRollBackTest() throws IOException {
    try {
      Universe universe = new Universe();
      File file = new File("testcases/" + "Blinker" + "/" + "1");
      FileInputStream in = new FileInputStream(file);

      Storable memento = universe.getOutermostCell().createMemento();
      memento.load(in);
      universe.getOutermostCell().transfer(memento, new Point(0, 0), Cell.LOAD);
      in.close();

      Neighborhood originalState = universe.getOutermostCell();

      // tick
      universe.getTickSystem().tick();
      universe.doRollback();

      Neighborhood rollbackState = universe.getOutermostCell();

      assertEquals(originalState, rollbackState);
    }
    catch (IOException e) {
      fail();
    }
  }
}
