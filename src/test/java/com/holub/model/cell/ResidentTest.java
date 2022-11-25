package com.holub.model.cell;

import com.holub.tools.Observable;
import com.holub.tools.Observer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ResidentTest {
  @Test
  void TestCreate() {
    Resident resident = new Resident();
    Assertions.assertFalse(resident.isAlive());
  }

  @Test
  void TestToggle() {
    Resident resident = new Resident();
    boolean previousState = resident.isAlive();
    resident.toggle();
    Assertions.assertNotEquals(previousState, resident.isAlive());
  }

  @Test
  void TestUpdate() {
    Resident resident = new Resident();
    resident.attach(o -> Assertions.assertEquals(resident, o));
    resident.update();
  }

  @Test
  void TestToggleUpdate() {
    Resident resident = new Resident();
    boolean previousState = resident.isAlive();
    resident.attach(o -> Assertions.assertEquals(previousState, resident.isAlive()));
    resident.toggle();
  }
}
