package com.holub.life.ui.menu;

import com.holub.life.system.TickSystem;
import javax.swing.JMenuItem;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ClockMenuItem implements MenuItem {
  final TickSystem tickSystem;

  /**
   * Create the menu that controls the clock speed and put it onto the menu
   * site.
   * @param menuSite
   */
  @Override
  public void register(final MenuSite menuSite) {
    // First set up a single listener that will handle all the
    // menu-selection events except "Exit"
    menuSite.addLine(this, "Go", "Halt", e -> { tickSystem.stop(); });
    menuSite.addLine(this, "Go", "Tick (Single Step)", e-> { tickSystem.tick();});
    for (String speedName : tickSystem.getSupportedSpeeds()) {
      menuSite.addLine(this, "Go", speedName, e->{tickSystem.setSpeed(((JMenuItem)e.getSource()).getName());});
    }
  }
}
