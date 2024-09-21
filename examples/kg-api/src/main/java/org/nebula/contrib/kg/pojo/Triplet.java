package org.nebula.contrib.kg.pojo;

// Copyright (c) 2024 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import org.nebula.contrib.ngbatis.models.data.NgEdge;
import org.nebula.contrib.ngbatis.models.data.NgVertex;

/**
 * @author yeweicheng
 * @since 2024-08-28 13:28
 * <br>Now is history!
 */

public class Triplet<T> {

  private NgVertex<T> src;
  private NgVertex<T> dst;
  private NgEdge<T> edge;

  public NgVertex<T> getSrc() {
    return src;
  }

  public void setSrc(NgVertex<T> src) {
    this.src = src;
  }

  public NgVertex<T> getDst() {
    return dst;
  }

  public void setDst(NgVertex<T> dst) {
    this.dst = dst;
  }

  public NgEdge<T> getEdge() {
    return edge;
  }

  public void setEdge(NgEdge<T> edge) {
    this.edge = edge;
  }
}
