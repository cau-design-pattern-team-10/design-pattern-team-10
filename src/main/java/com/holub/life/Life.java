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
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
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
    addKeyListener(new KeyAdapter() {
      public void keyPressed(KeyEvent e) {
        System.out.println(e);
        switch (e.getKeyCode()) {
          case KeyEvent.VK_RIGHT:
            tickSystem.tick();
            break;
          case KeyEvent.VK_LEFT:
          case KeyEvent.VK_U:
            try {
              universe.doRollback();
            } catch (IOException ex) {
              throw new RuntimeException(ex);
            }
            break;
          case KeyEvent.VK_SPACE:
            if(tickSystem.isRunning()) {
              tickSystem.stop();
            } else {
              tickSystem.resume();
            }
        }
      }
    });

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
   *
   * @param arguments command line arguments. Not used.
   */
  public static void main(final String[] arguments) {
    new Life();
  }
}
