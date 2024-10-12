package org.nebula.contrib.kg;

// Copyright (c) 2024 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * Hello world!
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class},
  scanBasePackages = {"org.nebula.contrib", "org.nebula.contrib.kg"})
public class KgApiApp {

  public static void main(String[] args) {
    SpringApplication app = new SpringApplication(KgApiApp.class);
    app.run(args);
  }
}
