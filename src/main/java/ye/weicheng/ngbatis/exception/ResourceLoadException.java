// Copyright 2022-present Weicheng Ye. All rights reserved.
// Use of this source code is governed by a MIT-style license that can be
// found in the LICENSE file.
package ye.weicheng.ngbatis.exception;

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
