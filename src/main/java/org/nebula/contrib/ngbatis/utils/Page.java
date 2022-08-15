package org.nebula.contrib.ngbatis.utils;

// Copyright (c) 2022 nebula-contrib. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import java.util.List;
import java.util.Map;

/**
 * @author yeweicheng
 * @since 2022-06-28 3:12
 * <br>Now is history!
 */

public class Page<T> {
    public int  pageNo = 1;
    public int  pageSize = 100;
    public int  startRow = 0;
    public int  endRow = 15;
    public long    total;
    public int     pages;
    public List<T> rows;
    public T entity;
    public Map<String, Object> q;

    public Long sysUser;

    public Page() {
    }

    public Page(int pageNo, int pageSize) {
        this.pageNo = pageNo;
        this.pageSize = pageSize;
        this.startRow = pageNo > 0 ? (pageNo - 1) * pageSize : 0;
        this.endRow = pageNo * pageSize;
    }

    public Long getSysUser() {
        return sysUser;
    }

    public void setSysUser(Long sysUser) {
        this.sysUser = sysUser;
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
        this.pages = (int) (this.total / this.pageSize + (( this.total  % this.pageSize == 0) ? 0 : 1));
    }

    public int getEndRow() {
        return endRow;
    }

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
        this.startRow = pageNo > 0 ? (pageNo - 1) * pageSize : 0;
        this.endRow = pageNo * pageSize;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
        this.startRow = pageNo > 0 ? (pageNo - 1) * pageSize : 0;
        this.endRow = pageNo * pageSize;
        this.setPages();;
    }

    public int getStartRow() {
        return startRow;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
        this.setPages();;
    }

    public Map<String, Object> getQ() {
        return q;
    }

    public void setQ(Map<String, Object> q) {
        this.q = q;
    }

    @Override
    public String toString() {
        return "Page{" + "pageNo=" + pageNo + ", pageSize=" + pageSize + ", startRow=" + startRow + ", endRow=" + endRow + ", total=" + total + ", pages=" + pages + '}';
    }

}
