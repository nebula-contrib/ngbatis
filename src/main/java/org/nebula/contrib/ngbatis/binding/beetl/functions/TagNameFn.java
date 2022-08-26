package org.nebula.contrib.ngbatis.binding.beetl.functions;

// Copyright (c) 2022 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import static org.nebula.contrib.ngbatis.proxy.NebulaDaoBasicExt.entityType;

import javax.persistence.Table;
import org.nebula.contrib.ngbatis.models.ClassModel;
import org.nebula.contrib.ngbatis.utils.StringUtil;

/**
 * 通过实体对象，获取 vertexName 与 edgeName
 *
 * @author yeweicheng
 * @since 2022-08-25 2:51 <br>
 *     Now is history!
 */
public class TagNameFn extends AbstractFunction
    <Object, ClassModel, Void, Void, Void, Void> {
  @Override
  public Object call(final Object para, final ClassModel cm) {
    if (cm != null) {
      return entityType(cm.getClazz());
    }
    Class<?> entityType = para.getClass();
    Table tableAnno = entityType.getAnnotation(Table.class);
    return tableAnno != null ? tableAnno.name() : StringUtil.xX2xUnderscoreX(
      entityType.getSimpleName());
  }
}
