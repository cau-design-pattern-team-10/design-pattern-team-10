package com.holub.life.ui;

import com.holub.life.model.Point;
import com.holub.life.system.TickSystem;
import com.holub.life.system.Universe;
import com.holub.life.ui.menu.ClockMenuItem;
import com.holub.tools.Observable;
import com.holub.tools.Observer;
import com.holub.life.ui.cell.CellUI;
import com.holub.life.ui.cell.CellUIFactory;
import com.holub.life.ui.menu.MenuSite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import lombok.Getter;

/**
 * The Universe is a mediator that sits between the Swing event model and the
 * Life classes. It is also a singleton, accessed via Universe.instance().
 * It handles all Swing events and translates them into requests to the
 * outermost Neighborhood. It also creates the Composite Neighborhood.
 */

public class UniversePanel extends JPanel implements Observer {

  /**
   * The size of the smallest "atomic" cell---a Resident object.
   * This size is extrinsic to a Resident (It's passed into the Resident's
   * "draw yourself" method.
   */
  public static final int DEFAULT_CELL_SIZE = 8;
  private Universe universe;
  private CellUI outermostCellUI;
  private int cellSize = DEFAULT_CELL_SIZE;
  @Getter
  private StatusBar statusBar;
  @Getter
  private MenuSite menuSite;

  /**
   * The constructor is private so that the universe can be created.
   * only by an outer-class method [Neighborhood.createUniverse()].
   * @param u
   */
  public UniversePanel(final Universe u) {
    // Create the nested Cells that comprise the "universe." A bug
    // in the current implementation causes the program to fail
    // miserably if the overall size of the grid is too big to fit
    // on the screen.
    this.universe = u;
    this.universe.attach(this);
    this.statusBar = new StatusBar();
    this.universe.getTickSystem().attach(statusBar);
    this.menuSite = new MenuSite();
    CellUIFactory cellUIFactory = CellUIFactory.getInstance();
    this.outermostCellUI = cellUIFactory.createCellUI(
        universe.getOutermostCell(), this);

    addComponentListener(new ComponentAdapter() {
           public void componentResized(final ComponentEvent e) {
             // Make sure that the cells fit evenly into the
             // total grid size so that each cell will be the
             // same size. For example, in a 64x64 grid, the
             // total size must be an even multiple of 63.

             Rectangle bounds = getBounds();
             cellSize = bounds.height / universe.widthInCells();
             bounds.height = cellSize * universe.widthInCells();
             bounds.width = bounds.height;
             setBounds(bounds);
           }
         });

    setBackground(Color.white);
    final Dimension preferredSize = new Dimension(
        universe.widthInCells() * cellSize,
        universe.widthInCells() * cellSize);

    setPreferredSize(preferredSize);
    setMaximumSize(preferredSize);
    setMinimumSize(preferredSize);
    setOpaque(true);

    addMouseListener(new MouseAdapter() {
           public void mousePressed(final MouseEvent e) {
             java.awt.Point mouseRealPoint = e.getPoint();
             Point p = new Point();
             p.setX(mouseRealPoint.x / cellSize);
             p.setY(mouseRealPoint.y / cellSize);
             outermostCellUI.click(p);
           }
         });
  }

  /**
   * Override paint to ask the outermost Neighborhood (and any subcells) to draw
   * themselves recursively. All knowledge of screen size is also encapsulated.
   * (The size is passed into the outermost <code>Cell</code>.)
   *
   * @param g
   */
  public void paint(final Graphics g) {
    Rectangle panelBounds = getBounds();
    // The panel bounds is relative to the upper-left
    // corner of the screen. Pretend that it's at (0,0)
    panelBounds.x = 0;
    panelBounds.y = 0;
    outermostCellUI.redraw(g, panelBounds, true);
  }

  /**
   *
   * @param o
   */
  @Override
  public void detectUpdate(final Observable o) {
    if (o instanceof Universe) {
      repaint();
      return;
    }
    throw new UnsupportedOperationException("only support Universe");
  }

  public void setFrame(JFrame jframe) {
    ClockMenuItem clockMenu = new ClockMenuItem(universe.getTickSystem());
    menuSite.establish(jframe);

    registerGridMenu();
    menuSite.register(clockMenu);
    jframe.getContentPane().setLayout(new BorderLayout());
    jframe.getContentPane().add(this, BorderLayout.CENTER);
    jframe.getContentPane().add(statusBar, BorderLayout.SOUTH);
    jframe.addKeyListener(new KeyAdapter() {
      public void keyPressed(KeyEvent e) {
        TickSystem tickSystem = universe.getTickSystem();
        switch (e.getKeyCode()) {
          case KeyEvent.VK_RIGHT:
            tickSystem.tick();
            break;
          case KeyEvent.VK_LEFT:
          case KeyEvent.VK_U:
            try {
              universe.doRollback();
            } catch (IOException ex) {
              throw new RuntimeException(ex);
            }
            break;
          case KeyEvent.VK_SPACE:
            if(tickSystem.isRunning()) {
              tickSystem.stop();
            } else {
              tickSystem.resume();
            }
        }
      }
    });
  }

  private void registerGridMenu() {
    menuSite.addLine(this, "Grid", "Clear",
        e -> {
          universe.clear();
          repaint();
        });

    menuSite.addLine(this, "Grid", "Load",
        e -> {
          try {
            universe.doLoad();
          } catch (IOException theException) {
            JOptionPane.showMessageDialog(null, "Read Failed!",
                "The Game of Life", JOptionPane.ERROR_MESSAGE);
          }
        });

    menuSite.addLine(this, "Grid", "Load(Overlap)",
        e -> {
          try {
            universe.doOverlapLoad();
          } catch (IOException theException) {
            JOptionPane.showMessageDialog(null, "Read Failed!",
                "The Game of Life", JOptionPane.ERROR_MESSAGE);
          }
        });

    menuSite.addLine(this, "Grid", "Store",
        e -> {
          try {
            universe.doStore();
          } catch (IOException theException) {
            JOptionPane.showMessageDialog(null, "Write Failed!",
                "The Game of Life", JOptionPane.ERROR_MESSAGE);
          }
        });
    menuSite.addLine(this, "Go", "Undo",
        e -> {
          try {
            universe.doRollback();
          } catch (IOException theException) {
            JOptionPane.showMessageDialog(null, "Undo Failed!",
                "The Game of Life", JOptionPane.ERROR_MESSAGE);
          }
        });


    menuSite.addLine(this, "Grid", "Exit",
        e -> System.exit(0));

  }
}
