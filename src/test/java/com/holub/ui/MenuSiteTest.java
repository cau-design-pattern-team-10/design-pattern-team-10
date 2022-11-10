package com.holub.ui;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.junit.jupiter.api.Test;

class MenuSiteTest extends JFrame {

  static MenuSiteTest instance; // = new Test();
  static boolean isDisabled1 = false;
  static boolean isDisabled2 = false;

  MenuSiteTest() {
    setSize(400, 200);
    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        System.exit(1);
      }
    });
    MenuSite.establish(this);
    show();
  }

  //------------------------------------------------------------
  static class RemoveListener implements ActionListener {

    public void actionPerformed(ActionEvent e) {
      MenuSite.removeMyMenus(instance);
    }
  }
  //------------------------------------------------------------

  @Test()
  void menuSiteTest()
      throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException, IOException {
    com.holub.tools.Log.toScreen("com.holub.ui");
    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

    instance = this;

    // Create a generic reporter.

    ActionListener reportIt = new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        JMenuItem item = (JMenuItem) (e.getSource());
        System.out.println(item.getText());
      }
    };

    // Create the File menu first.

    ActionListener terminator = new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        System.exit(0);
      }
    };

    // Make the file menu with it's own ID so that the removal
    // test in the main menu doesn't remove it.

    Object fileId = new Object();
    MenuSite.addMenu(fileId, "File");
    MenuSite.addLine(fileId, "File", "Quit", terminator);
    MenuSite.addLine(fileId, "File", "Bye", terminator);

    // Now, make a few more menus.

    MenuSite.addMenu(instance, "Main");
    MenuSite.addLine(instance, "Main", "Add Line Item to Menu", new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        MenuSite.addLine(instance, "Main", "Remove Main and Help menus", new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            MenuSite.removeMyMenus(instance);
          }
        });
      }
    });

    //---------------------------------------------------------
    MenuSite.addLine(instance, "Main", "-", null);
    //---------------------------------------------------------
    final Object disable1 = new Object();

    MenuSite.addLine(instance, "Main", "Toggle1", new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        isDisabled1 = !isDisabled1;
        MenuSite.setEnable(disable1, !isDisabled1);
        MenuSite.getMyMenuItem(instance, "Main", "Toggle1")
            .setText(isDisabled1 ? "Enable following Item" : "Disable following Item");

      }
    });
    MenuSite.getMyMenuItem(instance, "Main", "Toggle1").setText("Disable following Item");

    MenuSite.addLine(disable1, "Main", "Disableable", reportIt);

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    final Object disable2 = new Object();

    MenuSite.addLine(instance, "Main", "Toggle2", new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        isDisabled2 = !isDisabled2;
        MenuSite.setEnable(disable2, !isDisabled2);
        MenuSite.getMyMenuItem(instance, "Main", "Toggle2")
            .setText(isDisabled2 ? "Enable following Item" : "Disable following Item");
      }
    });
    MenuSite.getMyMenuItem(instance, "Main", "Toggle2").setText("Disable following Item");
    MenuSite.addLine(disable2, "Main", "Disableable", reportIt);

    //--------------------------------------------------------

    // Check that a single line item can be removed

    final Object id = new Object();

    MenuSite.addLine(id, "Main", "-", null);
    MenuSite.addLine(id, "Main", "Remove this item & separator line", new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        MenuSite.removeMyMenus(id);
      }
    });

    // Check out submenus. Create two of them, one in two
    // steps and the other in a single step. Then add items
    // that remove the submenus to make sure that removal works
    // correctly.

    MenuSite.addLine(instance, "Main", "-", null);
    MenuSite.addLine(instance, "Main:Submenu1", "Submenu One Item", reportIt);
    MenuSite.addLine(instance, "Main:Submenu2", "Submenu Two Item", reportIt);
    MenuSite.addLine(instance, "Main:Submenu3", "Submenu Three Item", reportIt);
    MenuSite.addLine(instance, "Main:Submenu2:SubSubmenu2", "Sub-Submenu Two Item", reportIt);

    MenuSite.addLine(instance, "Main:Submenu3:SubSubmenu3", "Sub-Submenu Three Item", reportIt);

    MenuSite.addLine(instance, "Main:Submenu3:SubSubmenu3:SubSubSubmenu3",
        "Sub-Sub-Submenu Three Item", reportIt);

    MenuSite.addLine(instance, "Main", "-", null);

    // Check that the map file works correctly.
    // Items 5 and 6 are deliberately malformed in the map
    // file and will cause an error to be logged.
    // item.7 doesn't exist in the file.

    Path currentRelativePath = Paths.get("src/test/java/com/holub/ui/test/menu.map.txt");
    System.out.println(currentRelativePath.toAbsolutePath());
    MenuSite.mapNames(new URL("file:" + currentRelativePath.toAbsolutePath()));

    MenuSite.addLine(instance, "Main", "item.1", reportIt);
    MenuSite.addLine(instance, "Main", "item.2", reportIt);
    MenuSite.addLine(instance, "Main", "item.3", reportIt);
    MenuSite.addLine(instance, "Main", "item.4", reportIt);
    MenuSite.addLine(instance, "Main", "item.5", reportIt);
    MenuSite.addLine(instance, "Main", "item.6", reportIt);
    MenuSite.addLine(instance, "Main", "item.7", reportIt);

    // Create a help menu. Do it in the middle of things
    // to make sure that it ends up on the far right.
    // Use all three mechanisms for adding menu items directly
    // using the menu's "name," and using the menu's "text").

    MenuSite.addLine(instance, "Help", "Get Help", reportIt);

    // Create a second "requester" and have it add a Removal
    // menu with the name RemovalMenu. Picking that menu
    // will remove only the menu for the current requester.
    // Do this after doing the help menu to make sure that
    // it's inserted in the right place.

    final Object x = new Object();
    MenuSite.addLine(x, "Removal", "Select to Remove Removal menu", new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        MenuSite.removeMyMenus(x);
      }
    });
  }

}