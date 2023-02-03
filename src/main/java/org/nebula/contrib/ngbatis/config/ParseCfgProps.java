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

/**
 * <p>xml 相关的配置参数。</p>
 * <p/>
 * @author yeweicheng
 * <br>Now is history!
 */
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

  /**
   * session存活有效期
   */
  public Long sessionLifeLength;

  /**
   * session健康检测间隔
   */
  public Long checkFixedRate;

  public ParseCfgProps() {
  }

  public String getMapperTplLocation() {
    return mapperTplLocation;
  }

  /**
   * <p>设置NebulaDaoBasic对应mapper xml存放的路径。</p>
   * @param mapperTplLocation NebulaDaoBasic.xml 存放路径
   * @return
   */
  public ParseCfgProps setMapperTplLocation(String mapperTplLocation) {
    if (Strings.isBlank(mapperTplLocation)) {
      return this;
    }
    this.mapperTplLocation = mapperTplLocation;
    return this;
  }

  /**
   * <p>获取开发者业务dao对应xml所存放的路径。</p>
   * @return 开发者业务dao对应xml所存放的路径
   */
  public String getMapperLocations() {
    return mapperLocations;
  }

  /**
   * <p>设置开发者业务dao对应xml所存放的路径。</p>
   * @param mapperLocations  业务dao对应xml所存放的路径
   * @return 解析配置（本应是 void，为支持链式调用而改）
   */
  public ParseCfgProps setMapperLocations(String mapperLocations) {
    if (Strings.isBlank(mapperLocations)) {
      return this;
    }
    this.mapperLocations = mapperLocations;
    return this;
  }

  /**
   * <p>获取mapper标签中用来表示dao方法名的属性，默认为id。</p>
   * 
   * @return
   */
  public String getId() {
    return id;
  }

  /**
   * <p>设置mapper标签中用来表示dao方法名的属性。</p>
   * @param id xml 属性名
   * @return 解析配置（本应是 void，为支持链式调用而改）
   */
  public ParseCfgProps setId(String id) {
    if (Strings.isBlank(id)) {
      return this;
    }
    this.id = id;
    return this;
  }

  public String getNamespace() {
    return namespace;
  }

  /**
   * <p>用来设置接口类名的 xml标签属性名</p>
   * @param namespace 声明 接口类名的 xml标签属性名
   * @return 解析配置（本应是 void，为支持链式调用而改）
   */
  public ParseCfgProps setNamespace(String namespace) {
    if (Strings.isBlank(namespace)) {
      return this;
    }
    this.namespace = namespace;
    return this;
  }

  public String getSpace() {
    return space;
  }

  public void setSpace(String space) {
    this.space = space;
  }

  public String getResultType() {
    return resultType;
  }

  /**
   * <p>设置 xml 中，接口方法对应标签用来声明返回值类型的xml属性名。</p>
   * @param resultType 返回值类型（接口是集合时，则为其范型）的xml属性名
   * @return 解析配置（本应是 void，为支持链式调用而改）
   */
  public ParseCfgProps setResultType(String resultType) {
    if (Strings.isBlank(resultType)) {
      return this;
    }
    this.resultType = resultType;
    return this;
  }

  public String getParameterType() {
    return parameterType;
  }

  /**
   * <p>设置 xml 中，接口方法对应标签用来声明参数类型的xml属性名。</p>
   * @param parameterType 参数类型的xml属性名
   * @return 解析配置（本应是 void，为支持链式调用而改）
   */
  public ParseCfgProps setParameterType(String parameterType) {
    if (Strings.isBlank(parameterType)) {
      return this;
    }
    this.parameterType = parameterType;
    return this;
  }

  public String getMapper() {
    return mapper;
  }

  /**
   * <p>设置xml的根标签，默认为：mapper.</p>
   * @param mapper xml的根标签
   * @return 解析配置（本应是 void，为支持链式调用而改）
   */
  public ParseCfgProps setMapper(String mapper) {
    if (Strings.isBlank(mapper)) {
      return this;
    }
    this.mapper = mapper;
    return this;
  }

  public String getStatementStart() {
    return statementStart;
  }

  /**
   * <p>设置 beetl 的开始标志</p>
   * @param statementStart xml中，beetl 的开始标志
   * @return 解析配置（本应是 void，为支持链式调用而改）
   */
  public ParseCfgProps setStatementStart(String statementStart) {
    if (Strings.isBlank(statementStart)) {
      return this;
    }
    this.statementStart = statementStart;
    return this;
  }

  public String getStatementEnd() {
    return statementEnd;
  }

  /**
   * 设置 beetl 的结束符
   * @param statementEnd beetl 的结束符
   * @return 解析配置（本应是 void，为支持链式调用而改）
   */
  public ParseCfgProps setStatementEnd(String statementEnd) {
    if (Strings.isBlank(statementEnd)) {
      return this;
    }
    this.statementEnd = statementEnd;
    return this;
  }

  public String getLogShow() {
    return logShow;
  }

  /**
   * <p>设置打印的日志分类。</p>
   * @param logShow 打印的日志字符串，以“," 分隔形成集合
   * @return 解析配置（本应是 void，为支持链式调用而改）
   */
  public ParseCfgProps setLogShow(String logShow) {
    if (Strings.isBlank(logShow)) {
      return this;
    }
    this.logShow = logShow;
    logShowTypes = new HashSet<>(Arrays.asList(logShow.split(",")));
    return this;
  }

  public Set<String> getLogShowTypes() {
    return logShowTypes;
  }

  /**
   * <p>设置打印的日志分类。</p>
   * @param logShowTypes 打印的日志分类集合
   * @return 解析配置（本应是 void，为支持链式调用而改）
   */
  public ParseCfgProps setLogShowTypes(Set<String> logShowTypes) {
    if (isEmpty(logShowTypes)) {
      return this;
    }
    this.logShowTypes = logShowTypes;
    return this;
  }

  public boolean isResourceRefresh() {
    return resourceRefresh;
  }

  /**
   * <p>运行时是否读取实时的xml</p>
   * @param resourceRefresh 实时读取xml
   * @return 解析配置（本应是 void，为支持链式调用而改）
   */
  public ParseCfgProps setResourceRefresh(Boolean resourceRefresh) {
    if (resourceRefresh == null) {
      return this;
    }
    this.resourceRefresh = resourceRefresh;
    return this;
  }

  public Long getSessionLifeLength() {
    return sessionLifeLength;
  }

  /**
   * session存活有效期
   * @param sessionLifeLength 单位毫秒
   * @return null 或者 正数
   */
  public ParseCfgProps setSessionLifeLength(Long sessionLifeLength) {
    if (sessionLifeLength == null || sessionLifeLength <= 0) {
      return this;
    }
    this.sessionLifeLength = sessionLifeLength;
    return this;
  }

  public Long getCheckFixedRate() {
    return checkFixedRate;
  }

  /**
   * session健康检测间隔
   * @param checkFixedRate 单位毫秒
   * @return null 或者 正数
   */
  public ParseCfgProps setCheckFixedRate(Long checkFixedRate) {
    if (checkFixedRate == null || checkFixedRate <= 0) {
      return this;
    }
    this.checkFixedRate = checkFixedRate;
    return this;
  }
}
