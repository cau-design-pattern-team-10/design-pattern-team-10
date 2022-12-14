package com.holub.life.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.holub.life.model.cell.Cell;
import com.holub.life.model.cell.Cell.Memento;
import com.holub.life.system.Universe;
import com.holub.tools.Storable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import org.junit.jupiter.api.Test;

public class LifeTest {

  @Test
  void TestFiles() throws IOException {
    TestCase[] testCases = {
        new TestCase("Beacon", 3),
        new TestCase("Bee_hive", 2),
        new TestCase("Blinker", 3),
        new TestCase("Block", 2),
        new TestCase("Boat", 2),
        new TestCase("Glider", 5),
        new TestCase("HWSS", 5),
        new TestCase("Loaf", 2),
        new TestCase("LWSS", 5),
        new TestCase("MWSS", 5),
        new TestCase("Penta_decathlon", 16),
        new TestCase("Pulsar", 4),
        new TestCase("Toad", 3),
        new TestCase("Tub", 2),
    };

    for (TestCase tc : testCases) {
      Universe universe = new Universe();
      Memento[] currentState = new Memento[tc.num + 1];
      Memento[] nextState = new Memento[tc.num + 1];
      for (int step = 1; step <= tc.num; step++) {

        File file = new File("testcases/" + tc.name + "/" + step);

        FileInputStream in = new FileInputStream(file);

        Storable memento = universe.getOutermostCell().createMemento();
        memento.load(in);
        universe.getOutermostCell().transfer(memento, new Point(0, 0), Cell.LOAD);
        in.close();

        currentState[step] = (Memento) universe.getOutermostCell().createMemento();

        // tick
        universe.getTickSystem().tick();
        nextState[step] = (Memento) universe.getOutermostCell().createMemento();

        if (step != 1) {
          assertEquals(currentState[step], nextState[step - 1], "At " + tc.name + " step " + step);
        }
      }
    }
  }
}

class TestCase {
  final String name;
  final int num;
  public TestCase(String name, int num) {
    this.name = name;
    this.num = num;
  }
}
