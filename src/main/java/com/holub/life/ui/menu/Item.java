package com.holub.life.ui.menu;

import java.awt.Component;
import java.util.LinkedList;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.MenuElement;

/*** ***********************************************************
 * An Item makes the association between a line item or
 * submenu and the MenuBar or Menu that contains it. You can
 * ask an Item to add or remove itself from its container.
 * All the weirdness associated with help menus is handled
 * here.
 */
public class Item {

  /**
   *
   */
  private MenuSite menuSite = null;
  /**
   *
   */
  private final Component item;
  /**
   * of JMenu or of JMenuItem's parent.
   */
  private final String parentSpecification;

  /**
   * JMenu or JMenuBar.
   */
  private MenuElement parent;
  /**
   *
   */
  private final boolean isHelpMenu;

  /*** Create a new Item. If the JMenuItem's name is the
   *  string "help" then it's assumed to be the help menu and
   *  is treated specially. Note that several help menus can
   *  be added to a site: They'll be stacked up at the far
   *  right in the reverse order of addition. Similarly
   *  file menus are stacked up at the far left.
   *
   * @param i   the item being added
   * @param p   The menu bar or a menu that contains the current item. Must
   *             be a JMenuBar or a JMenu.
   * @param ps
   */

  public Item(final Component i, final MenuElement p,
      final String ps) {
    if (p == null) {
      throw new AssertionError();
    }
    if (!(p instanceof JMenu) && !(p instanceof JMenuBar)) {
      throw new AssertionError("Parent must be JMenu or JMenuBar");
    }

    this.item = i;
    this.parent = p;
    this.parentSpecification = ps;
    this.isHelpMenu =
        (item instanceof JMenuItem)
            && (item.getName().compareToIgnoreCase("help") == 0);

    assert valid();
  }

  /**
   *
   * @return string
   */
  public String toString() {
    StringBuilder b = new StringBuilder(parentSpecification);
    if (item instanceof JMenuItem) {
      JMenuItem i = (JMenuItem) item;
      b.append(":");
      b.append(i.getName());
      b.append(" (");
      b.append(i.getText());
      b.append(")");
    }
    return b.toString();
  }

  /**
   *
   * @return menu item is valid
   */
  private boolean valid() {
    assert item != null : "item is null";
    assert parent != null : "parent is null";
    return true;
  }

  /**
   *
   * @param specifier
   * @return check equal using specifier
   */
  public boolean specifiedBy(final String specifier) {
    return parentSpecification.equals(specifier);
  }

  /**
   *
   * @return item
   */
  public Component item() {
    return item;
  }

  /*** ******************************************************
   * Attach a menu item to it's parent (either a menu
   * bar or a menu). Items are added at the end of the
   * <code>menuBarContents</code> list unless a help
   * menu exists, in which case items are added at
   * the penultimate position.
   * @param ms
   */
  public final void attachYourselfToYourParent(final MenuSite ms) {
    assert valid();

    this.menuSite = ms;
    LinkedList<Item> menuBarContents = menuSite.getMenuBarContents();
    if (parent instanceof JMenu) {
      ((JMenu) parent).add(item);
    } else if (menuBarContents.isEmpty()) {
      menuBarContents.add(this);
      ((JMenuBar) parent).add(item);
    } else {
      Item last = (Item) (menuBarContents.getLast());
      if (!last.isHelpMenu) {
        menuBarContents.addLast(this);
        ((JMenuBar) parent).add(item);
      } else  {
        menuBarContents.removeLast();
        menuBarContents.add(this);
        menuBarContents.add(last);

        if (parent == menuSite.getMenuBar()) {
          parent = regenerateMenuBar();
        }
      }
    }
  }

  /*** ******************************************************
   * Remove the current menu item from its parent
   * (either a menu bar or a menu). The Item is invalid
   * after it's detached, and should be discarded.
   */
  public void detachYourselfFromYourParent() {
    assert valid();

    if (parent instanceof JMenu) {
      ((JMenu) parent).remove(item);
    } else {
      JMenuBar menuBar = menuSite.getMenuBar();
      LinkedList<Item> menuBarContents = menuSite.getMenuBarContents();
      menuBar.remove(item);
      menuBarContents.remove(this);
      regenerateMenuBar(); // without me on it

      parent = null;
      menuSite = null;
    }
  }

  /*** ******************************************************
   * Set or reset the "disabled" state of a menu item.
   * @param on
   */
  public void setEnableAttribute(final boolean on) {
    if (item instanceof JMenuItem) {
      JMenuItem i = (JMenuItem) this.item;
      i.setEnabled(on);
    }
  }

  /*** ******************************************************
   * Replace the old menu bar with a new one that reflects
   * the current state of the <code>menuBarContents</code>
   * list.
   * @return generatedMenuBar
   */
  private JMenuBar regenerateMenuBar() {
    if (!valid()) {
      throw new AssertionError();
    }

    // Create the new menu bar and populate it from
    // the current-contents list.

    JMenuBar menuBar = new JMenuBar();
    menuSite.setMenuBar(menuBar);
    for (Item i : menuSite.getMenuBarContents()) {
      menuBar.add(i.item);
    }

    // Replace the old menu bar with the new one.
    // Calling setVisible causes the menu bar to be
    // redrawn with a minimum amount of flicker. Without
    // it, the redraw doesn't happen at all.

    JFrame menuFrame = menuSite.getMenuFrame();
    menuFrame.setJMenuBar(menuBar);
    menuFrame.setVisible(true);
    return menuBar;
  }
}
