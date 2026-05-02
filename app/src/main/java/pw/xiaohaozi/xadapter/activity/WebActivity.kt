package pw.xiaohaozi.xadapter.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.webkit.WebSettings
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import pw.xiaohaozi.xadapter.databinding.ActivityWebBinding
import pw.xiaohaozi.xadapter.enableEdgeToEdge
import pw.xiaohaozi.xadapter.loadMarkDownByAsses

class WebActivity : AppCompatActivity() {
    val TAG = "WebActivity"
    val binding by lazy { ActivityWebBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.flBar) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.llGoBack.setOnClickListener { onBackPressed() }

        //binding.webView.loadUrl("file:///android_asset/${intent.getStringExtra("fileName")}.html")
        binding.webView.loadMarkDownByAsses(this, "${intent.getStringExtra("fileName")}.md")
    }

}