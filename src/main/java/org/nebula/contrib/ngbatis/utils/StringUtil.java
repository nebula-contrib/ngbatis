package org.nebula.contrib.ngbatis.utils;

// Copyright (c) 2022 nebula-contrib. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;

import java.util.Arrays;

/**
 * @author yeweicheng
 * @since 2022-06-12 23:25
 * <br>Now is history!
 */
public class StringUtil {

    public static String xX2x_x (String xX) {
        String[] uUsers = StringUtils.splitByCharacterTypeCamelCase(xX);
        return Strings.join(Arrays.stream(uUsers).iterator(), '_').toLowerCase();
    }

}
