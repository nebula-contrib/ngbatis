package org.nebula.contrib.ngbatis.binding.beetl.functions;

import org.beetl.core.Template;
import org.nebula.contrib.ngbatis.models.ClassModel;
import org.nebula.contrib.ngbatis.models.MapperContext;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * TODO
 * 2023-9-6 14:28 lyw.
 */
public class IncludeFn extends AbstractFunction<String,Map<String,Object>,Void,Void,Void,Void>{

    @Override
    public Object call(String ngql,Map<String,Object> args) {
        if(StringUtils.isEmpty(ngql)){
            throw new RuntimeException("未指定nGQL片段");
        }
        int idx = ngql.lastIndexOf(".");
        ClassModel classModel;
        String ngqlId;
        if(idx < 0){
            ngqlId = ngql;
            classModel = (ClassModel) ctx.globalVar.get("ng_cm");
        }else{
            String namespace = ngql.substring(0,idx);
            ngqlId = ngql.substring(idx + 1);
            classModel = MapperContext.newInstance().getInterfaces().get(namespace + ClassModel.PROXY_SUFFIX);
        }
        if(CollectionUtils.isEmpty(classModel.getNgqls()) || StringUtils.isEmpty(classModel.getNgqls().get(ngqlId))){
            throw new RuntimeException("未找到 nGQL(" + ngql + ") 的定义");
        }
        Map<String,Object> param;
        if(!CollectionUtils.isEmpty(args)){
            //防止同名的 子片段参数 覆盖 父片段参数，导致渲染结果与预期不一致。
            param = new LinkedHashMap<>(ctx.globalVar);
            param.putAll(args);
        }else{
            param = ctx.globalVar;
        }
        String text = classModel.getNgqls().get(ngqlId).getText();
        Template template = ctx.gt.getTemplate(text);
        template.fastBinding(param);
        template.renderTo(ctx.byteWriter);
        return null;
    }
}
