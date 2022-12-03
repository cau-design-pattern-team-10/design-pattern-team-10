package com.holub.life.model.cell;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ResidentTest {
  @Test
  void testCreate() {
    Resident resident = new Resident();
    Assertions.assertFalse(resident.isAlive());
  }

  @Test
  void testToggle() {
    Resident resident = new Resident();
    boolean previousState = resident.isAlive();
    resident.toggle();
    Assertions.assertNotEquals(previousState, resident.isAlive());
  }

  @Test
  void testUpdate() {
    Resident resident = new Resident();
    resident.attach(o -> Assertions.assertEquals(resident, o));
    resident.update();
  }

  @Test
  void testToggleUpdate() {
    Resident resident = new Resident();
    boolean previousState = resident.isAlive();
    resident.attach(o ->
        Assertions.assertEquals(previousState, resident.isAlive()));
    resident.toggle();
  }
}
