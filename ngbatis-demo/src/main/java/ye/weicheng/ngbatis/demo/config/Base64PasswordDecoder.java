package ye.weicheng.ngbatis.demo.config;

// Copyright (c) 2024 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import java.util.Base64;
import org.nebula.contrib.ngbatis.PasswordDecoder;
import org.springframework.stereotype.Component;

/**
 * yml 明码解密器的组建示例，使用 Base64 的方式
 * 如 yml 使用的是明文密码，则不需要这个 bean
 * 
 * @author yeweicheng
 * @since 2024-05-23 7:39
 * <br>Now is history!
 */
@Component
public class Base64PasswordDecoder implements PasswordDecoder {

  @Override
  public String decode(String password) {
    return new String(Base64.getDecoder().decode(password));
  }

}
