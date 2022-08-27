package org.nebula.contrib.ngbatis.exception;

// Copyright (c) 2022 All project authors and nebula-contrib. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

/**
 * 加载所需资源时发生的异常
 *
 * @author yeweicheng
 * <br>Now is history!
 */
public class ResourceLoadException extends RuntimeException{

    public ResourceLoadException() {}
    public ResourceLoadException( String msg ) {
        super( msg );
    }


    public ResourceLoadException( Throwable cause ) {
        super( cause );
    }

}
