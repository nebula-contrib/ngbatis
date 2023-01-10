package org.nebula.contrib.ngbatis.models.data;

// Copyright (c) 2022 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import java.util.ArrayList;
import java.util.List;

/**
 * A common pojo for subgraph.
 * 为【子图】数据定义的一个通用数据类型，对业务代码存在入侵，慎用。
 * @author yeweicheng
 * @since 2023-01-07 4:46
 *   <br> Now is history!
 */
public class NgSubgraph<I> {
  private List<NgVertex<I>> vertexes = new ArrayList<>();
  private List<NgEdge<I>> edges = new ArrayList<>();

  public List<NgVertex<I>> getVertexes() {
    return vertexes;
  }

  public void setVertexes(List<NgVertex<I>> vertexes) {
    this.vertexes = vertexes;
  }

  public List<NgEdge<I>> getEdges() {
    return edges;
  }

  public void setEdges(List<NgEdge<I>> edges) {
    this.edges = edges;
  }
}
