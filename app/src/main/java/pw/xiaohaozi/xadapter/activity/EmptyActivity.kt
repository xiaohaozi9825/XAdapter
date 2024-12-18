package pw.xiaohaozi.xadapter.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import pw.xiaohaozi.xadapter.R
import pw.xiaohaozi.xadapter.databinding.ActivityEmptyBinding
import pw.xiaohaozi.xadapter.enableEdgeToEdge


class EmptyActivity : AppCompatActivity() {
    private val binding by lazy { ActivityEmptyBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.flBar) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.title.text = intent.getStringExtra("title") ?: "SmartAdapter"
        binding.llGoBack.setOnClickListener { onBackPressed() }
        binding.btnImage.setOnClickListener {
            val intent = Intent(this, WebActivity::class.java)
                .putExtra("fileName", intent.getStringExtra("name"))
            startActivity(intent)
        }
        supportFragmentManager.beginTransaction().replace(
            R.id.fl_fragment,
            Class.forName(intent.getStringExtra("fragmentClassName")) as Class<out Fragment>, null
        ).commit()
    }
}

inline fun <reified F : Fragment> Activity.toEmptyActivity(title: String) {
    startActivity(
        Intent(this, EmptyActivity::class.java)
            .putExtra("fragmentClassName", F::class.java.name)
            .putExtra("title", title)
    )
}

fun Activity.toEmptyActivity(clazz: Class<out Fragment>, title: String, name: String) {
    startActivity(
        Intent(this, EmptyActivity::class.java)
            .putExtra("fragmentClassName", clazz.name)
            .putExtra("title", title)
            .putExtra("name", name)
    )
}