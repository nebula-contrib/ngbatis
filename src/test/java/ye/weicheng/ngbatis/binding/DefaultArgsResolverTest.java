// Copyright 2022-present Weicheng Ye. All rights reserved.
// Use of this source code is governed by a MIT-style license that can be
// found in the LICENSE file.
package ye.weicheng.ngbatis.binding;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author yeweicheng
 * @since 2022-06-22 14:45
 * <br>Now is history!
 */
class DefaultArgsResolverTest {

    @Test
    void customToJSON() throws IOException {

        DefaultArgsResolver defaultArgsResolver = new DefaultArgsResolver();
        Map<String, Object> o = new HashMap<String, Object>() {{
            put("dt", new Date() );
            put("t", new java.sql.Time( System.currentTimeMillis() ) );
            put("d", new java.sql.Date( System.currentTimeMillis() ) );
            put("o", null);
        }};
        Object o1 = defaultArgsResolver.customToJSON(o);
        System.out.println( o1 );
    }
}