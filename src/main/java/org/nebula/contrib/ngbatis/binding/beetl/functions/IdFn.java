package org.nebula.contrib.ngbatis.binding.beetl.functions;

// Copyright (c) 2022 All project authors and nebula-contrib. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import org.nebula.contrib.ngbatis.PkGenerator;
import org.nebula.contrib.ngbatis.proxy.MapperProxy;
import org.nebula.contrib.ngbatis.utils.ReflectUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;

import static org.nebula.contrib.ngbatis.utils.ReflectUtil.getPkField;

/**
 * 通过实体对象，获取 id 值
 *
 * @author yeweicheng
 * @since 2022-08-25 2:58
 * <br>Now is history!
 */
public class IdFn extends AbstractFunction
    <Iterable<?>, String, String, Void, Void, Void> {

    private static Logger log = LoggerFactory.getLogger(IdFn.class);

    @Override
    public Object call(Object entity, Boolean canNotNull) {
        canNotNull = canNotNull == null;
        Class<?> entityType = entity.getClass();
        Field pkField = getPkField(entityType, canNotNull);
        String tagName = fnCall(tagNameFn, entity);
        return setId(entity, pkField, tagName);
    }

    /**
     * 对实体对象设置 主键值（通过主键生成策略）
     *
     * @param record  实体类
     * @param pkField 主键属性
     * @param tagName 数据库中的模式名（节点类型与关系类型 名称）
     * @return 主键值
     */
    public Object setId(Object record, Field pkField, String tagName) {
        try {
            PkGenerator pkGenerator = MapperProxy.ENV.getPkGenerator();
            Object id = null;
            if (pkField != null) {
                id = ReflectUtil.getValue(record, pkField);
                if (id == null && pkGenerator != null) {
                    id = pkGenerator.generate(tagName, pkField.getType());
                    ReflectUtil.setValue(record, pkField, id);
                }
            }
            return fnCall(valueFmtFn, id);
        } catch (IllegalAccessException e) {
            log.error(e.getMessage());
            return null;
        }
    }

}
