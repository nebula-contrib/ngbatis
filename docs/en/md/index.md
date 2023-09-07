---
# https://vitepress.dev/reference/default-theme-home-page
layout: home

hero:
  name: "NgBatis Docs"
  tagline: NGBATIS is a database ORM framework base NebulaGraph + spring-boot, which takes advantage of the mybatis’ fashion development, including some de-factor operations in single table and vertex-edge, like mybatis-plus.
  actions:
    - theme: brand
      text: Quick Start
      link: /quick-start/about
    - theme: alt
      text: GitHub Repo
      link: https://github.com/nebula-contrib/ngbatis

features:
  - title: Templating
    details: nGQL in XML is essentially a string template, which can achieve dynamic queries through placeholder substitution. NgBatis uses Beetl as the template engine.
  - title: ORM
    details: Supports annotations in `javax.persistence.*` to implement object-relational mapping.
  - title: Built-in Basic CRUD
    details: By inheriting the base class `NebulaDaoBasic<T, ID>`, basic CRUD operations are implemented.

---
