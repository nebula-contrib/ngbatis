package org.nebula.contrib.ngbatis.binding.beetl.functions;

// Copyright (c) 2022 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import static org.nebula.contrib.ngbatis.utils.ReflectUtil.getAllColumnFields;
import static org.nebula.contrib.ngbatis.utils.ReflectUtil.getNameByColumn;
import static org.nebula.contrib.ngbatis.utils.ReflectUtil.getPkField;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

/**
 * 获取实体除主键以外的属性列表
 *
 * @author yeweicheng
 * @since 2022-09-04 5:24
 * <br>Now is history!
 */
public class FieldNamesFn extends AbstractFunction<Object, Boolean, Void, Void, Void, Void> {

  @Override
  public List<String> call(Object entity, Boolean useColumnName) {
    useColumnName = useColumnName == null || useColumnName;
    Class<?> entityClass = entity.getClass();
    Field[] fields = getAllColumnFields(entityClass);
    List<String> fieldNames = new LinkedList<>();
    Field pkField = getPkField(entityClass, true);
    for (Field field : fields) {
      String fieldName = useColumnName ? getNameByColumn(field) : field.getName();
      if (!field.equals(pkField)) {
        fieldNames.add(fieldName);
      }
    }
    return fieldNames;
  }

}
