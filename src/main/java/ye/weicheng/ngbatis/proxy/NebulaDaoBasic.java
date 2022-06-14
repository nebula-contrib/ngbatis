// Copyright 2022-present Weicheng Ye. All rights reserved.
// Use of this source code is governed by a MIT-style license that can be
// found in the LICENSE file.
package ye.weicheng.ngbatis.proxy;

import com.vesoft.nebula.client.graph.data.ResultSet;
import ye.weicheng.ngbatis.exception.QueryException;
import java.io.Serializable;
import java.util.*;

import static ye.weicheng.ngbatis.proxy.NebulaDaoBasicExt.proxy;
import static ye.weicheng.ngbatis.proxy.NebulaDaoBasicExt.recordToQL;

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
    default int deleteLogicByPrimaryKey(ID id) {
        throw new QueryException("No implements");
    }

    /**
     * <p>通过 主键删除当前记录</p>
     *
     * @param id 表记录主键
     * @return
     */
    default int deleteByPrimaryKey(ID id) {
        throw new QueryException("No implements");
    }

    /**
     * <p>插入一条记录，全属性插入</p>
     *
     * @param record 当前表对应的记录数据
     * @return
     */
    default int insert(T record) {
        String nGQL = recordToQL( record, false );
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
        String nGQL = recordToQL( record, true );
        ResultSet resultSet = (ResultSet)proxy(this.getClass(),  ResultSet.class,  nGQL, new Class[] { Object.class }, record );
        return resultSet.isSucceeded() ? 1: 0;
    }


    /**
     * <p>通过主键查询对应表的单条记录</p>
     *
     * @param id 记录主键
     * @return 表中的记录对应的实体对象
     */
    default T selectByPrimaryKey(ID id) {
        throw new QueryException("No implements");
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

        throw new QueryException("No implements");
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
    default int updateByPrimaryKeySelective(T record) {

        throw new QueryException("No implements");
    }

    default int updateByPrimaryKeyWithBLOBs(T record) {

        throw new QueryException("No implements");
    }

    default int updateByPrimaryKey(T record) {

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
}



