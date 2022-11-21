package com.holub.ui.menu;

import java.awt.Component;
import java.util.ListIterator;
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

  // private JMenuItem  item;
  private final Component item;

  private final String parentSpecification; // of JMenu or of
  // JMenuItem's parent
  private MenuElement parent;           // JMenu or JMenuBar

  MenuSite menuSite = null;
  private final boolean isHelpMenu;

  public String toString() {
    StringBuffer b = new StringBuffer(parentSpecification);
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

  /*------------------------------------------------------------*/

  private boolean valid() {
    assert item != null : "item is null";
    assert parent != null : "parent is null";
    return true;
  }

  /*** Create a new Item. If the JMenuItem's name is the
   *  string "help" then it's assumed to be the help menu and
   *  is treated specially. Note that several help menus can
   *  be added to a site: They'll be stacked up at the far
   *  right in the reverse order of addition. Similarly
   *  file menus are stacked up at the far left.
   *
   *  @param item     the item being added
   *  @param parent   The menu bar or a menu that
   *  				 contains the current item. Must
   *  				 be a JMenuBar or a JMenu.
   */

  public Item(Component item, MenuElement parent,
      String parentSpecification) {
    assert parent != null;
    assert parent instanceof JMenu || parent instanceof JMenuBar
        : "Parent must be JMenu or JMenuBar";

    this.item = item;
    this.parent = parent;
    this.parentSpecification = parentSpecification;
    this.isHelpMenu =
        (item instanceof JMenuItem)
            && (item.getName().compareToIgnoreCase("help") == 0);

    assert valid();
  }

  public boolean specifiedBy(String specifier) {
    return parentSpecification.equals(specifier);
  }

  public Component item() {
    return item;
  }

  /*** ******************************************************
   * Attach a menu item to it's parent (either a menu
   * bar or a menu). Items are added at the end of the
   * <code>menuBarContents</code> list unless a help
   * menu exists, in which case items are added at
   * the penultimate position.
   */

  public final void attachYourselfToYourParent(MenuSite menuSite) {
    assert valid();

    this.menuSite = menuSite;
    if (parent instanceof JMenu) {
      ((JMenu) parent).add(item);
    } else if (menuSite.menuBarContents.size() <= 0) {
      menuSite.menuBarContents.add(this);
      ((JMenuBar) parent).add(item);
    } else {
      Item last = (Item) (menuSite.menuBarContents.getLast());
      if (!last.isHelpMenu) {
        menuSite.menuBarContents.addLast(this);
        ((JMenuBar) parent).add(item);
      } else  // remove the help menu, add the new
      {    // item, then put the help menu back
        // (following the new item).

        menuSite.menuBarContents.removeLast();
        menuSite.menuBarContents.add(this);
        menuSite.menuBarContents.add(last);

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
    } else // the parent's the menu bar.
    {
      JMenuBar menuBar = menuSite.getMenuBar();
      menuBar.remove(item);
      menuSite.menuBarContents.remove(this);
      regenerateMenuBar(); // without me on it

      parent = null;
      menuSite = null;
    }
  }

  /*** ******************************************************
   * Set or reset the "disabled" state of a menu item.
   */

  public void setEnableAttribute(boolean on) {
    if (item instanceof JMenuItem) {
      JMenuItem item = (JMenuItem) this.item;
      item.setEnabled(on);
    }
  }

  /*** ******************************************************
   * Replace the old menu bar with a new one that reflects
   * the current state of the <code>menuBarContents</code>
   * list.
   */
  private JMenuBar regenerateMenuBar() {
    assert valid();

    // Create the new menu bar and populate it from
    // the current-contents list.

    JMenuBar menuBar = new JMenuBar();
    menuSite.SetMenuBar(menuBar);
    ListIterator i = menuSite.menuBarContents.listIterator(0);
    while (i.hasNext()) {
      menuBar.add(((Item) (i.next())).item);
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
