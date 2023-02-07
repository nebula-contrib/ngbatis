package org.nebula.contrib.ngbatis.config;

// Copyright (c) 2022 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

/**
 * yml 配置文件对应ngbatis属性的模型类
 *
 * @author gin soul
 * @since 2023-02-06 18:00
 */
public class NebulaNgbatisConfig {

    /**
     * session存活有效期
     */
    public Long sessionLifeLength;

    /**
     * session健康检测间隔
     */
    public Long checkFixedRate;
    /**
     * Whether to use the session pool of nebula-java
     */
    public Boolean useSessionPool;


    public Long getSessionLifeLength() {
        return sessionLifeLength;
    }

    /**
     * session存活有效期
     * @param sessionLifeLength 单位毫秒
     * @return null 或者 正数
     */
    public NebulaNgbatisConfig setSessionLifeLength(Long sessionLifeLength) {
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
    public NebulaNgbatisConfig setCheckFixedRate(Long checkFixedRate) {
        if (checkFixedRate == null || checkFixedRate <= 0) {
            return this;
        }
        this.checkFixedRate = checkFixedRate;
        return this;
    }

    public Boolean getUseSessionPool() {
        return useSessionPool;
    }

    /**
     * 默认 false
     * @param useSessionPool 是否使用 nebula-java 的 SessionPool
     * @return true/false
     */
    public NebulaNgbatisConfig setUseSessionPool(Boolean useSessionPool) {
        if (useSessionPool == null) {
            this.useSessionPool = false;
            return this;
        }
        this.useSessionPool = useSessionPool;
        return this;

    }
}
