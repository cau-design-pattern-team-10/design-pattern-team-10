package com.holub.model.cell;

import lombok.Builder;
import lombok.Data;

/**
 * 클래스 이름을 기능에 맞추어 바꿔주세요.
 * @author min-uklee
 */
@Data
@Builder
public final class NearestCellsDTO {

  /**
   *
   */
  private Cell north;
  /**
   *
   */
  private Cell south;
  /**
   *
   */
  private Cell east;
  /**
   *
   */
  private Cell west;
  /**
   *
   */
  private Cell northeast;
  /**
   *
   */
  private Cell northwest;
  /**
   *
   */
  private Cell southeast;
  /**
   *
   */
  private Cell southwest;
}
