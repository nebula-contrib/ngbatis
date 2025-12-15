package org.nebula.contrib.ngbatis.models.data;

// Copyright (c) 2025 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

public class NgTriplet<I> {
  private I srcId;
  private I dstId;
  private Object startNode;
  private Object edge;
  private Object endNode;

  public NgTriplet() {
  }

  public NgTriplet(Object startNode, Object edge, Object endNode) {
    this.startNode = startNode;
    this.edge = edge;
    this.endNode = endNode;
  }

  public I getSrcId() {
    return srcId;
  }

  public void setSrcId(I srcId) {
    this.srcId = srcId;
  }

  public I getDstId() {
    return dstId;
  }

  public void setDstId(I dstId) {
    this.dstId = dstId;
  }

  public Object getStartNode() {
    return startNode;
  }

  public void setStartNode(Object startNode) {
    this.startNode = startNode;
  }

  public Object getEdge() {
    return edge;
  }

  public void setEdge(Object edge) {
    this.edge = edge;
  }

  public Object getEndNode() {
    return endNode;
  }

  public void setEndNode(Object endNode) {
    this.endNode = endNode;
  }
}
