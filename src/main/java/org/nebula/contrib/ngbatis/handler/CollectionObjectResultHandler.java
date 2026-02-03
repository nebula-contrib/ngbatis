package org.nebula.contrib.ngbatis.handler;

// Copyright (c) 2022 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import com.vesoft.nebula.client.graph.data.Node;
import com.vesoft.nebula.client.graph.data.Relationship;
import com.vesoft.nebula.client.graph.data.ResultSet;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.nebula.contrib.ngbatis.models.data.NgEdge;
import org.nebula.contrib.ngbatis.models.data.NgVertex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 结果集数据类型转换器。
 * <p> ResultSet -&gt; Collection&lt;Object&gt; </p>
 * @author yeweicheng
 * @since 2022-06-10 22:31
 * <br>Now is history!
 */
@Component
public class CollectionObjectResultHandler extends AbstractResultHandler<Collection, Object> {

  @Autowired
  private ObjectResultHandler objectResultHandler;

  private List<Class<?>> collectionInnerType = Arrays.asList(NgEdge.class, NgVertex.class);

  private boolean isCollectionInnerType(Class<?> resultType) {
    for (Class<?> innerType : collectionInnerType) {
      boolean assignableFrom = innerType.isAssignableFrom(resultType);
      if (assignableFrom) {
        return true;
      }
    }
    return false;
  }

  @Override
  public Collection handle(Collection newResult, ResultSet result, Class resultType)
      throws NoSuchFieldException, IllegalAccessException, InstantiationException {
    List<String> columnNames = result.getColumnNames();
    int size = result.rowsSize();
    for (int i = 0; i < size; i++) {
      Object o = resultType.newInstance();
      ResultSet.Record record = result.rowValues(i);
      o = objectResultHandler.handle(o, record, columnNames, resultType);
      newResult.add(o);
    }
    return newResult;
  }

  public Collection<Object> handle(Collection<Object> nestedCollection, Class<?> resultType) {
    if (isCollectionInnerType(resultType)) {
      try {
        Collection<Object> nestedCollect = defaultInstance(nestedCollection.getClass());
        for (Object o : nestedCollection) {
          if (NgEdge.class.isAssignableFrom(resultType) && o instanceof Relationship) {
            o = objectResultHandler.toEdge((Relationship) o);
          }
          if (NgVertex.class.isAssignableFrom(resultType) && o instanceof Node) {
            o = objectResultHandler.toVertex((Node) o);
          }
          nestedCollect.add(o);
        }
        return nestedCollect;
      } catch (UnsupportedEncodingException e) {
        // 在前置判断中，已经规避了异常发生的可能性
        throw new RuntimeException(e);
      }
    }
    return nestedCollection;
  }

}
