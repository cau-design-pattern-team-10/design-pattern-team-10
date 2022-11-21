package com.holub.life;

import com.holub.system.Universe;
import com.holub.system.Clock;
import com.holub.ui.UniversePanel;
import com.holub.ui.menu.ClockMenuItem;
import com.holub.ui.menu.MenuSite;
import java.awt.BorderLayout;
import javax.swing.JFrame;

/*******************************************************************
 * An implemenation of Conway's Game of Life.
 *
 * @include /etc/license.txt
 */

public final class Life extends JFrame {

  public static void main(String[] arguments) {
    new Life();
  }

  private Life() {
    super("The Game of Life. "
        + "(c)2003 Allen I. Holub <http://www.holub.com>");
    // Define data plane
    Clock clock = new Clock();
    Universe universe = new Universe(clock);

    // Define ui plane
    MenuSite menuSite = new MenuSite();
    menuSite.establish(this);    //{=life.java.establish}
    UniversePanel universePanel = new UniversePanel(universe, menuSite);
    ClockMenuItem clockMenu = new ClockMenuItem(clock);
    menuSite.register(clockMenu);

    setDefaultCloseOperation(EXIT_ON_CLOSE);
    getContentPane().setLayout(new BorderLayout());
    getContentPane().add(universePanel, BorderLayout.CENTER); //{=life.java.install}

    pack();
    setVisible(true);
  }
}
