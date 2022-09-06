package ye.weicheng.ngbatis.demo;

// Copyright (c) 2022 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class}, scanBasePackages = {
    "ye.weicheng", "org.nebula.contrib"})
//@EnableAutoConfiguration
@EnableCaching
public class NgbatisDemoApplication {

  public static void main(String[] args) {
    SpringApplication app = new SpringApplication(NgbatisDemoApplication.class);
    app.run(args);
  }

}
