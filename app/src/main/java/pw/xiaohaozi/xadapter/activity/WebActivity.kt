package pw.xiaohaozi.xadapter.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import pw.xiaohaozi.xadapter.databinding.ActivityWebBinding

class WebActivity : AppCompatActivity() {
    val binding by lazy { ActivityWebBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.llGoBack.setOnClickListener { onBackPressed() }
        binding.webView.loadUrl("file:///android_asset/${intent.getStringExtra("fileName")}.html")
    }
}