# XAdapter smart — merged into consuming app when minifyEnabled is true.
# See docs/zh-CN/混淆与R8.md

-keepattributes Signature, InnerClasses, EnclosingMethod

-keep class pw.xiaohaozi.xadapter.smart.** { *; }

-keep,allowobfuscation class * extends pw.xiaohaozi.xadapter.smart.provider.XProvider { *; }
-keep,allowobfuscation class * extends pw.xiaohaozi.xadapter.smart.provider.SmartProvider { *; }
-keep,allowobfuscation class * extends pw.xiaohaozi.xadapter.smart.adapter.SmartAdapter { *; }
-keep,allowobfuscation class * extends pw.xiaohaozi.xadapter.smart.adapter.XAdapter { *; }

-keepclassmembers class * implements androidx.viewbinding.ViewBinding {
    public static ** inflate(android.view.LayoutInflater, android.view.ViewGroup, boolean);
    public static ** inflate(android.view.LayoutInflater);
    public static ** bind(android.view.View);
}
