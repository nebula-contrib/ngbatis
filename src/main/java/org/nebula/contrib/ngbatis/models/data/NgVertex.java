package org.nebula.contrib.ngbatis.models.data;

// Copyright (c) 2022 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import java.util.List;
import java.util.Map;

/**
 * A common pojo for vertexes.
 * 为【点】数据定义的一个通用数据类型，对业务代码存在入侵，慎用。
 * @author yeweicheng
 * @since 2023-01-07 4:23
 *   <br> Now is history!
 */
public class NgVertex<I> {
  private String type;
  private I vid;
  private List<String> tags;
  private Map<String, Object> properties;
  
  public String tag() {
    return tags.get(0);
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public I getVid() {
    return vid;
  }

  public void setVid(I vid) {
    this.vid = vid;
  }

  public List<String> getTags() {
    return tags;
  }

  public void setTags(List<String> tags) {
    this.tags = tags;
  }

  public Map<String, Object> getProperties() {
    return properties;
  }

  public void setProperties(Map<String, Object> properties) {
    this.properties = properties;
  }
}
