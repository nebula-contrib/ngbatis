// Copyright 2022-present Weicheng Ye. All rights reserved.
// Use of this source code is governed by a MIT-style license that can be
// found in the LICENSE file.
package ye.weicheng.ngbatis;

import java.util.Map;

/**
 * 参数格式化器
 *
 * @author yeweicheng
 * <br>Now is history!
 */
public interface ArgNameFormatter {

    CqlAndArgs format(String oldArgName,  Map<String, Object> obj );

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
