// Copyright 2022-present Weicheng Ye. All rights reserved.
// Use of this source code is governed by a MIT-style license that can be
// found in the LICENSE file.
package ye.weicheng.ngbatis.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.nebula.contrib.ngbatis.PkGenerator;
import org.nebula.contrib.ngbatis.binding.TimestampPkGenerator;

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
