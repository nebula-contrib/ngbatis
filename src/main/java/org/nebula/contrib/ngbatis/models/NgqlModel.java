package org.nebula.contrib.ngbatis.models;

/**
 * TODO
 * 2023-9-6 15:45 lyw.
 */
public class NgqlModel {

  private String id;
  private String text;

  public NgqlModel(String id, String text) {
    this.id = id;
    this.text = text;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

}
