package org.nebula.contrib.ngbatis.models.data;

// Copyright (c) 2022 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A common pojo for paths.
 * 为【路径】数据定义的一个通用数据类型，对业务代码存在入侵，慎用。
 * @author yeweicheng
 * @since 2023-01-07 4:23
 *   <br> Now is history!
 */
public class NgPath<I> {
  
  private List<Relationship<I>> relationships = new ArrayList<>();

  public List<Relationship<I>> getRelationships() {
    return relationships;
  }

  public void setRelationships(
      List<Relationship<I>> relationships) {
    this.relationships = relationships;
  }

  public static class Relationship<I> {
    private I dstID;
    private NgVertex<I> dst = new NgVertex<>();
    private String edgeName;
    private Long rank;
    private I srcID;
    private NgVertex<I> src = new NgVertex<>();
    private Map<String,Object> properties;

    public I getDstID() {
      return dstID;
    }

    public void setDstID(I dstID) {
      this.dstID = dstID;
    }

    public String getEdgeName() {
      return edgeName;
    }

    public void setEdgeName(String edgeName) {
      this.edgeName = edgeName;
    }

    public Long getRank() {
      return rank;
    }

    public void setRank(Long rank) {
      this.rank = rank;
    }

    public I getSrcID() {
      return srcID;
    }

    public void setSrcID(I srcID) {
      this.srcID = srcID;
    }

    public Map<String, Object> getProperties() {
      return properties;
    }

    public void setProperties(Map<String, Object> properties) {
      this.properties = properties;
    }

    public NgVertex<I> getDst() {
      return dst;
    }

    public void setDst(NgVertex<I> dst) {
      this.dst = dst;
    }

    public NgVertex<I> getSrc() {
      return src;
    }

    public void setSrc(NgVertex<I> src) {
      this.src = src;
    }
  }
  
}
