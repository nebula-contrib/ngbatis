package org.nebula.contrib.ngbatis.binding.beetl.functions;

import org.beetl.core.Template;
import org.nebula.contrib.ngbatis.models.ClassModel;
import org.nebula.contrib.ngbatis.models.MapperContext;
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
        if(StringUtils.isEmpty(ngql)){
            throw new RuntimeException("未指定nGQL片段");
        }
        int idx = ngql.lastIndexOf(".");
        ClassModel classModel;
        String ngqlId;
        if(idx < 0){
            ngqlId = ngql;
            Map<String, Object> param = ctx.globalVar;
            classModel = (ClassModel) param.get("ng_cm");
        }else{
            String namespace = ngql.substring(0,idx);
            ngqlId = ngql.substring(idx + 1);
            classModel = MapperContext.newInstance().getInterfaces().get(namespace + ClassModel.PROXY_SUFFIX);
        }
        if(CollectionUtils.isEmpty(classModel.getNgqls()) || StringUtils.isEmpty(classModel.getNgqls().get(ngqlId))){
            throw new RuntimeException("未找到 nGQL(" + ngql + ") 的定义");
        }
        String text = classModel.getNgqls().get(ngqlId).getText();
        Template template = ctx.gt.getTemplate(text);
        template.fastBinding(ctx.template.getCtx().globalVar);
        template.renderTo(ctx.byteWriter);
        return null;
    }
}
