package org.nebula.contrib.ngbatis.base;

import java.util.Map;

/**
 * 实体基类
 * @author xYLiuuuuuu
 * @since 2024/9/8 10:01
 */

public abstract class GraphBase {

	protected abstract String getEntityTypeName();

	protected abstract Map<String,Object> getEntityProperties();

}
