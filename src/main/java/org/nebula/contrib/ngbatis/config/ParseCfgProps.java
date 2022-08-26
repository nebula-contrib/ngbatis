package org.nebula.contrib.ngbatis.config;

// Copyright (c) 2022 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.
import static org.apache.commons.lang3.ObjectUtils.isEmpty;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.apache.logging.log4j.util.Strings;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**.
 * xml 相关的配置参数.
 *.
 * @author yeweicheng <br>.
 *     Now is history.
.*/
@Component
@ConfigurationProperties(prefix = "cql.parser")
public class ParseCfgProps {

  private String mapperTplLocation = "NebulaDaoBasic.xml";

  private String mapperLocations = "mapper/**/*.xml";

  private String id = "id";

  private String namespace = "namespace";

  private String space = "space";

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

  public ParseCfgProps setLogShow(final String logShow) {
    if (Strings.isBlank(logShow)) {
      return this;
    }
    this.logShow = logShow;
    logShowTypes = new HashSet<>(Arrays.asList(logShow.split(",")));
    return this;
  }

  public String getMapperTplLocation() {
    return mapperTplLocation;
  }

  public ParseCfgProps setMapperTplLocation(final String mapperTplLocation) {
    if (Strings.isBlank(mapperTplLocation)) {
      return this;
    }
    this.mapperTplLocation = mapperTplLocation;
    return this;
  }

  public String getMapperLocations() {
    return mapperLocations;
  }

  public ParseCfgProps setMapperLocations(final String mapperLocations) {
    if (Strings.isBlank(mapperLocations)) {
      return this;
    }
    this.mapperLocations = mapperLocations;
    return this;
  }

  public String getId() {
    return id;
  }

  public ParseCfgProps setId(final String id) {
    if (Strings.isBlank(id)) {
      return this;
    }
    this.id = id;
    return this;
  }

  public String getNamespace() {
    return namespace;
  }

  public ParseCfgProps setNamespace(final String namespace) {
    if (Strings.isBlank(namespace)) {
      return this;
    }
    this.namespace = namespace;
    return this;
  }

  public String getSpace() {
    return space;
  }

  public void setSpace(final String space) {
    this.space = space;
  }

  public String getResultType() {
    return resultType;
  }

  public ParseCfgProps setResultType(final String resultType) {
    if (Strings.isBlank(resultType)) {
      return this;
    }
    this.resultType = resultType;
    return this;
  }

  public String getParameterType() {
    return parameterType;
  }

  public ParseCfgProps setParameterType(final String parameterType) {
    if (Strings.isBlank(parameterType)) {
      return this;
    }
    this.parameterType = parameterType;
    return this;
  }

  public String getMapper() {
    return mapper;
  }

  public ParseCfgProps setMapper(final String mapper) {
    if (Strings.isBlank(mapper)) {
      return this;
    }
    this.mapper = mapper;
    return this;
  }

  public String getStatementStart() {
    return statementStart;
  }

  public ParseCfgProps setStatementStart(final String statementStart) {
    if (Strings.isBlank(statementStart)) {
      return this;
    }
    this.statementStart = statementStart;
    return this;
  }

  public String getStatementEnd() {
    return statementEnd;
  }

  public ParseCfgProps setStatementEnd(final String statementEnd) {
    if (Strings.isBlank(statementEnd)) {
      return this;
    }
    this.statementEnd = statementEnd;
    return this;
  }

  public String getLogShow() {
    return logShow;
  }

  public Set<String> getLogShowTypes() {
    return logShowTypes;
  }

  public ParseCfgProps setLogShowTypes(final Set<String> logShowTypes) {
    if (isEmpty(logShowTypes)) {
      return this;
    }
    this.logShowTypes = logShowTypes;
    return this;
  }

  public boolean isResourceRefresh() {
    return resourceRefresh;
  }

  public ParseCfgProps setResourceRefresh(final Boolean resourceRefresh) {
    if (resourceRefresh == null) {
      return this;
    }
    this.resourceRefresh = resourceRefresh;
    return this;
  }
}
