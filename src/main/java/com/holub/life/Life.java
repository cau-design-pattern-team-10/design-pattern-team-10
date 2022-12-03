package com.holub.life;

import com.holub.system.Clock;
import com.holub.system.Clock.Listener;
import com.holub.system.TickSystem;
import com.holub.system.Universe;
import com.holub.ui.StatusBar;
import com.holub.ui.UniversePanel;
import com.holub.ui.menu.ClockMenuItem;
import com.holub.ui.menu.MenuSite;
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
    Clock clock = new Clock();
    Universe universe = new Universe(clock);

    MenuSite menuSite = new MenuSite();
    menuSite.establish(this);
    UniversePanel universePanel = new UniversePanel(universe, menuSite);
    TickSystem tickSystem = new TickSystem(clock);
    ClockMenuItem clockMenu = new ClockMenuItem(tickSystem);
    menuSite.register(clockMenu);

    StatusBar statusBar = new StatusBar();
    tickSystem.attach(statusBar);
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    getContentPane().setLayout(new BorderLayout());
    getContentPane().add(universePanel, BorderLayout.CENTER);
    getContentPane().add(statusBar, BorderLayout.SOUTH);

    pack();
    setVisible(true);
  }

  /**
   * Life.main() provides an entrypoint for this program.
   * @param arguments command line arguments. Not used.
   */
  public static void main(final String[] arguments) {
    new Life();
  }
}
