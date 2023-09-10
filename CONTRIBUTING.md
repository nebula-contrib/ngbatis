# How To Contribute

## Markdown Lint

We could lint markdown files before commiting.

```bash
npm ci
npm run lint-md
```

In case of any linting errors, please fix them before commiting. The buildin formatter may help in most cases.

```bash
npm run format-md
```

## Version base

You should be having:

- JDK >= 8
- NebulaGraph > v3.0
- Springboot 2.x
- Maven

## Clone the repository

1. Click the `fork` button on the top right of this page.
2. Download the project to your machine.

## Start develop

1. Add developer in pom.xml if you are the first time to contributing.
2. Use your ideas to make ngbatis better.
3. Describe changes in `CHANGELOG.md`

## Make sure everything goes well

1. Run unit tests in `ngbatis-demo/test`
2. Use maven plugins to check code style: `maven->plugins->checkstyle->checkstyle:check`

## Commit and pull request

## Experience the joy brought by sharing

> If you have any questions, issue please.
