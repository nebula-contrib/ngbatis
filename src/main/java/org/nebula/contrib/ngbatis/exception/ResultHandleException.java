package org.nebula.contrib.ngbatis.exception;

// Copyright (c) 2022 All project authors and nebula-contrib. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

/**
 * 处理结果集时发生的异常
 *
 * @author yeweicheng <br>
 *         Now is history!
 */
public class ResultHandleException extends RuntimeException {

    public ResultHandleException(String msg) {
        super("返回结果处理异常：" + msg);
    }

    public ResultHandleException(Throwable cause) {
        super("返回结果处理异常：" + cause.getMessage(), cause);
    }

}
