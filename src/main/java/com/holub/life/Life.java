package com.holub.life;

import com.holub.life.system.Clock;
import com.holub.life.system.Universe;
import com.holub.life.ui.UniversePanel;
import java.awt.BorderLayout;
import javax.swing.JFrame;

/**
 * An implementation of Conway's Game of Life.
 */

public final class Life extends JFrame {

  /**
   * Life() makes a JFrame for the game and initialize components.
   */
  private Life() {
    super("The Game of Life. "
        + "(c)2003 Allen I. Holub <https://www.holub.com>");
    Universe universe = new Universe();
    UniversePanel universePanel = new UniversePanel(universe);
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    universePanel.setFrame(this);

    pack();
    setVisible(true);
  }

  /**
   * Life.main() provides an entrypoint for this program.
   *
   * @param arguments command line arguments. Not used.
   */
  public static void main(final String[] arguments) {
    new Life();
  }
}
