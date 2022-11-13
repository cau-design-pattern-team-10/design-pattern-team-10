package com.holub.life;

import com.holub.model.Universe;
import com.holub.system.Clock;
import com.holub.ui.UniversePanel;
import com.holub.ui.menu.ClockMenu;
import com.holub.ui.menu.MenuSite;
import java.awt.BorderLayout;
import javax.swing.JComponent;
import javax.swing.JFrame;

/*******************************************************************
 * An implemenation of Conway's Game of Life.
 *
 * @include /etc/license.txt
 */

public final class Life extends JFrame {

  private static JComponent universe;

  public static void main(String[] arguments) {
    new Life();
  }

  private Life() {
    super("The Game of Life. "
        + "(c)2003 Allen I. Holub <http://www.holub.com>");

    // Must establish the MenuSite very early in case
    // a subcomponent puts menus on it.
    MenuSite menuSite = new MenuSite();
    menuSite.establish(this);    //{=life.java.establish}
    Clock clock = new Clock();
    ClockMenu clockMenu = new ClockMenu(clock, menuSite);

    setDefaultCloseOperation(EXIT_ON_CLOSE);
    getContentPane().setLayout(new BorderLayout());
    Universe universe = new Universe(clock);
    getContentPane().add(new UniversePanel(universe, menuSite), BorderLayout.CENTER); //{=life.java.install}

    pack();
    setVisible(true);
  }
}
