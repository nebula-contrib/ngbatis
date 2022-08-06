// Copyright 2022-present Weicheng Ye. All rights reserved.
// Use of this source code is governed by a MIT-style license that can be
// found in the LICENSE file.
package org.nebula.contrib.ngbatis.exception;

/**
 * 解析xml文件时发生的异常
 *
 * @author yeweicheng
 * <br>Now is history!
 */
public class ParseException extends RuntimeException {

    public ParseException( String msg ) {
        super( "映射文件解析异常：" + msg );
    }

}
