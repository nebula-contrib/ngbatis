package ye.weicheng.ngbatis.demo.pojo;

// Copyright (c) 2025 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.Duration;
import java.util.Objects;
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
  private Long aLong;
  private Boolean aBoolean;
  private String aString;
  private Double aDouble;
  private Integer anInt;
  private Short aShort;
  private Byte aByte;
  private Float aFloat;
  private java.sql.Date aDate;
  private java.sql.Time aTime;
  @DateTimeFormat( pattern = "yyyy-MM-dd'T'HH:mm:ss" )
  private java.util.Date aDateTime;
  private java.sql.Timestamp aTimestamp;
  private Duration aDuration;

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
    if (!Objects.equals(aLong, that.aLong)) {
      return false;
    }
    if (!Objects.equals(aBoolean, that.aBoolean)) {
      return false;
    }
    if (!Objects.equals(aString, that.aString)) {
      return false;
    }
    if (!Objects.equals(aDouble, that.aDouble)) {
      return false;
    }
    if (!Objects.equals(anInt, that.anInt)) {
      return false;
    }
    if (!Objects.equals(aShort, that.aShort)) {
      return false;
    }
    if (!Objects.equals(aByte, that.aByte)) {
      return false;
    }
    if (!Objects.equals(aFloat, that.aFloat)) {
      return false;
    }
    if (!Objects.equals(aDate, that.aDate)) {
      return false;
    }
    if (!Objects.equals(aTime, that.aTime)) {
      return false;
    }
    if (!Objects.equals(aDateTime, that.aDateTime)) {
      return false;
    }
    if (!Objects.equals(aTimestamp, that.aTimestamp)) {
      return false;
    }
    return Objects.equals(aDuration, that.aDuration);
  }

  @Override
  public int hashCode() {
    int result = id != null ? id.hashCode() : 0;
    result = 31 * result + (aLong != null ? aLong.hashCode() : 0);
    result = 31 * result + (aBoolean != null ? aBoolean.hashCode() : 0);
    result = 31 * result + (aString != null ? aString.hashCode() : 0);
    result = 31 * result + (aDouble != null ? aDouble.hashCode() : 0);
    result = 31 * result + (anInt != null ? anInt.hashCode() : 0);
    result = 31 * result + (aShort != null ? aShort.hashCode() : 0);
    result = 31 * result + (aByte != null ? aByte.hashCode() : 0);
    result = 31 * result + (aFloat != null ? aFloat.hashCode() : 0);
    result = 31 * result + (aDate != null ? aDate.hashCode() : 0);
    result = 31 * result + (aTime != null ? aTime.hashCode() : 0);
    result = 31 * result + (aDateTime != null ? aDateTime.hashCode() : 0);
    result = 31 * result + (aTimestamp != null ? aTimestamp.hashCode() : 0);
    result = 31 * result + (aDuration != null ? aDuration.hashCode() : 0);
    return result;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Long getaLong() {
    return aLong;
  }

  public void setaLong(Long aLong) {
    this.aLong = aLong;
  }

  public Boolean getaBoolean() {
    return aBoolean;
  }

  public void setaBoolean(Boolean aBoolean) {
    this.aBoolean = aBoolean;
  }

  public String getaString() {
    return aString;
  }

  public void setaString(String aString) {
    this.aString = aString;
  }

  public Double getaDouble() {
    return aDouble;
  }

  public void setaDouble(Double aDouble) {
    this.aDouble = aDouble;
  }

  public Integer getAnInt() {
    return anInt;
  }

  public void setAnInt(Integer anInt) {
    this.anInt = anInt;
  }

  public Short getaShort() {
    return aShort;
  }

  public void setaShort(Short aShort) {
    this.aShort = aShort;
  }

  public Byte getaByte() {
    return aByte;
  }

  public void setaByte(Byte aByte) {
    this.aByte = aByte;
  }

  public Float getaFloat() {
    return aFloat;
  }

  public void setaFloat(Float aFloat) {
    this.aFloat = aFloat;
  }

  public Date getaDate() {
    return aDate;
  }

  public void setaDate(Date aDate) {
    this.aDate = aDate;
  }

  public Time getaTime() {
    return aTime;
  }

  public void setaTime(Time aTime) {
    this.aTime = aTime;
  }

  public java.util.Date getaDateTime() {
    return aDateTime;
  }

  public void setaDateTime(java.util.Date aDateTime) {
    this.aDateTime = aDateTime;
  }

  public Timestamp getaTimestamp() {
    return aTimestamp;
  }

  public void setaTimestamp(Timestamp aTimestamp) {
    this.aTimestamp = aTimestamp;
  }

  public Duration getaDuration() {
    return aDuration;
  }

  public void setaDuration(Duration aDuration) {
    this.aDuration = aDuration;
  }
}
