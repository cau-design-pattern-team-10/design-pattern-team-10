package com.holub.life;

import java.util.Objects;

/***
 *  The Direction class is used to indicate in just what way a
 *  particular Cell is unstable. If a subcell has just changed
 *  on the north side, for example, then you indicate the
 *  change by issuing:
 *  <PRE>
 *  Direction isDisruptive = new Direction();
 *  isDisruptive.add( Direction.NORTH );
 *  </PRE>
 *  Later on, when updating, you can check whether a cell
 *  is disruptive on a particular edge by issuing:
 *  <Pre>
 *  if( someCell.isDisruptiveTo().the( Direction.NORTH ) )
 *    //...
 *  </PRE>
 *  Two constant directions are provided: Direction.NONE and
 *  Direction.ANY. These differ from a standard direction in that
 *  they cannot be modified. A call to {@link #add} results in
 *  an <code>UnsupportedOperationException</code> toss.
 */

public class Direction {

  /**
   *
   */
  private static final int BITS_NORTH = 0x0001;
  /**
   *
   */
  public static final Direction NORTH = new Immutable(BITS_NORTH);
  /**
   *
   */
  private static final int BITS_SOUTH = 0x0002;
  /**
   *
   */
  public static final Direction SOUTH = new Immutable(BITS_SOUTH);
  /**
   *
   */
  private static final int BITS_EAST = 0x0004;
  /**
   *
   */
  public static final Direction EAST = new Immutable(BITS_EAST);
  /**
   *
   */
  private static final int BITS_WEST = 0x0008;
  /**
   *
   */
  public static final Direction WEST = new Immutable(BITS_WEST);
  /**
   *
   */
  private static final int BITS_NORTHEAST = 0x0010;
  /**
   *
   */
  public static final Direction NORTHEAST = new Immutable(BITS_NORTHEAST);
  /**
   *
   */
  private static final int BITS_NORTHWEST = 0x0020;

  /**
   *
   */
  public static final Direction NORTHWEST = new Immutable(BITS_NORTHWEST);
  /**
   *
   */
  private static final int BITS_SOUTHEAST = 0x0040;
  /**
   *
   */
  public static final Direction SOUTHEAST = new Immutable(BITS_SOUTHEAST);
  /**
   *
   */
  private static final int BITS_SOUTHWEST = 0x0080;
  /**
   *
   */
  public static final Direction SOUTHWEST = new Immutable(BITS_SOUTHWEST);
  /**
   *
   */
  private static final int BITS_ALL = 0x00ff;
  /**
   *
   */
  public static final Direction ALL = new Immutable(BITS_ALL);
  /**
   *
   */
  private static final int BITS_NONE = 0x0000;
  /**
   *
   */
  public static final Direction NONE = new Immutable(BITS_NONE);
  /**
   *
   */
  private int map = BITS_NONE;

  /**
   * @param d
   */
  public Direction(final Direction d) {
    map = d.map;
  }

  /**
   * @param bits
   */
  private Direction(final int bits) {
    map = bits;
  }

  /**
   *
   * @param o
   * @return Do two objects equal
   */
  @Override
  public boolean equals(final Object o) {
    if (o instanceof Direction) {
      Direction d = (Direction) o;
      return d.map == map;
    }
    return false;
  }

  /**
   * @return map hash
   */
  @Override
  public int hashCode() {
    return Objects.hash(map);
  }

  /**
   *
   */
  public void clear() {
    map = BITS_NONE;
  }

  /**
   *
   * @param d
   */
  public void add(final Direction d) {
    map |= d.map;
  }

  /**
   *
   * @param d
   * @return contains that direction.
   */
  public boolean has(final Direction d) {
    return the(d);
  }

  /**
   *
   * @param d
   * @return has direction
   */
  public boolean the(final Direction d) {
    return (map & d.map) == d.map;
  }

  private static final class Immutable extends Direction {

    /**
     *
     */
    private static final String UNSUPPORTED_EXCEPTION_MSG =
        "May not modify Direction constant (Direction.NORTH, etc.)";

    /**
     *
     * @param bits
     */
    private Immutable(final int bits) {
      super(bits);
    }

    /**
     *
     */
    @Override
    public void clear() {
      throw new UnsupportedOperationException(UNSUPPORTED_EXCEPTION_MSG);
    }

    /**
     *
     * @param d
     */
    @Override
    public void add(final Direction d) {
      throw new UnsupportedOperationException(UNSUPPORTED_EXCEPTION_MSG);
    }
  }
}
