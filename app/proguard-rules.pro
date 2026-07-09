# XAdapter Demo — app 模块混淆规则
# SDK 侧规则由 :smart / :node 的 consumer-rules.pro 自动合并，详见 docs/zh-CN/混淆与R8.md

# ---------- 调试堆栈 ----------
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# ---------- 本模块 ViewBinding / DataBinding ----------
-keep class pw.xiaohaozi.xadapter.databinding.** { *; }
-keep class * extends androidx.databinding.ViewDataBinding { *; }
-keep class pw.xiaohaozi.xadapter.DataBindingAdapter { *; }
-keep class pw.xiaohaozi.xadapter.info.HomeInfo { *; }

# ---------- VBFragment 泛型反射 ----------
-keep class * extends pw.xiaohaozi.xadapter.fragment.VBFragment { *; }

# ---------- EmptyActivity 通过 Class.forName 加载 Fragment ----------
-keepnames class * extends androidx.fragment.app.Fragment
-keepclassmembers class * extends androidx.fragment.app.Fragment {
    public <init>();
}

# ---------- Gson（node 示例 JSON、GsonUtil） ----------
-keepattributes Signature
-keepattributes *Annotation*

-keep class com.google.gson.reflect.TypeToken { *; }
-keep class * extends com.google.gson.reflect.TypeToken
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

-keepclassmembers,allowobfuscation class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# assets/省市县.json 等按字段名反序列化，保留模型字段名
-keep class pw.xiaohaozi.xadapter.info.** { <fields>; }
-keep class pw.xiaohaozi.xadapter.fragment.node.** { <fields>; }

# ---------- Glide ----------
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep class * extends com.bumptech.glide.module.AppGlideModule { <init>(...); }
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
    **[] $VALUES;
    public *;
}
-keep class com.bumptech.glide.load.data.ParcelFileDescriptorRewinder$InternalRewinder {
    *** rewind();
}

# ---------- 第三方库（抑制告警 / 保留必要 API） ----------
-dontwarn com.google.android.flexbox.**
-dontwarn com.lxj.xpopup.**
-dontwarn com.hjq.permissions.**

-keep class com.lxj.xpopup.** { *; }
-keep class com.hjq.permissions.** { *; }

# ---------- Media3 / ExoPlayer ----------
-dontwarn androidx.media3.**

# ---------- Kotlin ----------
-dontwarn kotlin.reflect.jvm.internal.**
