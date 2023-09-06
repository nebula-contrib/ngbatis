import { defineConfig, withBase } from "vitepress";
import { withMermaid } from "vitepress-plugin-mermaid";

// https://vitepress.dev/reference/site-config
export default withMermaid({
  base: "/ngbatis/",
  title: "NgBatis",
  description: "Access NebulaGraph the MyBatis way",
  themeConfig: {
    search: {
      provider: "local",
    },
    // https://vitepress.dev/reference/default-theme-config
    nav: [
      { text: "Home", link: "/index" },
      { text: "API", link: "/quick-start/about" },
    ],
    socialLinks: [
      { icon: "github", link: "https://github.com/nebula-contrib/ngbatis" },
    ],

    sidebar: [
      {
        text: "Quick Start",
        items: [
          { text: "About NgBatis", link: "/quick-start/about" },
          { text: "Features", link: "/quick-start/features" },
          { text: "Installation", link: "/quick-start/install" },
        ],
      },
      {
        text: "Examples",
        items: [
          { text: "Preparation", link: "/dev-example/prepare" },
          { text: "Using Base Class for Read and Write", link: "/dev-example/dao-basic" },
          { text: "Base Class Implementation for Multiple Tags", link: "/dev-example/multi-tag" },
          { text: "Custom nGQL", link: "/dev-example/custom-crud" },
          { text: "How to Pass Parameters", link: "/dev-example/parameter-use" },
          { text: "Different Return Value Types", link: "/dev-example/result" },
          { text: "Built-in Return Value Types", link: "/dev-example/result-built-in" },
          { text: "Parameter Condition Control", link: "/dev-example/parameter-if" },
          { text: "Parameter Loop", link: "/dev-example/parameter-for" },
          {
            text: "Built-in Functions and Variables",
            link: "/dev-example/built-in-function",
          },
        ],
      },
      {
        text: "Learn More",
        items: [
          { text: "Runtime Sequence", link: "/step-forward-docs/operation-sequence" },
          {
            text: "Advanced Configuration",
            link: "/step-forward-docs/advanced-configuration",
          },
        ],
      },
    ],

    langMenuLabel: "Languages",
  },

  locales: {
    "https://graph-cn.github.io/ngbatis-docs/": {
      lang: "zh-CN",
      label: "简体中文",
      title: "NgBatis",
      description: "一个 NebulaGraph 的 ORM 框架",
    },
    "/": {
      lang: "en-US",
      label: "English",
      title: "NgBatis",
      description: "An ORM framework for NebulaGraph",
    },
  },
});