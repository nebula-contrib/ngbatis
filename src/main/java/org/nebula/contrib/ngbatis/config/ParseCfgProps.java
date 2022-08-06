// Copyright 2022-present Weicheng Ye. All rights reserved.
// Use of this source code is governed by a MIT-style license that can be
// found in the LICENSE file.
package org.nebula.contrib.ngbatis.config;

import org.apache.logging.log4j.util.Strings;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.apache.commons.lang3.ObjectUtils.isEmpty;

/**
 * xml 相关的配置参数
 *
 * @author yeweicheng
 * <br>Now is history!
 */
@Component
@ConfigurationProperties( prefix="cql.parser")
public class ParseCfgProps {

    private String mapperTplLocation = "NebulaDaoBasic.xml";

    private String mapperLocations = "mapper/**/*.xml";

    private String id = "id";

    private String namespace = "namespace";

    private String resultType = "resultType";

    private String parameterType = "parameterType";

    private String mapper = "mapper";

    private String statementStart = "@";

    private String statementEnd = null;

    private String logShow = "xml-load,env-init,query";

    private Set<String> logShowTypes = null;

    private boolean resourceRefresh = false;

    public ParseCfgProps() {
    }

    public ParseCfgProps setLogShow( String logShow ) {
        if(Strings.isBlank( logShow ) ) return this;
        this.logShow = logShow;
        logShowTypes = new HashSet<>(Arrays.asList(logShow.split( ",")));
        return this;
    }

    public String getMapperTplLocation() {
        return mapperTplLocation;
    }

    public ParseCfgProps setMapperTplLocation(String mapperTplLocation) {
        if(Strings.isBlank( mapperTplLocation ) ) return this;
        this.mapperTplLocation = mapperTplLocation;
        return this;
    }

    public String getMapperLocations() {
        return mapperLocations;
    }

    public ParseCfgProps setMapperLocations(String mapperLocations) {
        if(Strings.isBlank( mapperLocations ) ) return this;
        this.mapperLocations = mapperLocations;
        return this;
    }

    public String getId() {
        return id;
    }

    public ParseCfgProps setId(String id) {
        if(Strings.isBlank( id ) ) return this;
        this.id = id;
        return this;
    }

    public String getNamespace() {
        return namespace;
    }

    public ParseCfgProps setNamespace(String namespace) {
        if(Strings.isBlank( namespace ) ) return this;
        this.namespace = namespace;
        return this;
    }

    public String getResultType() {
        return resultType;
    }

    public ParseCfgProps setResultType(String resultType) {
        if(Strings.isBlank( resultType ) ) return this;
        this.resultType = resultType;
        return this;
    }

    public String getParameterType() {
        return parameterType;
    }

    public ParseCfgProps setParameterType(String parameterType) {
        if(Strings.isBlank( parameterType ) ) return this;
        this.parameterType = parameterType;
        return this;
    }

    public String getMapper() {
        return mapper;
    }

    public ParseCfgProps setMapper(String mapper) {
        if(Strings.isBlank( mapper ) ) return this;
        this.mapper = mapper;
        return this;
    }

    public String getStatementStart() {
        return statementStart;
    }

    public ParseCfgProps setStatementStart(String statementStart) {
        if(Strings.isBlank( statementStart ) ) return this;
        this.statementStart = statementStart;
        return this;
    }

    public String getStatementEnd() {
        return statementEnd;
    }

    public ParseCfgProps setStatementEnd(String statementEnd) {
        if(Strings.isBlank( statementEnd ) ) return this;
        this.statementEnd = statementEnd;
        return this;
    }

    public String getLogShow() {
        return logShow;
    }

    public Set<String> getLogShowTypes() {
        return logShowTypes;
    }

    public ParseCfgProps setLogShowTypes(Set<String> logShowTypes) {
        if( isEmpty( logShowTypes ) ) return this;
        this.logShowTypes = logShowTypes;
        return this;
    }

    public boolean isResourceRefresh() {
        return resourceRefresh;
    }

    public ParseCfgProps setResourceRefresh(Boolean resourceRefresh) {
        if( resourceRefresh == null ) return this;
        this.resourceRefresh = resourceRefresh;
        return this;
    }
}
