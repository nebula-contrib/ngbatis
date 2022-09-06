package ye.weicheng.ngbatis.demo.config;

// Copyright (c) 2022 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import org.nebula.contrib.ngbatis.PkGenerator;
import org.nebula.contrib.ngbatis.binding.TimestampPkGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 主键生成样例
 *
 * @author yeweicheng
 * @since 2022-06-14 12:32
 * <br>Now is history!
 */
@Configuration
public class PkGeneratorConfig {

  @Bean
  public PkGenerator pkGenerator() {
    return new TimestampPkGenerator();
  }

}
