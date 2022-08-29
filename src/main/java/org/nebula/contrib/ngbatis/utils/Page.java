package org.nebula.contrib.ngbatis.utils;

// Copyright (c) 2022 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import java.util.List;
import java.util.Map;

/**
 * <p>用于分页的数据容器。</p>
 * @author yeweicheng
 * @since 2022-06-28 3:12
 * <br>Now is history!
 */

public class Page<T> {

  private int pageNo = 1;
  private int pageSize = 100;
  private int startRow = 0;
  private int endRow = 15;
  private long total;
  private int pages;
  private List<T> rows;
  private T entity;
  private Map<String, Object> query;

  public Page() {
  }

  /**
   * <p>创建分页对象。</p>
   * @param pageNo 当前页码
   * @param pageSize 每页的容量
   */
  public Page(int pageNo, int pageSize) {
    this.pageNo = pageNo;
    this.pageSize = pageSize;
    this.startRow = pageNo > 0 ? (pageNo - 1) * pageSize : 0;
    this.endRow = pageNo * pageSize;
  }

  public List<T> getRows() {
    return rows;
  }

  public void setRows(List<T> rows) {
    this.rows = rows;
  }

  public int getPages() {
    return pages;
  }

  public void setPages(int pages) {
    this.pages = pages;
  }

  public void setPages() {
    this.pages =
      (int) (this.total / this.pageSize + ((this.total % this.pageSize == 0) ? 0 : 1));
  }

  public int getEndRow() {
    return endRow;
  }

  public void setEndRow(int endRow) {
    this.endRow = endRow;
  }

  public int getPageNo() {
    return pageNo;
  }

  /**
   * 设置页码。
   * @param pageNo 想要设置成的新页码
   */
  public void setPageNo(int pageNo) {
    this.pageNo = pageNo;
    this.startRow = pageNo > 0 ? (pageNo - 1) * pageSize : 0;
    this.endRow = pageNo * pageSize;
  }

  /**
   * <p>获取每页容量。</p>
   * @return 每页容量
   */
  public int getPageSize() {
    return pageSize;
  }

  /**
   * <p>设置每页容量</p>
   * @param pageSize 每页容量
   */
  public void setPageSize(int pageSize) {
    this.pageSize = pageSize;
    this.startRow = pageNo > 0 ? (pageNo - 1) * pageSize : 0;
    this.endRow = pageNo * pageSize;
    this.setPages();
    ;
  }

  public int getStartRow() {
    return startRow;
  }

  /**
   * <p>设查询的起始条数（offset）。</p>
   * @param startRow 起始条数
   */
  public void setStartRow(int startRow) {
    this.startRow = startRow;
  }

  public long getTotal() {
    return total;
  }

  /**
   * <p>设置总条数。</p>
   * @param total 总条数
   */
  public void setTotal(long total) {
    this.total = total;
    this.setPages();
  }

  public Map<String, Object> getQuery() {
    return query;
  }

  public void setQuery(Map<String, Object> query) {
    this.query = query;
  }

  public T getEntity() {
    return entity;
  }

  public void setEntity(T entity) {
    this.entity = entity;
  }

  @Override
  public String toString() {
    return "Page{"
      + "pageNo="
      + pageNo
      + ", pageSize="
      + pageSize
      + ", startRow="
      + startRow
      +
      ", endRow="
      + endRow
      + ", total="
      + total
      + ", pages="
      + pages
      + '}';
  }

}
