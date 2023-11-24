package ye.weicheng.ngbatis.demo.pojo;

// Copyright (c) 2022 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.Duration;
import java.util.Date;

/**
 * @author yeweicheng
 * @since 2023-06-07 15:26
 * <br>Now is history!
 */
@Table(name = "time_test")
public class TimeTest {
  @Id
  private String id;
  
  @Column(name = "t_date")
  private java.sql.Date date;
  
  @Column(name = "t_datetime")
  private Date datetime;
  
  @Column(name = "t_time")
  private java.sql.Time time;
  
  @Column(name = "t_timestamp")
  private java.sql.Timestamp timestamp;

  @Column(name = "t_duration")
  private Duration duration;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public java.sql.Date getDate() {
    return date;
  }

  public void setDate(java.sql.Date date) {
    this.date = date;
  }

  public Date getDatetime() {
    return datetime;
  }

  public void setDatetime(Date datetime) {
    this.datetime = datetime;
  }

  public Time getTime() {
    return time;
  }

  public void setTime(Time time) {
    this.time = time;
  }

  public Timestamp getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(Timestamp timestamp) {
    this.timestamp = timestamp;
  }

  public Duration getDuration() {
    return duration;
  }

  public void setDuration(Duration duration) {
    this.duration = duration;
  }
}
