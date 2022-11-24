package com.holub.ui.menu;

import com.holub.system.Clock;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JMenuItem;

public class ClockMenuItem implements MenuItem {

  /**
   *
   */
  private static final int AGONIZING_MS = 500;
  /**
   *
   */
  private static final int SLOW_MS = 150;
  /**
   *
   */
  private static final int MEDIUM_MS = 70;
  /**
   *
   */
  private static final int FAST_MS = 30;
  /**
   *
   */
  private Clock clock;

  /**
   * @param c
   */
  public ClockMenuItem(final Clock c) {
    this.clock = c;
  }

  /**
   * Create the menu that controls the clock speed and put it onto the menu
   * site.
   * @param menuSite
   */
  @Override
  public void register(final MenuSite menuSite) {
    // First set up a single listener that will handle all the
    // menu-selection events except "Exit"

    ActionListener modifier = e -> {
      String name = ((JMenuItem) e.getSource()).getName();
      char toDo = name.charAt(0);

      if (toDo == 'T') {
        clock.tick();              // single tick
      } else {
        Map<Character, Integer> tickMap = new HashMap<>();
        tickMap.put('A', AGONIZING_MS); // agonizing
        tickMap.put('S', SLOW_MS); // slow
        tickMap.put('M', MEDIUM_MS); // medium
        tickMap.put('F', FAST_MS); // fast
        Integer speed = tickMap.get(toDo);
        if (speed == null) {
          speed = 0;
        }
        clock.startTicking(speed); // fast
      }
    };
    menuSite.addLine(this, "Go", "Halt", modifier);
    menuSite.addLine(this, "Go", "Tick (Single Step)", modifier);
    menuSite.addLine(this, "Go", "Agonizing", modifier);
    menuSite.addLine(this, "Go", "Slow", modifier);
    menuSite.addLine(this, "Go", "Medium", modifier);
    menuSite.addLine(this, "Go", "Fast", modifier);
  }
}
