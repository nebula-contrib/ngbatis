// Copyright 2022-present Weicheng Ye. All rights reserved.
// Use of this source code is governed by a MIT-style license that can be
// found in the LICENSE file.
package ye.weicheng.ngbatis.config;

import org.apache.logging.log4j.util.Strings;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * xml 相关的配置参数
 *
 * @author yeweicheng
 * <br>Now is history!
 */
@Component
@ConfigurationProperties( prefix="cql.parser")
public class ParseCfgProps {

    private String mapperLocations = "classpath:mapper/*.xml";

    private String id = "id";

    private String namespace = "namespace";

    private String resultType = "resultType";

    private String parameterType = "parameterType";

    private String mapper = "mapper";

    private String statementStart = "@";

    private String statementEnd = null;

    private String logShow = "";

    private Set<String> logShowTypes = null;

    private boolean resourceRefresh = false;

    public ParseCfgProps() {
        setLogShow( "xml-load,env-init,query");
    }

    public void setLogShow( String logShow ) {
        if(Strings.isBlank( logShow ) ) return;
        this.logShow = logShow;
        logShowTypes = new HashSet<>(Arrays.asList(logShow.split( ",")));
    }

    public String getMapperLocations() {
        return mapperLocations;
    }

    public void setMapperLocations(String mapperLocations) {
        this.mapperLocations = mapperLocations;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getResultType() {
        return resultType;
    }

    public void setResultType(String resultType) {
        this.resultType = resultType;
    }

    public String getParameterType() {
        return parameterType;
    }

    public void setParameterType(String parameterType) {
        this.parameterType = parameterType;
    }

    public String getMapper() {
        return mapper;
    }

    public void setMapper(String mapper) {
        this.mapper = mapper;
    }

    public String getStatementStart() {
        return statementStart;
    }

    public void setStatementStart(String statementStart) {
        this.statementStart = statementStart;
    }

    public String getStatementEnd() {
        return statementEnd;
    }

    public void setStatementEnd(String statementEnd) {
        this.statementEnd = statementEnd;
    }

    public String getLogShow() {
        return logShow;
    }

    public Set<String> getLogShowTypes() {
        return logShowTypes;
    }

    public void setLogShowTypes(Set<String> logShowTypes) {
        this.logShowTypes = logShowTypes;
    }

    public boolean isResourceRefresh() {
        return resourceRefresh;
    }

    public void setResourceRefresh(boolean resourceRefresh) {
        this.resourceRefresh = resourceRefresh;
    }
}
