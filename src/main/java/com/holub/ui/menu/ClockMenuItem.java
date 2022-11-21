package com.holub.ui.menu;

import com.holub.system.Clock;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenuItem;

public class ClockMenuItem implements MenuItem {

  Clock clock;

  public ClockMenuItem(Clock clock) {
    this.clock = clock;
  }

  /**
   * Create the menu that controls the clock speed and put it onto the menu site.
   */
  @Override
  public void register(MenuSite menuSite) {
    // First set up a single listener that will handle all the
    // menu-selection events except "Exit"

    ActionListener modifier =                  //{=startSetup}
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            String name = ((JMenuItem) e.getSource()).getName();
            char toDo = name.charAt(0);

            if (toDo == 'T') {
              clock.tick();              // single tick
            } else {
              clock.startTicking(toDo == 'A' ? 500 :    // agonizing
                  toDo == 'S' ? 150 :    // slow
                      toDo == 'M' ? 70 :    // medium
                          toDo == 'F' ? 30 : 0); // fast
            }
          }
        };
    // {=midSetup}
    menuSite.addLine(this, "Go", "Halt", modifier);
    menuSite.addLine(this, "Go", "Tick (Single Step)", modifier);
    menuSite.addLine(this, "Go", "Agonizing", modifier);
    menuSite.addLine(this, "Go", "Slow", modifier);
    menuSite.addLine(this, "Go", "Medium", modifier);
    menuSite.addLine(this, "Go", "Fast", modifier); // {=endSetup}
  }  //{=endCreateMenus}
}
