package com.holub.life.system;

import com.holub.life.model.Point;
import com.holub.life.model.cell.Resident;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ResidentService {
  private static ResidentService instance;

  private Map<Point, Resident> mappingTable;
  private Map<Resident, Point> inverseMappingTable;

  public static synchronized ResidentService getInstance() {
    if (instance == null) {
      instance = new ResidentService();
    }
    return instance;
  }

  private ResidentService() {
    mappingTable = new HashMap<>();
    inverseMappingTable = new HashMap<>();
  }

  /**
   *
   * @param r
   * @param p
   * @return if the Point p already exists, return false;
   *         otherwise, return true.
   */
  public boolean register(final Resident r, final Point p) {
    mappingTable.put(p, r);
    inverseMappingTable.put(r, p);
    return true;
  }

  /**
   *
   * @param p
   * @return get resident on a specific point.
   */
  public Resident getResident(final Point p) {
    return mappingTable.get(p);
  }

  /**
   *
   * @param resident
   * @param dx
   * @param dy
   * @return get resident using offset.
   */
  public Resident getResident(final Resident resident, final int dx, final int dy) {
    Point p = inverseMappingTable.get(resident);
    if (p == null) {
      return null;
    }

    return getResident(
        Point.builder()
            .x(p.getX() + dx)
            .y(p.getY() + dy)
            .build());
  }
}
