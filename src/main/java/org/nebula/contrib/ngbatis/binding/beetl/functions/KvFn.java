package org.nebula.contrib.ngbatis.binding.beetl.functions;

// Copyright (c) 2022 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.nebula.contrib.ngbatis.utils.ReflectUtil.getAllColumnFields;
import static org.nebula.contrib.ngbatis.utils.ReflectUtil.schemaByEntityType;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.nebula.contrib.ngbatis.utils.ReflectUtil;

/**
 * 通过实体对象，获取属性的 属性名列表、值列表、参数名列表
 *
 * @author yeweicheng
 * @since 2022-08-25 3:56
 * <br>Now is history!
 */
public class KvFn extends AbstractFunction<Object, String, Boolean, Boolean, Boolean, Void> {

  @Override
  public Object call(Object entity, String prefix, Boolean excludePk, Boolean selective,
      Boolean idRequired) {
    excludePk = excludePk == null || excludePk;
    selective = selective != null && selective;
    idRequired = idRequired == null || idRequired;
    if (entity == null) {
      return new KV();
    }
    if (entity instanceof Map) {
      return mapToKv((Map<String, Object>)entity, prefix, selective);
    }
    Class<?> entityType = entity.getClass();
    Field[] fields = getAllColumnFields(entityType);

    List<Field> fieldList = new ArrayList<>();
    Field pkField = null;
    if (excludePk) {
      pkField = fnCall(pkFieldFn, entityType, idRequired);
    }

    for (Field field : fields) {
      if (pkField != null && pkField.equals(field)) {
        continue;
      }
      fieldList.add(field);
    }

    return recordToKV(entity, fieldList, selective, prefix);
  }

  private Object mapToKv(Map<String, Object> map, String prefix, Boolean selective) {
    KV kv = new KV();
    Set<String> keys = map.keySet();
    for (String key : keys) {
      String name = null;
      Object value = map.get(key);
      if (!selective || value != null) {
        name = key;
      }
      if (name != null) {
        kv.columns.add(name);
        Object[] paras = {value};
        kv.values.add(fnCall(valueFmtFn, paras));
        Class<?> type = value == null ? null : value.getClass();
        kv.types.add(type);
        String valueName = isEmpty(prefix) ? name : String.format("%s.%s", prefix, name);
        kv.valueNames.add(valueName);
      }
    }
    return kv;
  }

  /**
   * 对输入接口的参数进行前置处理，转换成 {@link KV KV} 对象，为参数替换做准备
   *
   * @param record  待参数化对象
   * @param fields  对象属性数组
   * @param selective 是否排除空值
   * @param prefix  参数前缀
   * @return 属性占位符与属性值的键值对（双集合，相同下标成对）
   */
  public KV recordToKV(Object record, Iterable<Field> fields, boolean selective, String prefix) {
    KV kv = new KV();
    for (Field field : fields) {
      String name = null;
      Object value = ReflectUtil.getValue(record, field);
      if (!selective || value != null) {
        name = ReflectUtil.getNameByColumn(field);
      }
      if (name != null) {
        
        Class<?> entityType = field.getDeclaringClass();
        String tagName = schemaByEntityType(entityType);
        List<String> currentTagColumns = kv.multiTagColumns
            .computeIfAbsent(tagName, (k) -> new ArrayList<>());
        currentTagColumns.add(name);
        
        kv.columns.add(name);
        Object[] paras = {value};
        kv.values.add(fnCall(valueFmtFn, paras));
        kv.types.add(field.getType());
        String valueName = isEmpty(prefix) ? name : String.format("%s.%s", prefix, name);
        kv.valueNames.add(valueName);
      }
    }
    return kv;
  }

  public static class KV {
    public final Map<String, List<String>> multiTagColumns = new LinkedHashMap<>();
    public final List<String> columns = new ArrayList<>();
    public final List<String> valueNames = new ArrayList<>();
    public final List<Object> values = new ArrayList<>();
    public final List<Class<?>> types = new ArrayList<>();
  }

}
