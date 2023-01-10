package org.nebula.contrib.ngbatis.models.data;

// Copyright (c) 2022 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import java.util.Map;

/**
 * A common pojo for edges.
 * 为【边】数据定义的一个通用数据类型，对业务代码存在入侵，慎用。
 * @author yeweicheng
 * @since 2023-01-07 4:20
 *   <br> Now is history!
 */
public class NgEdge<I> {
  private String type;
  private I srcID;
  private I dstID;
  private Long rank;
  private String edgeName;
  private Map<String, Object> properties;

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public I getSrcID() {
    return srcID;
  }

  public void setSrcID(I srcID) {
    this.srcID = srcID;
  }

  public I getDstID() {
    return dstID;
  }

  public void setDstID(I dstID) {
    this.dstID = dstID;
  }

  public Long getRank() {
    return rank;
  }

  public void setRank(Long rank) {
    this.rank = rank;
  }

  public String getEdgeName() {
    return edgeName;
  }

  public void setEdgeName(String edgeName) {
    this.edgeName = edgeName;
  }

  public Map<String, Object> getProperties() {
    return properties;
  }

  public void setProperties(Map<String, Object> properties) {
    this.properties = properties;
  }
}
