# XAdapter 中文文档

本目录面向 **仅通过 Maven/JitPack 添加依赖** 的集成方：`smart`、`node` 为 SDK，`app` 为演示工程（不参与依赖）。

| 文档 | 说明 |
|------|------|
| [接入指南](./接入指南.md) | JitPack 仓库、Gradle 依赖、ViewBinding、模块选择 |
| [环境与依赖](./环境与依赖.md) | 推荐 AGP/Kotlin/JDK、SDK 自带传递依赖、RecyclerView 等 |
| [混淆与 R8](./混淆与R8.md) | consumer 规则、集成方需补充的 ViewBinding 配置、常见崩溃排查 |
| [完整教程](./完整教程.md) | 从 RecyclerView 绑定到多布局、事件、选择、Differ、侧滑、node 树形等 |
| [API 参考 · smart](./API参考-smart.md) | 工厂函数、`SmartDataProxy` / `EventProxy` / `SelectedProxy`、`XAdapter`、`OnBindParams` 等 |
| [API 参考 · node](./API参考-node.md) | `NodeEntity`、`NodeAdapter` / `NodeProvider`、工厂与扩展 |

集成遇到问题可先对照 **接入指南** 的「常见问题」，仍无法解决再到 [Issues](https://github.com/xiaohaozi9825/XAdapter/issues) 反馈。
