package org.nebula.contrib.ngbatis.session;

// Copyright (c) 2022 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.
import com.vesoft.nebula.client.graph.net.Session;

/**.
 * 在本地定义的一个 Session，并通过定期检测，保证 Session 的存活稳定态.
 *.
 * @author yeweicheng.
 * @since 2022-08-26 1:54 <br>.
 *     Now is history.
.*/
public class LocalSession {

  private long birth;
  private Session session;
  private String currentSpace;
  int useCount = 0;

  public LocalSession(final long birth, final Session session) {
    this.birth = birth;
    this.session = session;
  }

  public long getBirth() {
    return birth;
  }

  public void setBirth(final long birth) {
    this.birth = birth;
  }

  public Session getSession() {
    return session;
  }

  public void setSession(final Session session) {
    this.session = session;
  }

  public String getCurrentSpace() {
    return currentSpace;
  }

  public void setCurrentSpace(final String currentSpace) {
    this.currentSpace = currentSpace;
  }
}
