# XAdapter node — merged into consuming app when minifyEnabled is true.
# smart consumer-rules.pro is applied transitively via :node -> :smart.
# See docs/zh-CN/混淆与R8.md

-keep class pw.xiaohaozi.xadapter.node.** { *; }

-keep,allowobfuscation class * extends pw.xiaohaozi.xadapter.node.NodeProvider { *; }
-keep,allowobfuscation class * extends pw.xiaohaozi.xadapter.node.NodeAdapter { *; }
