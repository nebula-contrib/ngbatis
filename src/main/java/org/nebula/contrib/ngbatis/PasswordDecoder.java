package org.nebula.contrib.ngbatis;

// Copyright (c) 2024 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

/**
 * 用于：从配置中得到的密码，可以解密获得明文密码
 * 
 * @author yeweicheng
 * @since 2024-05-23 7:31
 * <br>Now is history!
 */
public interface PasswordDecoder {
  
  String decode(String password);

}
