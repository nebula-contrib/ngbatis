package ye.weicheng.ngbatis.demo.pojo;

// Copyright (c) 2025 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import java.util.List;
import org.nebula.contrib.ngbatis.models.data.NgEdge;
import org.nebula.contrib.ngbatis.models.data.NgVertex;

/**
 * @author yeweicheng
 * @since 2025-07-01 13:00
 * <br>Now is history!
 */
public class TripletStep<T> {
  
  private NgVertex<T> src;
  private NgVertex<T> dst;
  private List<NgEdge<T>> edges;


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

  public List<NgEdge<T>> getEdges() {
    return edges;
  }

  public void setEdges(List<NgEdge<T>> edges) {
    this.edges = edges;
  }

}
