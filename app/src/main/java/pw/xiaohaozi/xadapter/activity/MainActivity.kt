package pw.xiaohaozi.xadapter.activity

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import pw.xiaohaozi.xadapter.R
import pw.xiaohaozi.xadapter.databinding.ActivityMainBinding
import pw.xiaohaozi.xadapter.databinding.ItemHomeBinding
import pw.xiaohaozi.xadapter.databinding.ItemHomeTitleBinding
import pw.xiaohaozi.xadapter.databinding.ItemImageCardBinding
import pw.xiaohaozi.xadapter.enableEdgeToEdge
import pw.xiaohaozi.xadapter.fragment.EventFragment
import pw.xiaohaozi.xadapter.fragment.MultipleFragment
import pw.xiaohaozi.xadapter.fragment.SelectFragment
import pw.xiaohaozi.xadapter.fragment.SingleFragment
import pw.xiaohaozi.xadapter.fragment.SpecialLayoutFragment
import pw.xiaohaozi.xadapter.smart.ext.createAdapter
import pw.xiaohaozi.xadapter.info.HomeInfo
import pw.xiaohaozi.xadapter.info.VerseInfo
import pw.xiaohaozi.xadapter.smart.ext.toAdapter
import pw.xiaohaozi.xadapter.smart.ext.withType


class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    private val adapter = createAdapter()
        .withType<ItemImageCardBinding, Int>(isFixed = true) { holder, data, position ->
            holder.binding.image.setImageResource(data)
        }
        .withType<ItemHomeTitleBinding, String>(isFixed = true) { holder, data, position ->
            holder.binding.tvTitle.text = data
        }
        .withType<ItemHomeBinding, HomeInfo> { holder, data, position ->
            holder.binding.data = data
        }
        .setOnClickListener { holder, data, position, view ->
            val clazz = data?.clazz
            if (clazz == null)
                Toast.makeText(this@MainActivity, "敬请期待", Toast.LENGTH_SHORT).show()
            else
                toEmptyActivity(clazz, data.label, clazz.simpleName)
        }
        .toAdapter()
    var data: VerseInfo? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.rvList.adapter = adapter
        adapter.reset(list)
    }

    private val list = arrayListOf(
        R.mipmap.home_top,
        "Adapter创建",
        HomeInfo(
            "创建单布局Adapter",
            "提供多种创建Adapter的方式",
            R.mipmap.ic_launcher,
            SingleFragment::class.java
        ),
        HomeInfo(
            "创建多布局Adapter", "快速创建Adapter", R.mipmap.ic_launcher, MultipleFragment::class.java
        ),
        "Adapter使用",
        HomeInfo(
            "item选择", "快速创建Adapter", R.mipmap.ic_launcher,
            SelectFragment::class.java
        ),
        HomeInfo(
            "事件监听", "快速创建Adapter", R.mipmap.ic_launcher,
            EventFragment::class.java
        ),
        HomeInfo(
            "特殊布局", "头布局、脚布局、空布局、错误布局、分组布局", R.mipmap.ic_launcher,
            SpecialLayoutFragment::class.java
        ),
        HomeInfo(
            "数据操作", "数据增删改查", R.mipmap.ic_launcher,
            SpecialLayoutFragment::class.java
        ),
    )

}
