package pw.xiaohaozi.xadapter.activity

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import pw.xiaohaozi.xadapter.R
import pw.xiaohaozi.xadapter.databinding.ActivityMainBinding
import pw.xiaohaozi.xadapter.databinding.ItemHomeBinding
import pw.xiaohaozi.xadapter.databinding.ItemHomeTitleBinding
import pw.xiaohaozi.xadapter.fragment.SingleFragment
import pw.xiaohaozi.xadapter.smart.ext.createAdapter
import pw.xiaohaozi.xadapter.info.HomeInfo
import pw.xiaohaozi.xadapter.smart.ext.toAdapter
import pw.xiaohaozi.xadapter.smart.ext.withType


class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    private val adapter = createAdapter()
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.rvList.adapter = adapter
        adapter.reset(list)
    }

    private val list = arrayListOf(
        "单布局",
        HomeInfo(
            "创建Adapter",
            "提供多种创建Adapter的方式",
            R.mipmap.ic_launcher,
            SingleFragment::class.java
        ),
        HomeInfo(
            "选择事件",
            "封装了item选择事件，可实现单选、多选、全选、全不选等",
            R.mipmap.ic_launcher
        ),
        HomeInfo(
            "事件监听",
            "封装了多种事件监听，如点击、长按、文本改变、选中状态、开关等",
            R.mipmap.ic_launcher
        ),
        "多布局",
        HomeInfo("创建Adapter", "快速创建Adapter", R.mipmap.ic_launcher),
        HomeInfo("点击事件", "快速创建Adapter", R.mipmap.ic_launcher),
        HomeInfo("选择事件", "快速创建Adapter", R.mipmap.ic_launcher),
        HomeInfo("事件监听", "快速创建Adapter", R.mipmap.ic_launcher),
    )

}
