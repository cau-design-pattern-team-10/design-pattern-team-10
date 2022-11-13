package com.holub.ui;

import com.holub.model.Cell;
import com.holub.model.DummyCell;
import com.holub.model.Universe;
import com.holub.system.Clock;
import com.holub.tools.Observable;
import com.holub.tools.Observer;

import java.awt.*;
import java.io.IOException;
import javax.swing.*;
import java.awt.event.*;

/**
 * The Universe is a mediator that sits between the Swing event model and the Life classes. It is
 * also a singleton, accessed via Universe.instance(). It handles all Swing events and translates
 * them into requests to the outermost Neighborhood. It also creates the Composite Neighborhood.
 *
 * @include /etc/license.txt
 */

public class UniversePanel extends JPanel implements Observer {
  Universe universe;
  private final MenuSite menuSite;
  // The constructor is private so that the universe can be created
  // only by an outer-class method [Neighborhood.createUniverse()].

  public UniversePanel(Universe universe, MenuSite menuSite) {  // Create the nested Cells that comprise the "universe." A bug
    // in the current implementation causes the program to fail
    // miserably if the overall size of the grid is too big to fit
    // on the screen.
    this.universe = universe;
    this.universe.attach(this);
    this.menuSite = menuSite;

    addComponentListener
        (new ComponentAdapter() {
           public void componentResized(ComponentEvent e) {
             // Make sure that the cells fit evenly into the
             // total grid size so that each cell will be the
             // same size. For example, in a 64x64 grid, the
             // total size must be an even multiple of 63.

             Rectangle bounds = getBounds();
             bounds.height /= universe.widthInCells();
             bounds.height *= universe.widthInCells();
             bounds.width = bounds.height;
             setBounds(bounds);
           }
         }
        );

    setBackground(Color.white);
    final Dimension PREFERRED_SIZE = new Dimension (
        universe.widthInCells() * universe.DEFAULT_CELL_SIZE,
        universe.widthInCells() * universe.DEFAULT_CELL_SIZE
    );

    setPreferredSize(PREFERRED_SIZE);
    setMaximumSize(PREFERRED_SIZE);
    setMinimumSize(PREFERRED_SIZE);
    setOpaque(true);

    addMouseListener          //{=Universe.mouse}
        (new MouseAdapter() {
           public void mousePressed(MouseEvent e) {
             Rectangle bounds = getBounds();
             bounds.x = 0;
             bounds.y = 0;
             universe.outermostCell.getCellUI().userClicked(e.getPoint(), bounds);
             repaint();
           }
         }
        );

    menuSite.addLine(this, "Grid", "Clear",
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            universe.clear();
            repaint();
          }
        }
    );

    menuSite.addLine      // {=Universe.load.setup}
        (this, "Grid", "Load",
            new ActionListener() {
              public void actionPerformed(ActionEvent e) {
                try {
                  universe.doLoad();
                } catch (IOException theException) {
                  JOptionPane.showMessageDialog(null, "Read Failed!",
                      "The Game of Life", JOptionPane.ERROR_MESSAGE);
                }
              }
            }
        );

    menuSite.addLine
        (this, "Grid", "Store",
            new ActionListener() {
              public void actionPerformed(ActionEvent e) {
                try {
                  universe.doStore();
                } catch (IOException theException) {
                  JOptionPane.showMessageDialog(null, "Write Failed!",
                      "The Game of Life", JOptionPane.ERROR_MESSAGE);
                }
              }
            }
        );

    menuSite.addLine
        (this, "Grid", "Exit",
            new ActionListener() {
              public void actionPerformed(ActionEvent e) {
                System.exit(0);
              }
            }
        );

  }
  /**
   * Override paint to ask the outermost Neighborhood (and any subcells) to draw themselves
   * recursively. All knowledge of screen size is also encapsulated. (The size is passed into the
   * outermost <code>Cell</code>.)
   */

  public void paint(Graphics g) {
    Rectangle panelBounds = getBounds();
    Rectangle clipBounds = g.getClipBounds();

    // The panel bounds is relative to the upper-left
    // corner of the screen. Pretend that it's at (0,0)
    panelBounds.x = 0;
    panelBounds.y = 0;
    universe.outermostCell.getCellUI().redraw(g, panelBounds, true);    //{=Universe.redraw1}
  }

  /**
   * Force a screen refresh by queing a request on the Swing event queue. This is an example of the
   * Active Object pattern (not covered by the Gang of Four). This method is called on every clock
   * tick. Note that the redraw() method on a given <code>Cell</code> does nothing if the
   * <code>Cell</code> doesn't have to be refreshed.
   */

  private void refreshNow() {
    SwingUtilities.invokeLater
        (new Runnable() {
           public void run() {
             Graphics g = getGraphics();
						 if (g == null)    // Universe not displayable
						 {
							 return;
						 }
             try {
               Rectangle panelBounds = getBounds();
               panelBounds.x = 0;
               panelBounds.y = 0;
               universe.outermostCell.getCellUI().redraw(g, panelBounds, false); //{=Universe.redraw2}
             } finally {
               g.dispose();
             }
           }
         }
        );
  }

  @Override
  public void detectUpdate(Observable o) {
    if (o instanceof Universe) {
      repaint();
      return;
    }
    throw new UnsupportedOperationException("only support Universe");
  }
}
