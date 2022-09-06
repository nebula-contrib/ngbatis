package ye.weicheng.ngbatis.demo.pojo;

// Copyright (c) 2022 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

/**
 * <p>Person与Like关系的复合类型。</p>
 * @author yeweicheng
 * @since 2022-06-28 20:34
 * <br>Now is history!
 */
public class PersonLikePerson {

  private Person person1;
  private Like like;
  private Person person2;

  public Person getPerson1() {
    return person1;
  }

  public void setPerson1(Person person1) {
    this.person1 = person1;
  }

  public Like getLike() {
    return like;
  }

  public void setLike(Like like) {
    this.like = like;
  }

  public Person getPerson2() {
    return person2;
  }

  public void setPerson2(Person person2) {
    this.person2 = person2;
  }
}
