// Copyright 2022-present Weicheng Ye. All rights reserved.
// Use of this source code is governed by a MIT-style license that can be
// found in the LICENSE file.
package ye.weicheng.ngbatis.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import ye.weicheng.ngbatis.Env;

@SpringBootApplication(exclude={DataSourceAutoConfiguration.class}, scanBasePackages = "ye.weicheng")
//@EnableAutoConfiguration
public class NgbatisDemoApplication {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(NgbatisDemoApplication.class);
		Env.classLoader = app.getClassLoader();
		app.run(args);
	}

}
