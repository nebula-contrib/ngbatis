// Copyright 2022-present Weicheng Ye. All rights reserved.
// Use of this source code is governed by a MIT-style license that can be
// found in the LICENSE file.
package org.nebula.contrib.ngbatis.exception;

/**
 * 处理结果集时发生的异常
 *
 * @author yeweicheng
 * <br>Now is history!
 */
public class ResultHandleException extends RuntimeException {

    public ResultHandleException( String msg ) {
        super( "返回结果处理异常：" + msg );
    }

    public ResultHandleException( Throwable cause ) {
        super( "返回结果处理异常：" + cause.getMessage() , cause );
    }

}
