package org.nebula.contrib.ngbatis.models.ext;

// Copyright (c) 2024 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import static org.nebula.contrib.ngbatis.utils.ReflectUtil.setValue;

import com.vesoft.nebula.client.graph.data.SelfSignedSSLParam;

/**
 * 属性可设置的自签名 SSL 参数
 * 
 * @author yeweicheng
 * @since 2024-07-05 4:35
 * <br>Now is history!
 */
public class SettableSelfSignedSSLParam extends SelfSignedSSLParam {

  public SettableSelfSignedSSLParam() {
    super(null, null, null);
  }
  
  public void setPassword(String password)
      throws NoSuchFieldException, IllegalAccessException {
    setValue(this, "password", password);
  }
  
  public void setCrtFilePath(String crtFilePath)
      throws NoSuchFieldException, IllegalAccessException {
    setValue(this, "crtFilePath", crtFilePath);
  }
  
  public void setKeyFilePath(String keyFilePath)
      throws NoSuchFieldException, IllegalAccessException {
    setValue(this, "keyFilePath", keyFilePath);
  }
  
}
