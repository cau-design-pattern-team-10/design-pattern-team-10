package com.holub.life.system;

import com.holub.tools.Publisher;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.MenuElement;
import javax.swing.MenuSelectionManager;

/***
 * The <code>Clock</code> class handles the timing of game board
 * updates. It creates its own menu (which sets the clock speed),
 * and sends notifications off to any observers every time the
 * clock "ticks."
 *
 * <h2>Revisions</h2>
 * <p>
 * 12-8-2004 AIH Added a kludge to the clock-tick handler that
 *  checks whether any menu item is active before it
 *  allows the clock to tick. This mod fixes a bug
 *  that caused the running game to overwrite any
 *  displayed menus. See {@link #menuIsActive} for
 *  details.
 */

public class Clock {

  /**
   *
   */
  private final Timer timer = new Timer();
  /**
   *
   */
  private TimerTask tick = null;
  /**
   *
   */
  private final Publisher publisher = new Publisher();


  /**
   * The clock can't be an everything-is-static singleton because
   * it creates a menu, and it can't do that until the menus
   * are established.
   */
  public Clock() {
    // do nothing.
  }

  /**
   * Start up the clock.
   *
   * @param millisecondsBetweenTicks The number of milliseconds between ticks.
   * A value of 0 indicates that the clock should be stopped.
   */

  public void startTicking(final int millisecondsBetweenTicks) {
    if (tick != null) {
      tick.cancel();
      tick = null;
    }

    if (millisecondsBetweenTicks > 0) {
      tick = new TimerTask() {
        public void run() {
          tick();
        }
      };
      timer.scheduleAtFixedRate(tick, 0, millisecondsBetweenTicks);
    }
  }

  /**
   * Stop the clock.
   */

  public void stop() {
    startTicking(0);
  }

  /**
   * Add a listener that's notified every time the clock ticks.
   * <PRE>
   * Clock.instance().addClockListener(new Clock.Listener() {
   *   public void tick() {
   *     System.out.println("tick!");
   *   }
   * });
   * </PRE>
   * @param observer
   */
  public void addClockListener(final Listener observer) {
    publisher.subscribe(observer);
  }

  /**
   * Force the clock to "tick," even if it's not time for a tick. Useful for
   * forcing a tick when the clock is stopped.
   * (Life uses this for single stepping.)
   */
  public void tick() {
    publisher.publish(subscriber -> {
          if (!menuIsActive()) {
            ((Listener) subscriber).tick();
          }
        });
  }

  /**
   * Check if any item on the menu bar has been selected. This is an incredible
   * kluge. The problem is that Swing draws the menus on the same "canvas" as
   * the main frame. As a consequence, displayed menus are overwritten by a
   * running game. Moreover, Swing provides no notification on the order of
   * "some menu item has been selected," so the only way to detect menu-bar
   * activity is to poll it. This method does just that, and returns true if
   * some menu is being displayed. The {@link #tick} method effectively
   * suspends clock ticks as long as a menu is displayed, (which slows down
   * the clock, unfortunately).
   * <p>
   * From a design-patterns perspective, this method demonstrates that a facade
   * (the MenuSite) does not provide strict isolation from the underlying
   * subsystem. I can't imagine another application that would need to know
   * whether or not the menu bar is active, so adding this menu to the MenuSite
   * would be "noise." Since the method does something that's of no relevance to
   * other MenuSite users, it makes an "end run" around the facade.
   *
   * @return isActive
   */

  private boolean menuIsActive() {
    MenuElement[] path =
        MenuSelectionManager.defaultManager().getSelectedPath();
    return (path != null && path.length > 0);
  }

  /**
   * Implement this interface to be notified about clock ticks.
   *
   * @see Clock
   */
  public interface Listener {

    /**
     *
     */
    void tick();
  }
}
