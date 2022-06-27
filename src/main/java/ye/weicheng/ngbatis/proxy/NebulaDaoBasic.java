// Copyright 2022-present Weicheng Ye. All rights reserved.
// Use of this source code is governed by a MIT-style license that can be
// found in the LICENSE file.
package ye.weicheng.ngbatis.proxy;

import com.vesoft.nebula.client.graph.data.ResultSet;
import ye.weicheng.ngbatis.TextResolver;
import ye.weicheng.ngbatis.exception.QueryException;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.*;

import static ye.weicheng.ngbatis.proxy.NebulaDaoBasicExt.*;

/**
 * @author yeweicheng
 * @since 2022-06-12 12:21
 * <br>Now is history!
 */
public interface NebulaDaoBasic<T ,ID extends Serializable> {
    /**
     * <p>数据操作，逻辑删除接口，前提当前类 有字段 is_del </p>
     *
     * @param id 表记录主键
     * @return
     */
    default int deleteLogicById(ID id) {
        String cqlTpl = getCqlTpl();
        Class<?>[] classes = entityTypeAndIdType(this.getClass());
        TextResolver textResolver = MapperProxy.ENV.getTextResolver();
        Field pkField = getPkField(classes[0].getDeclaredFields(), classes[0]);
        String pstm = keyFormat(pkField, pkField.getName(), true);
        Map<String, Object> tplParam = new HashMap<String, Object>() {{
            put("id", pstm);
        }};
        Map<String, Object> queryParam = new HashMap<String, Object>() {{
            put( pkField.getName(), id);
        }};
        String cql = textResolver.resolve(cqlTpl, tplParam);
        ResultSet resultSet = (ResultSet)proxy(this.getClass(), ResultSet.class, cql, new Class<?>[]{Serializable.class}, queryParam);
        return resultSet.isSucceeded() ? 1 : 0;
    }

    /**
     * <p>通过 主键删除当前记录</p>
     *
     * @param id 表记录主键
     * @return
     */
    default int deleteById(ID id) {
        throw new QueryException("No implements");
    }

    /**
     * <p>插入一条记录，全属性插入</p>
     *
     * @param record 当前表对应的记录数据
     * @return
     */
    default int insert(T record) {

        TextResolver textResolver = MapperProxy.ENV.getTextResolver();

        KV kv = allFields(record);
        String cqlTpl = getCqlTpl();
        Class<?> vertexType = record.getClass();
        String vertexName = vertexName(vertexType);

        Field[] fields = vertexType.getDeclaredFields();

        Field pkField = getPkField( fields, vertexType );

        String vId = keyFormat(pkField, pkField.getName(), true);

        Object id = setId( record, pkField, vertexName );
        String nGQL = textResolver.resolve(
                cqlTpl,
                new HashMap<String, Object>() {{
                    put( "columns", kv.columns );
                    put( "valueColumns", kv.valueNames );
                    put( "table", vertexName);
                    put( "vId", vId );
                }}
        );
        ResultSet resultSet = (ResultSet)proxy( this.getClass(), ResultSet.class,  nGQL, new Class[] { Object.class }, record );
        return resultSet.isSucceeded() ? 1: 0;
    }

    /**
     * <p>插入非空字段。</p>
     *
     * @param record
     * @return
     */
    default Integer insertSelective(T record) {
        TextResolver textResolver = MapperProxy.ENV.getTextResolver();

        KV kv = notNullFields(record);
        String cqlTpl = getCqlTpl();
        Class<?> vertexType = record.getClass();
        String vertexName = vertexName(vertexType);

        Field[] fields = vertexType.getDeclaredFields();

        Field pkField = getPkField( fields, vertexType );

        String vId = keyFormat(pkField, pkField.getName(), true);

        Object id = setId( record, pkField, vertexName );
        String nGQL = textResolver.resolve(
                cqlTpl,
                new HashMap<String, Object>() {{
                    put( "columns", kv.columns );
                    put( "valueColumns", kv.valueNames );
                    put( "table", vertexName);
                    put( "vId", vId );
                }}
        );
        ResultSet resultSet = (ResultSet)proxy( this.getClass(), ResultSet.class,  nGQL, new Class[] { Object.class }, record );
        return resultSet.isSucceeded() ? 1: 0;
    }


    /**
     * <p>通过主键查询对应表的单条记录</p>
     *
     * @param id 记录主键
     * @return 表中的记录对应的实体对象
     */
    default T selectById(ID id) {
        Class[] classes = entityTypeAndIdType(this.getClass());
        Class<T> entityType = classes[0];
        String vertexName = vertexName(entityType);
        String nGQL = "MATCH ( n: " + vertexName + " ) WHERE id(n) == $p0 RETURN n LIMIT 2 "; // limit 2 to check the data error, when there is 2 rows record has same id;
        return (T)proxy( this.getClass(), entityType, nGQL, new Class[] {Serializable.class}, id );
    }

    /**
     * <p>通过多个 id 值查询符合条件的记录</p>
     *
     * @param ids
     * @return
     */
    default List<T> selectByIds(Collection<ID> ids) {

        throw new QueryException("No implements");
    }

    /**
     * <p>以实体类为载体，存放查询条件，不为空的属性为查询条件</p>
     *
     * @param record
     * @return
     */
    default List<T> selectBySelective(T record){
        KV kv = notNullFields(record);
        TextResolver textResolver = MapperProxy.ENV.getTextResolver();
        String cqlTpl = getCqlTpl();
        Class<?> entityType = record.getClass();
        String vertexName = vertexName(entityType);
        String nGQL = textResolver.resolve(
                cqlTpl,
                new HashMap<String, Object>() {{
                    put( "columns", kv.columns );
                    put( "valueColumns", kv.valueNames );
                    put( "tag", vertexName);
                }}
        );
        return (List<T>)proxy( this.getClass(), entityType, nGQL, new Class[] {Object.class}, record );
    }

    /**
     * <p>以实体类为载体，存放查询条件，不为空的属性为查询条件，String 类型的属性也使用精确查询</p>
     *
     * @param record
     * @return
     */
    default List<T> selectBySelectivePrecise(T record) {

        throw new QueryException("No implements");
    }

    /**
     * <p>通过 map 存放查询参数，查询多条记录并映射成实体类</p>
     * <p>通常与 {@link #countByMap(Map) countByMap} 联合使用，以实现分页数据获取功能</p>
     *
     * @param param
     * @return
     */
    default List<T> selectByMap(Map<String, Object> param) {

        throw new QueryException("No implements");
    }

    /**
     * <p>统计符合条件的记录数</p>
     * <p>通常与 {@link #selectByMap(Map) selectByMap} 联合使用，以实现分页数据获取功能</p>
     *
     * @param param 查询条件
     * @return
     */
    default Long countByMap(Map<String, Object> param) {

        throw new QueryException("No implements");
    }

    /**
     * <p>按条件查出所有符合条件的记录的 主键 </p>
     *
     * @param record 查询条件
     * @return
     */
    default List<ID> selectIdBySelective(T record) {

        throw new QueryException("No implements");
    }

    /**
     * <p>更新</p>
     *
     * @param record
     * @return
     */
    default int updateByIdSelective(T record) {

        throw new QueryException("No implements");
    }

    default int updateByIdWithBLOBs(T record) {

        throw new QueryException("No implements");
    }

    default int updateById(T record) {

        throw new QueryException("No implements");
    }

    default int insertBatch(List<T> ts) {

        throw new QueryException("No implements");
    }

    default Long countGridByMap(Map<String, Object> param) {

        throw new QueryException("No implements");
    }

    default List<Map> selectGridByMap(Map<String, Object> param) {

        throw new QueryException("No implements");
    }

    default int updateSelective(T t)  {

        throw new QueryException("No implements");
    }

    default List<Map> selectPageByMap(Map<String, Object> param) {

        throw new QueryException("No implements");
    }

    default int updateBatch(List<T> ts) {

        throw new QueryException("No implements");
    }

    default void insertEdge(Object v1, Object e, Object v2) {
        TextResolver textResolver = MapperProxy.ENV.getTextResolver();

        KV kv = notNullFields(e, "p1");
        String cqlTpl = getCqlTpl();
        Class<?> edgeType = e.getClass();
        String edgeName = edgeName(edgeType);

        Field v1PkField = getPkField(v1.getClass());
        Field v2PkField = getPkField(v2.getClass());
        String eId1 = keyFormat(v1PkField, v1PkField.getName(), true, "p0");
        String eId2 = keyFormat(v2PkField, v2PkField.getName(), true, "p2");

        String nGQL = textResolver.resolve(
                cqlTpl,
                new HashMap<String, Object>() {{
                    put( "columns", kv.columns );
                    put( "valueColumns", kv.valueNames );
                    put( "e", edgeName);
                    put( "eId1", eId1);
                    put( "eId2", eId2);
                }}
        );
        proxy( this.getClass(), edgeType, nGQL, new Class[] { Object.class, Object.class, Object.class }, v1, e, v2 );
    }
}



