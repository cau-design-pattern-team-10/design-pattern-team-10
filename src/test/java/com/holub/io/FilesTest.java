package com.holub.io;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.FileNotFoundException;
import org.junit.jupiter.api.Test;

class FilesTest {

  @Test
  void fileTest() {
    try {
      File f = Files.userSelected(".", ".test", "Test File", "Select!");
      System.out.println("Selected " + f.getName());
    } catch (FileNotFoundException e) {
      System.out.println("No file selected");
    }
    System.exit(0); // Required to stop AWT thread & shut down.

  }
}