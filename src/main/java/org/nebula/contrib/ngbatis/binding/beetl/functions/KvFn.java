package org.nebula.contrib.ngbatis.binding.beetl.functions;

// Copyright (c) 2022 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.nebula.contrib.ngbatis.utils.ReflectUtil.getAllColumnFields;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import org.nebula.contrib.ngbatis.utils.ReflectUtil;

/**.
 * 通过实体对象，获取属性的 属性名列表、值列表、参数名列表.
 *.
 * @author yeweicheng.
 * @since 2022-08-25 3:56 <br>.
 *     Now is history.
.*/
public class KvFn extends AbstractFunction
    <Object, String, Boolean, Boolean, Boolean, Void> {

  @Override
  public Object call(
      Object entity, String prefix, Boolean excludePk, Boolean selective,
      Boolean idRequired) {
    excludePk = excludePk == null || excludePk;
    selective = selective != null && selective;
    idRequired = idRequired == null;
    if (entity == null) {
      return new KV();
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

  public static class KV {
    public final List<String> columns = new ArrayList<>();
    public final List<String> valueNames = new ArrayList<>();
    public final List<Object> values = new ArrayList<>();
  }

  /**.
   * 对输入接口的参数进行前置处理，转换成 {@link KV KV} 对象，为参数替换做准备.
   *.
   * @param record 待参数化对象.
   * @param fields 对象属性数组.
   * @param selective 是否排除空值.
   * @param prefix 参数前缀.
   * @return 属性占位符与属性值的键值对（双集合，相同下标成对）.
  .*/
  public KV recordToKV(Object record, Iterable<Field> fields,
      boolean selective, String prefix) {
    KV kv = new KV();
    for (Field field : fields) {
      String name = null;
      if (selective) {
        Object value = ReflectUtil.getValue(record, field);
        if (value != null) {
          name = field.getName();
        }
      } else {
        name = field.getName();
      }
      if (name != null) {
        kv.columns.add(name);
        Object[] paras = {ReflectUtil.getValue(record, field)};
        kv.values.add(fnCall(valueFmtFn, paras));
        kv.valueNames.add(isEmpty(prefix) ? name : String.format(
          "%s.%s", prefix, name));
      }
    }
    return kv;
  }
}
