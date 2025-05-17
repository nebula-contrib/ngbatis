package ye.weicheng.ngbatis.demo.pojo;

// Copyright (c) 2025 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import javax.persistence.Id;
import javax.persistence.Table;
import org.nebula.contrib.ngbatis.annotations.Space;
import org.springframework.data.geo.Box;
import org.springframework.data.geo.Point;
import org.springframework.data.geo.Polygon;

/**
 * @author yeweicheng
 * @since 2025-05-17 11:31
 * <br>Now is history!
 */
@Table(name = "test_geo")
@Space(name = "test")
public class TestGeo {

  @Id
  private String id;

  private Point geoPoint;

  private Box geoLine;

  private Polygon geoPolygon;

  private Object geo;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Point getGeoPoint() {
    return geoPoint;
  }

  public void setGeoPoint(Point geoPoint) {
    this.geoPoint = geoPoint;
  }

  public Box getGeoLine() {
    return geoLine;
  }

  public void setGeoLine(Box geoLine) {
    this.geoLine = geoLine;
  }

  public Polygon getGeoPolygon() {
    return geoPolygon;
  }

  public void setGeoPolygon(Polygon geoPolygon) {
    this.geoPolygon = geoPolygon;
  }

  public Object getGeo() {
    return geo;
  }

  public void setGeo(Object geo) {
    this.geo = geo;
  }
}
