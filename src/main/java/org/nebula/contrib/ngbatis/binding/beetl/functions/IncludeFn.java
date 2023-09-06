package org.nebula.contrib.ngbatis.binding.beetl.functions;

import org.beetl.core.Template;
import org.nebula.contrib.ngbatis.models.ClassModel;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * TODO
 * 2023-9-6 14:28 lyw.
 */
public class IncludeFn extends AbstractFunction<String,Void,Void,Void,Void,Void>{

    @Override
    public Object call(String ngql) {
        Map<String, Object> param = ctx.globalVar;
        ClassModel classModel = (ClassModel) param.get("ng_cm");
        if(CollectionUtils.isEmpty(classModel.getNgqls()) || StringUtils.isEmpty(classModel.getNgqls().get(ngql))){
            throw new RuntimeException("未找到 nGQL:" + ngql + " 的定义");
        }
        String text = classModel.getNgqls().get(ngql).getText();
        Template template = ctx.gt.getTemplate(text);
        template.fastBinding(ctx.template.getCtx().globalVar);
        template.renderTo(ctx.byteWriter);
        return null;
    }
}
