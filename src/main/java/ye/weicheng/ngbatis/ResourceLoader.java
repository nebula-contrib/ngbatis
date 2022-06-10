// Copyright 2022-present Weicheng Ye. All rights reserved.
// Use of this source code is governed by a MIT-style license that can be
// found in the LICENSE file.
package ye.weicheng.ngbatis;

import ye.weicheng.ngbatis.models.ClassModel;
import org.springframework.core.io.Resource;
import ye.weicheng.ngbatis.models.MapperContext;

import java.io.IOException;
import java.util.Map;

/**
 * xml 资源加载器，与 接口解析器。并将获取到的结果填充到 {@link MapperContext} 中
 *
 * @author yeweicheng
 * <br>Now is history!
 */
public interface ResourceLoader {

    Map<String, ClassModel> load();
    Map<String,ClassModel> parseClassModel( Resource resource ) throws IOException;

}
