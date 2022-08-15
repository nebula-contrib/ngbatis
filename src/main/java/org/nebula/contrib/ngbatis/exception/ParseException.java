package org.nebula.contrib.ngbatis.exception;

// Copyright (c) 2022 nebula-contrib. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

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
