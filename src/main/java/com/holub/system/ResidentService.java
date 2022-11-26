package com.holub.system;

import com.holub.model.Point;
import com.holub.model.cell.Resident;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ResidentService {
  private Map<Point, Resident> mappingTable;
  private Map<Resident, Point> inverseMappingTable;

  public ResidentService() {
    mappingTable = new HashMap<>();
  }

  /**
   *
   * @param r
   * @param p
   * @return if the Point p already exists, return false;
   *         otherwise, return true
   */
  public boolean register(Resident r, Point p) {
    if (mappingTable.containsKey(p)) {
      return false;
    }
    mappingTable.put(p, r);
    inverseMappingTable.put(r, p);
    return true;
  }

  /**
   *
   * @param p
   * @return get resident on a specific point.
   */
  public Optional<Resident> getResident(Point p) {
    return Optional.ofNullable(mappingTable.get(p));
  }

  /**
   *
   * @param resident
   * @param dx
   * @param dy
   * @return get resident using offset
   */
  public Optional<Resident> getResident(Resident resident, int dx, int dy) {
    Point p = inverseMappingTable.get(resident);
    if (p == null) {
      return Optional.empty();
    }

    p.setX(p.getX() + dx);
    p.setY(p.getY() + dy);
    return getResident(p);
  }
}
