package ye.weicheng.ngbatis.demo.repository;

// Copyright (c) 2024 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import com.vesoft.nebula.client.graph.data.ResultSet;
import org.nebula.contrib.ngbatis.annotations.Space;
import org.nebula.contrib.ngbatis.proxy.NebulaDaoBasic;
import org.springframework.data.repository.query.Param;
import ye.weicheng.ngbatis.demo.pojo.Paragraph;

/**
 * @author yeweicheng
 * @since 2024-12-19 9:30
 * <br>Now is history!
 */
@Space(name = "${nebula.ngbatis.test-space-placeholder}")
public interface ParagraphDao extends NebulaDaoBasic<Paragraph, Integer> {
  
  ResultSet testSpaceFromYml();

  ResultSet testSpaceFromParam(@Param("spaceParam") String space);

  ResultSet testSpaceAnno();
  
}
