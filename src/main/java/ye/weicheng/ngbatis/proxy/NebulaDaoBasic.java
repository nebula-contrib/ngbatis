// Copyright 2022-present Weicheng Ye. All rights reserved.
// Use of this source code is governed by a MIT-style license that can be
// found in the LICENSE file.
package ye.weicheng.ngbatis.proxy;

import com.vesoft.nebula.client.graph.data.ResultSet;
import ye.weicheng.ngbatis.TextResolver;
import ye.weicheng.ngbatis.exception.QueryException;
import ye.weicheng.ngbatis.utils.Page;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.*;

import static ye.weicheng.ngbatis.proxy.NebulaDaoBasicExt.*;

/**
 * 数据访问的基类，用于提供单表 CRUD 与基本的节点关系操作<br/>
 * <strong>以下在方法注释中所说的“对应类型” 均指的是 泛 型T</strong>
 *
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
        Class<?> vertexType = entityType( this.getClass() );
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

    default Long countPage(Page<T> page ) {
        TextResolver textResolver = MapperProxy.ENV.getTextResolver();
        String countTpl = getCqlTpl();

        Class<? extends NebulaDaoBasic> daoClass = this.getClass();

        Class[] classes = entityTypeAndIdType(daoClass);
        Class<T> entityType = classes[0];
        String vertexName = vertexName(entityType);

        KV kv = notNullFields(page.entity, "entity");
        HashMap<String, Object> param = new HashMap<String, Object>() {{
            put("columns", kv.columns);
            put("valueColumns", kv.valueNames);
            put("tag", vertexName);
            put("pageSize", page.pageSize);
            put("startRow", page.startRow);
        }};
        String countNGql = textResolver.resolve( countTpl, param );

        return (Long) proxy(daoClass, Long.class, countNGql, new Class[]{Page.class}, page);
    }

    /**
     * 查询对应类型的数据并分页<br/>
     *
     * @param page
     * @return
     */
    default  List<T> selectPage(Page<T> page) {
        Long total = countPage(page);
        page.setTotal( total );
        if (total == 0) return Collections.EMPTY_LIST;

        TextResolver textResolver = MapperProxy.ENV.getTextResolver();

        Class<? extends NebulaDaoBasic> daoClass = this.getClass();

        Class[] classes = entityTypeAndIdType(daoClass);
        Class<T> entityType = classes[0];
        String vertexName = vertexName(entityType);

        KV kv = notNullFields(page.entity, "entity");
        HashMap<String, Object> param = new HashMap<String, Object>() {{
            put("columns", kv.columns);
            put("valueColumns", kv.valueNames);
            put("tag", vertexName);
            put("pageSize", page.pageSize);
            put("startRow", page.startRow);
        }};

        String cqlTpl = getCqlTpl();
        String nGql = textResolver.resolve( cqlTpl, param );

        List<T> proxy = (List<T>) proxy(daoClass, entityType, nGql, new Class[]{Page.class}, page);
        page.setRows( proxy );
        return proxy;
    }

    default int updateBatch(List<T> ts) {

        throw new QueryException("No implements");
    }

    /**
     *  根据三元组值，插入关系
     *
     * @param v1 开始节点值
     * @param e 关系值
     * @param v2 结束节点值
     */
    default void insertEdge(Object v1, Object e, Object v2) {
        if( v2 == null || v1 == null || e == null ) return;
        TextResolver textResolver = MapperProxy.ENV.getTextResolver();

        KV kv = notNullFields(e, "p1");
        String cqlTpl = getCqlTpl();
        Class<?> edgeType = e.getClass();
        String edgeName = edgeName(edgeType);

        Field v1PkField = getPkField(v1.getClass());
        Field v2PkField = getPkField(v2.getClass());
        String eId1 = keyFormat(v1PkField, v1PkField.getName(), true, "p0");
        String eId2 = keyFormat(v2PkField, v2PkField.getName(), true, "p2");

        HashMap<String, Object> tplArgs = new HashMap<String, Object>() {{
            put("columns", kv.columns);
            put("valueColumns", kv.valueNames);
            put("e", edgeName);
            put("eId1", eId1);
            put("eId2", eId2);
        }};

        Field rankField = getRankField( e.getClass() );
        if( rankField != null ) {
            setId( e, rankField, edgeName );
            String rank = keyFormat(rankField, rankField.getName(), true, "p1");
            tplArgs.put( "rank", rank );
        }

        String nGQL = textResolver.resolve( cqlTpl, tplArgs );
        proxy( this.getClass(), edgeType, nGQL, new Class[] { Object.class, Object.class, Object.class }, v1, e, v2 );
    }

    /**
     * 提供开始节点的id、结束节点的id 与 关系名，判断是否已经建立关系
     *
     * @param startId 开始节点的 id
     * @param edgeType 关系类型
     * @param endId 结束节点的 id
     * @return
     */
    default Boolean existsEdge(ID startId, Class<?> edgeType, ID endId) {
        String cqlTpl = getCqlTpl();
        String edgeName = edgeName(edgeType);
        return (Boolean) proxy( this.getClass(), Boolean.class, cqlTpl, new Class[] { Serializable.class, Class.class, Serializable.class }, startId, edgeName, endId );
    };

    /**
     * 通过结束节点id与关系类型获取所有开始节点，<br/>
     * 开始节点类型为当前接口实现类所管理的实体对应的类型
     *
     * @param edgeType 关系类型
     * @param endId 结束节点的 id
     * @return 开始节点
     */
    default List<T> listStartNodes(Class<?> edgeType, ID endId) {
        Class<?> startType = entityType( this.getClass() );
        return (List<T>) listStartNodes( startType, edgeType, endId );
    }

    /**
     * 指定开始节点类型，并通过结束节点id与关系类型获取所有开始节点
     *
     * @param startType 开始节点的类型
     * @param edgeType 关系类型
     * @param endId 结束节点的 id
     * @return 开始节点
     */
    default List<?> listStartNodes(Class<?> startType, Class<?> edgeType, ID endId) {
        String cqlTpl = getCqlTpl();
        String startVertexName = vertexName( startType );
        String edgeName = edgeName( edgeType );
        Class<? extends NebulaDaoBasic> daoType = this.getClass();
        Class<?> returnType = entityType(daoType);
        return (List<?>) proxy(daoType, returnType, cqlTpl, new Class[] {Class.class, Class.class, Serializable.class }, startVertexName, edgeName, endId );
    }

    /**
     * 通过结束节点id与关系类型获取第一个开始节点，<br/>
     * 开始节点类型为当前接口实现类所管理的实体对应的类型 （对应类型）
     *
     * @param edgeType 关系类型
     * @param endId 结束节点的 id
     * @return 开始节点
     */
    default T startNode(Class<?> edgeType, ID endId ) {
        Class<?> startType = entityType( this.getClass() );
        return (T) startNode(startType, edgeType, endId);
    }

    /**
     * 指定开始节点类型，并通过结束节点id与关系类型获取第一个开始节点
     *
     * @param startType 开始节点的类型
     * @param edgeType 关系类型
     * @param endId 结束节点的 id
     * @param <E> 开始节点的类型
     * @return 开始节点
     */
    default <E> E startNode( Class<E> startType, Class<?> edgeType, ID endId ) {
        String cqlTpl = getCqlTpl();
        String startVertexName = vertexName( startType );
        String edgeName = edgeName( edgeType );
        Class<? extends NebulaDaoBasic> daoType = this.getClass();
        Class<?> returnType = entityType(daoType);
        return (E) proxy(daoType, returnType, cqlTpl, new Class[] {Class.class, Class.class, Serializable.class }, startVertexName, edgeName, endId );
    }

}



