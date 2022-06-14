// Copyright 2022-present Weicheng Ye. All rights reserved.
// Use of this source code is governed by a MIT-style license that can be
// found in the LICENSE file.
package ye.weicheng.ngbatis;

/**
 * 主键生成器接口<br>
 *     项目中可通过实现当前接口，并注册成 Component 即可完成注入
 *
 * @author yeweicheng
 * @since 2022-06-14 11:39
 * <br>Now is history!
 */
public interface PkGenerator {

    <T> T generate(String tagName, Class<T> pkType);

}
