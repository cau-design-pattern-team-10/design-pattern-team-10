package com.holub.ui.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.junit.jupiter.api.Test;

class MenuSiteTest extends JFrame {

  static MenuSiteTest instance; // = new Test();
  static boolean isDisabled1 = false;
  static boolean isDisabled2 = false;

  MenuSite menuSite;

  MenuSiteTest() {
    this.menuSite = new MenuSite();
    setSize(400, 200);
    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        System.exit(1);
      }
    });
    menuSite.establish(this);
    show();
  }

  //------------------------------------------------------------
  static class RemoveListener implements ActionListener {
    MenuSite menuSite;
    RemoveListener(MenuSite menuSite) {
      this.menuSite = menuSite;
    }

    public void actionPerformed(ActionEvent e) {
      menuSite.removeMyMenus(instance);
    }
  }
  //------------------------------------------------------------

  @Test()
  void menuSiteTest() throws UnsupportedLookAndFeelException,
      ClassNotFoundException, InstantiationException, IllegalAccessException,
      IOException {
    com.holub.tools.Log.toScreen("com.holub.ui");
    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

    instance = this;

    // Create a generic reporter.

    ActionListener reportIt = e -> {
      JMenuItem item = (JMenuItem) (e.getSource());
      System.out.println(item.getText());
    };

    // Create the File menu first.

    ActionListener terminator = e -> System.exit(0);

    // Make the file menu with it's own ID so that the removal
    // test in the main menu doesn't remove it.

    Object fileId = new Object();
    menuSite.addMenu(fileId, "File");
    menuSite.addLine(fileId, "File", "Quit", terminator);
    menuSite.addLine(fileId, "File", "Bye", terminator);

    // Now, make a few more menus.

    menuSite.addMenu(instance, "Main");
    menuSite.addLine(instance, "Main", "Add Line Item to Menu",
        e -> menuSite.addLine(instance, "Main", "Remove Main and Help menus",
            e1 -> menuSite.removeMyMenus(instance)));

    //---------------------------------------------------------
    menuSite.addLine(instance, "Main", "-", null);
    //---------------------------------------------------------
    final Object disable1 = new Object();

    menuSite.addLine(instance, "Main", "Toggle1", e -> {
      isDisabled1 = !isDisabled1;
      menuSite.setEnable(disable1, !isDisabled1);
      Objects.requireNonNull(menuSite.getMyMenuItem(instance, "Main", "Toggle1"))
        .setText(isDisabled1 ? "Enable following Item" : "Disable following Item");

    });
    Objects.requireNonNull(menuSite.getMyMenuItem(instance,
        "Main", "Toggle1")).setText("Disable following Item");

    menuSite.addLine(disable1, "Main", "Disabled", reportIt);

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    final Object disable2 = new Object();

    menuSite.addLine(instance, "Main", "Toggle2", e -> {
      isDisabled2 = !isDisabled2;
      menuSite.setEnable(disable2, !isDisabled2);
      menuSite.getMyMenuItem(instance, "Main", "Toggle2")
        .setText(isDisabled2 ? "Enable following Item" : "Disable following Item");
    });
    Objects.requireNonNull(menuSite.getMyMenuItem(instance,
        "Main", "Toggle2")).setText("Disable following Item");
    menuSite.addLine(disable2, "Main", "Disabled", reportIt);

    //--------------------------------------------------------

    // Check that a single line item can be removed

    final Object id = new Object();

    menuSite.addLine(id, "Main", "-", null);
    menuSite.addLine(id, "Main", "Remove this item & separator line",
        e -> menuSite.removeMyMenus(id));

    // Check out submenus. Create two of them, one in two
    // steps and the other in a single step. Then add items
    // that remove the submenus to make sure that removal works
    // correctly.

    menuSite.addLine(instance, "Main", "-", null);
    menuSite.addLine(instance, "Main:Submenu1", "Submenu One Item", reportIt);
    menuSite.addLine(instance, "Main:Submenu2", "Submenu Two Item", reportIt);
    menuSite.addLine(instance, "Main:Submenu3", "Submenu Three Item", reportIt);
    menuSite.addLine(instance, "Main:Submenu2:SubSubmenu2", "Sub-Submenu Two Item", reportIt);

    menuSite.addLine(instance, "Main:Submenu3:SubSubmenu3", "Sub-Submenu Three Item", reportIt);

    menuSite.addLine(instance, "Main:Submenu3:SubSubmenu3:SubSubSubmenu3",
        "Sub-Sub-Submenu Three Item", reportIt);

    menuSite.addLine(instance, "Main", "-", null);

    // Check that the map file works correctly.
    // Items 5 and 6 are deliberately malformed in the map
    // file and will cause an error to be logged.
    // item.7 doesn't exist in the file.

    Path currentRelativePath = Paths.get("src/test/java/com/holub/ui/test/menu.map.txt");
    System.out.println(currentRelativePath.toAbsolutePath());
    menuSite.mapNames(new URL("file:" + currentRelativePath.toAbsolutePath()));

    menuSite.addLine(instance, "Main", "item.1", reportIt);
    menuSite.addLine(instance, "Main", "item.2", reportIt);
    menuSite.addLine(instance, "Main", "item.3", reportIt);
    menuSite.addLine(instance, "Main", "item.4", reportIt);
    menuSite.addLine(instance, "Main", "item.5", reportIt);
    menuSite.addLine(instance, "Main", "item.6", reportIt);
    menuSite.addLine(instance, "Main", "item.7", reportIt);

    // Create a help menu. Do it in the middle of things
    // to make sure that it ends up on the far right.
    // Use all three mechanisms for adding menu items directly
    // using the menu's "name," and using the menu's "text").

    menuSite.addLine(instance, "Help", "Get Help", reportIt);

    // Create a second "requester" and have it add a Removal
    // menu with the name RemovalMenu. Picking that menu
    // will remove only the menu for the current requester.
    // Do this after doing the help menu to make sure that
    // it's inserted in the right place.

    final Object x = new Object();
    menuSite.addLine(x, "Removal", "Select to Remove Removal menu",
        e -> menuSite.removeMyMenus(x));
  }

}
