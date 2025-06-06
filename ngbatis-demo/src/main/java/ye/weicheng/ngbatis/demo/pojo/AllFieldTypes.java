package ye.weicheng.ngbatis.demo.pojo;

// Copyright (c) 2025 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.Duration;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * @author yeweicheng
 * @since 2025-05-07 2:49
 * <br>Now is history!
 */
@Table(name = "all_field_types")
public class AllFieldTypes {
  
  @Id
  private String id;
  @Column(name = "aLong")
  private Long longData;
  @Column(name = "aBoolean")
  private Boolean booleanData;
  @Column(name = "aString")
  private String stringData;
  @Column(name = "aDouble")
  private Double doubleData;
  @Column(name = "anInt")
  private Integer intData;
  @Column(name = "aShort")
  private Short shortData;
  @Column(name = "aByte")
  private Byte byteData;
  @Column(name = "aFloat")
  private Float floatData;
  @Column(name = "aDate")
  private java.sql.Date dateData;
  @Column(name = "aTime")
  private java.sql.Time timeData;
  @Column(name = "aDateTime")
  @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private java.util.Date dateTimeData;
  @Column(name = "aTimestamp")
  private java.sql.Timestamp timestampData;
  @Column(name = "aDuration")
  private Duration durationData;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    AllFieldTypes that = (AllFieldTypes) o;

    if (!Objects.equals(id, that.id)) {
      return false;
    }
    if (!Objects.equals(longData, that.longData)) {
      return false;
    }
    if (!Objects.equals(booleanData, that.booleanData)) {
      return false;
    }
    if (!Objects.equals(stringData, that.stringData)) {
      return false;
    }
    if (!Objects.equals(doubleData, that.doubleData)) {
      return false;
    }
    if (!Objects.equals(intData, that.intData)) {
      return false;
    }
    if (!Objects.equals(shortData, that.shortData)) {
      return false;
    }
    if (!Objects.equals(byteData, that.byteData)) {
      return false;
    }
    if (!Objects.equals(floatData, that.floatData)) {
      return false;
    }
    if (!Objects.equals(dateData, that.dateData)) {
      return false;
    }
    if (!Objects.equals(timeData, that.timeData)) {
      return false;
    }
    if (!Objects.equals(dateTimeData, that.dateTimeData)) {
      return false;
    }
    if (!Objects.equals(timestampData, that.timestampData)) {
      return false;
    }
    return Objects.equals(durationData, that.durationData);
  }

  @Override
  public int hashCode() {
    int result = id != null ? id.hashCode() : 0;
    result = 31 * result + (longData != null ? longData.hashCode() : 0);
    result = 31 * result + (booleanData != null ? booleanData.hashCode() : 0);
    result = 31 * result + (stringData != null ? stringData.hashCode() : 0);
    result = 31 * result + (doubleData != null ? doubleData.hashCode() : 0);
    result = 31 * result + (intData != null ? intData.hashCode() : 0);
    result = 31 * result + (shortData != null ? shortData.hashCode() : 0);
    result = 31 * result + (byteData != null ? byteData.hashCode() : 0);
    result = 31 * result + (floatData != null ? floatData.hashCode() : 0);
    result = 31 * result + (dateData != null ? dateData.hashCode() : 0);
    result = 31 * result + (timeData != null ? timeData.hashCode() : 0);
    result = 31 * result + (dateTimeData != null ? dateTimeData.hashCode() : 0);
    result = 31 * result + (timestampData != null ? timestampData.hashCode() : 0);
    result = 31 * result + (durationData != null ? durationData.hashCode() : 0);
    return result;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Long getLongData() {
    return longData;
  }

  public void setLongData(Long longData) {
    this.longData = longData;
  }

  public Boolean getBooleanData() {
    return booleanData;
  }

  public void setBooleanData(Boolean booleanData) {
    this.booleanData = booleanData;
  }

  public String getStringData() {
    return stringData;
  }

  public void setStringData(String stringData) {
    this.stringData = stringData;
  }

  public Double getDoubleData() {
    return doubleData;
  }

  public void setDoubleData(Double doubleData) {
    this.doubleData = doubleData;
  }

  public Integer getIntData() {
    return intData;
  }

  public void setIntData(Integer intData) {
    this.intData = intData;
  }

  public Short getShortData() {
    return shortData;
  }

  public void setShortData(Short shortData) {
    this.shortData = shortData;
  }

  public Byte getByteData() {
    return byteData;
  }

  public void setByteData(Byte byteData) {
    this.byteData = byteData;
  }

  public Float getFloatData() {
    return floatData;
  }

  public void setFloatData(Float floatData) {
    this.floatData = floatData;
  }

  public Date getDateData() {
    return dateData;
  }

  public void setDateData(Date dateData) {
    this.dateData = dateData;
  }

  public Time getTimeData() {
    return timeData;
  }

  public void setTimeData(Time timeData) {
    this.timeData = timeData;
  }

  public java.util.Date getDateTimeData() {
    return dateTimeData;
  }

  public void setDateTimeData(java.util.Date dateTimeData) {
    this.dateTimeData = dateTimeData;
  }

  public Timestamp getTimestampData() {
    return timestampData;
  }

  public void setTimestampData(Timestamp timestampData) {
    this.timestampData = timestampData;
  }

  public Duration getDurationData() {
    return durationData;
  }

  public void setDurationData(Duration durationData) {
    this.durationData = durationData;
  }
}
