package org.nebula.contrib.ngbatis.base;

import java.util.Map;
import org.nebula.contrib.ngbatis.models.MapperContext;

/**
 * 实体基类
 * @author xYLiuuuuuu
 * @since 2024/9/8 10:01
 */

public abstract class GraphBase {
  public GraphBase() {
    Map<String, Class<?>> tagTypeMapping = MapperContext.newInstance().getTagTypeMapping();
    tagTypeMapping.put(getEntityTypeName(), this.getClass());
  }

  protected abstract String getEntityTypeName();

  protected abstract Map<String, Object> getEntityProperties();
}
