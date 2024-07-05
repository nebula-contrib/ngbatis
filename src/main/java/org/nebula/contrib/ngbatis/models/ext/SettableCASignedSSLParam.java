package org.nebula.contrib.ngbatis.models.ext;

// Copyright (c) 2024 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import static org.nebula.contrib.ngbatis.utils.ReflectUtil.setValue;

import com.vesoft.nebula.client.graph.data.CASignedSSLParam;

/**
 * 属性可设置的 CA 签名 SSL 参数
 * 
 * @author yeweicheng
 * @since 2024-07-05 4:27
 * <br>Now is history!
 */
public class SettableCASignedSSLParam extends CASignedSSLParam {

  public void setCaCrtFilePath(String caCrtFilePath) 
      throws NoSuchFieldException, IllegalAccessException {
    setValue(this, "caCrtFilePath", caCrtFilePath);
  }

  public void setCrtFilePath(String certFilePath)
      throws NoSuchFieldException, IllegalAccessException {
    setValue(this, "crtFilePath", certFilePath);
  }

  public void setKeyFilePath(String keyFilePath)
      throws NoSuchFieldException, IllegalAccessException {
    setValue(this, "keyFilePath", keyFilePath);
  }

}
