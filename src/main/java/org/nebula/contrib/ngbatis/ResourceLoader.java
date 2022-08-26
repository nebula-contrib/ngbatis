package org.nebula.contrib.ngbatis;

// Copyright (c) 2022 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.
import java.io.IOException;
import java.util.Map;
import org.nebula.contrib.ngbatis.models.ClassModel;
import org.springframework.core.io.Resource;

/**.
 * xml 资源加载器，与 接口解析器。并将获取到的结果填充到 {@link MapperContext} 中.
 *.
 * @author yeweicheng <br>.
 *     Now is history.
.*/
public interface ResourceLoader {

  Map<String, ClassModel> load();

  Map<String, ClassModel> parseClassModel(Resource resource) throws IOException;
}
