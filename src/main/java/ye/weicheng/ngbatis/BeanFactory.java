// Copyright 2022-present Weicheng Ye. All rights reserved.
// Use of this source code is governed by a MIT-style license that can be
// found in the LICENSE file.
package ye.weicheng.ngbatis;

import ye.weicheng.ngbatis.models.ClassModel;
import org.objectweb.asm.Opcodes;

/**
 * 代理Bean工厂
 *
 * @author yeweicheng
 * <br>Now is history!
 */
public interface BeanFactory extends Opcodes {

    byte[] setClassCode(ClassModel cm) ;

}
