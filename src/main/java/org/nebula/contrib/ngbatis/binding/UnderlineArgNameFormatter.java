package org.nebula.contrib.ngbatis.binding;

// Copyright (c) 2022 All project authors and nebula-contrib. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.
import org.nebula.contrib.ngbatis.ArgNameFormatter;
import org.nebula.contrib.ngbatis.utils.KeySymbolMap;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 查询语句的中，下划线参数占位符的渲染器 FIXME 当前值支持一次 . 运算，需要修改格式化规则，形成不限制次数的 . 运算符运算
 *
 * @author yeweicheng <br>
 *         Now is history!
 */
@Component
public class UnderlineArgNameFormatter implements ArgNameFormatter {
    @Override
    public CqlAndArgs format(String oldText, Map<String, Object> obj) {
        String newText = textFormat(oldText, obj);
        return new CqlAndArgs(newText, argObjRename(obj));
    }

    private static String textFormat(String text, Map<String, Object> obj) {
        Pattern compile = Pattern
                .compile("\\{\\s{0,}\\w{0,}.\\w{0,}\\s{0,}\\}");
        Matcher matcher = compile.matcher(text);
        Map<String, Integer> elCount = new HashMap<>();
        while (matcher.find()) {
            String target = matcher.group();
            String key = target.split("\\.")[0].replace("{", "").trim();

            if (obj.get(key) instanceof Collection) {
                String countKey = target.replaceAll("\\s{0,}", "");
                Integer integer = elCount.get(countKey);
                if (integer == null) {
                    integer = 0;
                }
                text = text.replaceFirst(replaceRegExpSymbol(target),
                        target.replaceAll("\\.", "_" + integer + "_"));
                elCount.put(countKey, integer + 1);
            } else {
                text = text.replaceFirst(replaceRegExpSymbol(target),
                        target.replaceAll("\\.", "_"));
            }
        }
        return text;
    }

    public static String replaceRegExpSymbol(String text) {
        return text.replace("{", "\\{").replace("}", "\\}");
    }

    public Map<String, Object> argObjRename(Map<String, Object> obj) {
        return new KeySymbolMap(obj, "_").getOneDMap();
    }

}
