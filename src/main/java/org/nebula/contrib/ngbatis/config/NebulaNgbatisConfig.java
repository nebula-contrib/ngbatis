package org.nebula.contrib.ngbatis.config;

public class NebulaNgbatisConfig {

    /**
     * session存活有效期
     */
    public Long sessionLifeLength;

    /**
     * session健康检测间隔
     */
    public Long checkFixedRate;


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
}
