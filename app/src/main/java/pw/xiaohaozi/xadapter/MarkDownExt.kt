package pw.xiaohaozi.xadapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView
import okio.FileNotFoundException

fun WebView.loadMarkDown(markdown: String) {
    val html = getRenderHtml(markdown)
    loadDataWithBaseURL("file:///android_asset/", html, "text/html", "UTF-8", null)
}

@SuppressLint("SetJavaScriptEnabled")
fun WebView.loadMarkDownByAsses(context: Context, fileName: String) {
    try {
        setBackgroundColor(Color.TRANSPARENT)
        backgroundTintList = ColorStateList.valueOf(Color.TRANSPARENT)
        setLayerType(View.LAYER_TYPE_SOFTWARE, null)

        // 配置 WebView（必须开 JS）
        val webSettings: WebSettings = settings
        webSettings.javaScriptEnabled = true
        webSettings.domStorageEnabled = true
        webSettings.allowFileAccess = true

        val markdown = context.assets.open(fileName).readBytes().toString(Charsets.UTF_8)
        loadMarkDown(markdown)
    } catch (e: FileNotFoundException) {
        val md = "![敬请期待](ic_stay_tuned.png)"
        loadMarkDown(md)
    } catch (e: Exception) {
        val md = "##### 加载失败\n```kotlin\n$e\n```".trimIndent()
//        val md = "## Hello Markdown\n**加粗文本**\n- 列表1\n- 列表2\n[链接](https://www.baidu.com)\n```\n private lateinit var etMarkdown: EditText\n```"
        loadMarkDown(md)
    }
}

private fun getRenderHtml(markdown: String): String {
    val safe = markdown
        .replace("\\", "\\\\")
        .replace("`", "\\`")
        .replace("\"", "\\\"")
        .replace("\r\n", "\\n")
        .replace("\n", "\\n")

    return """
            <html>
                <head>
                    <meta charset="utf-8">
                    <link rel="stylesheet" href="github-markdown.min.css">
                
                    <!-- 👇 这里换成豆包同款清爽高亮样式 -->
                    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.7.0/styles/github.min.css">
                    <!--深色豆包风格-->
                    <!--<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.7.0/styles/atom-one-dark.min.css">-->
                    <style>
                        /* 全局背景透明 */
                        body, .markdown-body, #content {
                            background-color: transparent !important;
                        }
                        /* 图片强制透明、无边框、无背景 */
                        img {
                            background: transparent !important;
                            border: none !important;
                            border-radius: 8px;
                            box-shadow: none !important;
                            max-width: 100%;
                        }
                        body { padding:12px; font-size:16px;}
                        /* 👇 豆包同款代码块样式（柔和灰底、圆角、清爽） */
                        pre {
                            background-color: #f6f8fa;
                            padding: 4px 14px  !important;
                            border-radius: 8px;
                            overflow-x: auto;
                        }
                        code {
                            font-size: 14px;
                            color: #24292e;
                        }
                    </style>
                </head>
                <body class="markdown-body">
                    <div id="content"></div>
                
                    <script src="marked.min.js"></script>
                    <script src="https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.7.0/highlight.min.js"></script>
                
                    <script>
                        marked.setOptions({
                            highlight: function(code) {
                                return hljs.highlightAuto(code).value;
                            }
                        });
                        document.getElementById('content').innerHTML = marked.parse("$safe");
                        hljs.highlightAll();
                    </script>
                </body>
            </html>
        """.trimIndent()
}


