package org.nebula.contrib.ngbatis;

// Copyright (c) 2022 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.
import java.util.Map;

/**.
 * 参数格式化器.
 *.
 * @author yeweicheng <br>.
 *     Now is history.
.*/
public interface ArgNameFormatter {

  CqlAndArgs format(String oldArgName, Map<String, Object> obj);

  class CqlAndArgs {
    public CqlAndArgs(String cql, Map<String, Object> args) {
      this.cql = cql;
      this.args = args;
    }

    private String cql;
    private Map<String, Object> args;

    public String getCql() {
      return cql;
    }

    public void setCql(String cql) {
      this.cql = cql;
    }

    public Map<String, Object> getArgs() {
      return args;
    }

    public void setArgs(Map<String, Object> args) {
      this.args = args;
    }
  }
}
