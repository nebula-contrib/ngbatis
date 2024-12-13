package org.nebula.contrib.ngbatis.enums;

/**
 * 边的方向
 * @author xYLiuuuuuu
 * @since 2024/9/6 14:11
 */

public enum Direction {
  NULL(""), //默认是出边
  BIDIRECT("BIDIRECT"), //无向边
  REVERSELY("REVERSELY"); //入边
  private final String symbol;

  Direction(String symbol) {
    this.symbol = symbol;
  }

  public String getSymbol() {
    return symbol;
  }
}
