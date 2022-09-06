package org.nebula.contrib.ngbatis.binding.beetl.functions;

// Copyright (c) 2022 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import static org.nebula.contrib.ngbatis.utils.ReflectUtil.getAllColumnFields;
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
public class FieldNamesFn extends AbstractFunction<Object, Void, Void, Void, Void, Void> {

  @Override
  public List<String> call(Object entity) {
    Class<?> entityClass = entity.getClass();
    Field[] fields = getAllColumnFields(entityClass);
    List<String> fieldNames = new LinkedList<>();
    String pkName = getPkField(entityClass, true).getName();
    for (Field field : fields) {
      String fieldName = field.getName();
      if (!fieldName.equals(pkName)) {
        fieldNames.add(fieldName);
      }
    }
    return fieldNames;
  }

}
