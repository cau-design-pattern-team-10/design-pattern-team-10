package com.holub.life.model.cell;

import com.holub.life.model.Direction;
import com.holub.tools.Storable;
import com.holub.life.model.Point;
import com.holub.tools.Observable;

/***
 * This interface is the basic unit that comprises a life board.
 * It's implemented both by {@link Resident} (which represents
 * an individual cell on the board) and {@link Neighborhood},
 * which represents a group of cells.
 */

public interface Cell extends Observable {

  /**
   * Possible value for the "load" argument to transfer().
   */
  boolean STORE = false;
  /**
   * Possible value for the "load" argument to transfer().
   */
  boolean LOAD = true;

  /**
   * Figure out the next state of the cell, given the specified neighbors.
   * @param dto
   * @return true if the cell is unstable (changed state).
   */
  boolean figureNextState(NearestCellsDTO dto);

  /**
   * Access a specific contained cell located at the edge of the composite cell.
   *
   * @param row The requested row. Must be on the edge of the block.
   * @param column The requested column. Must be on the edge of the block.
   * @return true  if the the state changed.
   */
  Cell edge(int row, int column);

  /**
   * Transition to the state computed by the most recent call to
   * {@link #figureNextState}.
   *
   * @return true if a changed of state happened during the transition.
   */
  boolean transition();


  /**
   * Return true if this cell or any subcells are alive.
   *
   * @return this cell is alive.
   */
  boolean isAlive();

  /**
   * Return the specified width plus the current cell's width.
   *
   * @return this cell width
   */
  int widthInCells();

  /**
   * Return a fresh (newly created) object identical to yourself in content.
   * @return cell replica
   */
  Cell create();

  /**
   * Returns a Direction indicated the directions of the cells that have changed
   * state.
   *
   * @return A Direction object that indicates the edge or edges on which
   * a change has occurred.
   */

  Direction isDisruptiveTo();

  /**
   * Set the cell and all subcells into a "dead" state.
   */

  void clear();

  /**
   * This method is used internally to save or restore the state of a cell
   * from a memento.
   *
   * @param memento
   * @param upperLeftCorner
   * @param doLoad
   * @return true if this cell was modified by the transfer.
   */
  boolean transfer(Storable memento, Point upperLeftCorner,
      boolean doLoad);

  /**
   * This method is used by container of the outermost cell. It is not used
   * internally. It need be implemented only by whatever class defines the
   * outermost cell in the universe. Other cell implementations should throw
   * an UnsupportedOperationException when this method is called.
   *
   * @return memento
   */
  Storable createMemento();

  /**
   * The Cell.Memento interface stores the state of a Cell and all its
   * subcells for future restoration.
   *
   * @see Cell
   */

  interface Memento extends Storable {

    /**
     * On creation of the memento, indicate that a cell is alive.
     * @param location
     */
    void markAsAlive(Point location);

    /**
     * On restoration of a cell from a memento, indicate that a cell is alive.
     * @param location
     * @return does cell exist on the location.
     */
    boolean isAlive(Point location);
  }

}
