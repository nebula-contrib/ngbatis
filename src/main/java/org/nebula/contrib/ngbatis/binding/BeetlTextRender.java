package org.nebula.contrib.ngbatis.binding;

// Copyright (c) 2022 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.
import org.nebula.contrib.ngbatis.TextResolver;
import org.nebula.contrib.ngbatis.config.ParseCfgProps;
import org.beetl.core.Configuration;
import org.beetl.core.GroupTemplate;
import org.beetl.core.Template;
import org.beetl.core.resource.StringTemplateResourceLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

/**
 * 查询语句的中，参数占位符的渲染器
 *
 * @author yeweicheng <br>
 *         Now is history!
 */
@Component
public class BeetlTextRender implements TextResolver {

    @Autowired
    private ParseCfgProps props;

    private GroupTemplate gt = null;

    @Override
    public String resolve(String text, Map<String, Object> args) {
        initGt();
        Template template = this.gt.getTemplate(text);
        template.fastBinding(args);
        return template.render();
    }

    private void initGt() {
        try {
            if (gt == null) {
                StringTemplateResourceLoader resourceLoader
                  = new StringTemplateResourceLoader();
                Configuration cfg = Configuration.defaultConfiguration();
                cfg.setStatementStart(props.getStatementStart());
                cfg.setStatementEnd(props.getStatementEnd());
                GroupTemplate gt = new GroupTemplate(resourceLoader, cfg);
                this.gt = gt;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ParseCfgProps getProps() {
        return props;
    }

    public void setProps(ParseCfgProps props) {
        this.props = props;
    }

}
