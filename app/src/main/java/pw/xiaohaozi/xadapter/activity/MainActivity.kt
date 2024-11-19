package pw.xiaohaozi.xadapter.activity

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import coil.load
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pw.xiaohaozi.xadapter.R
import pw.xiaohaozi.xadapter.databinding.ActivityMainBinding
import pw.xiaohaozi.xadapter.databinding.ItemHomeBinding
import pw.xiaohaozi.xadapter.databinding.ItemHomeEmptyBinding
import pw.xiaohaozi.xadapter.databinding.ItemHomeFooterBinding
import pw.xiaohaozi.xadapter.databinding.ItemHomeHeaderBinding
import pw.xiaohaozi.xadapter.databinding.ItemHomeTitleBinding
import pw.xiaohaozi.xadapter.databinding.ItemImageCardBinding
import pw.xiaohaozi.xadapter.enableEdgeToEdge
import pw.xiaohaozi.xadapter.fragment.CheckFragment
import pw.xiaohaozi.xadapter.fragment.ClickFragment
import pw.xiaohaozi.xadapter.fragment.EventFragment
import pw.xiaohaozi.xadapter.fragment.LongClickFragment
import pw.xiaohaozi.xadapter.fragment.MultipleFragment
import pw.xiaohaozi.xadapter.fragment.SelectFragment
import pw.xiaohaozi.xadapter.fragment.SingleFragment
import pw.xiaohaozi.xadapter.fragment.SpecialLayoutFragment
import pw.xiaohaozi.xadapter.fragment.TextChangeFragment
import pw.xiaohaozi.xadapter.info.HomeInfo
import pw.xiaohaozi.xadapter.info.VerseInfo
import pw.xiaohaozi.xadapter.smart.ext.createAdapter
import pw.xiaohaozi.xadapter.smart.ext.toAdapter
import pw.xiaohaozi.xadapter.smart.ext.withType


class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    private val adapter = createAdapter()
        .addHeader<ItemHomeHeaderBinding>("head-001") {

        }
        .addHeader<ItemImageCardBinding>("head-002") {
            it.binding.image.load(R.mipmap.snow3)
        }
//        .addFooter<ItemHomeFooterBinding>("foot-001")

        .withType<ItemHomeTitleBinding, String>(isFixed = true) { (holder, data) ->
            holder.binding.tvTitle.text = data
        }
        .withType<ItemHomeBinding, HomeInfo> { (holder, data) ->
            holder.binding.data = data
        }
        .setOnClickListener { holder, data, position, view ->
            val clazz = data?.clazz
            if (clazz == null) {
                Toast.makeText(this@MainActivity, "敬请期待", Toast.LENGTH_SHORT).show()
                adapter.setEmpty<ItemHomeEmptyBinding>()
                adapter.reset(mutableListOf())
                click()

            } else
                toEmptyActivity(clazz, data.label, clazz.simpleName)
        }
        .toAdapter()

    private fun click() {
        lifecycleScope.launch {
            delay(2000)
            adapter.deleteEmpty()
            this@MainActivity.adapter.reset(list)
        }
    }

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
        "Adapter创建",
        HomeInfo(
            "创建单布局Adapter", "提供多种创建Adapter的方式", R.mipmap.ic_launcher, SingleFragment::class.java
        ),
        HomeInfo(
            "创建多布局Adapter", "快速创建Adapter", R.mipmap.ic_launcher, MultipleFragment::class.java
        ),
        "item事件监听",
        HomeInfo(
            "点击事件", "View点击事件", R.mipmap.ic_launcher, ClickFragment::class.java
        ),
        HomeInfo(
            "长按事件", "View长按事件", R.mipmap.ic_launcher, LongClickFragment::class.java
        ),
        HomeInfo(
            "选中事件", "RadioButton、CheckBox选中状态监听", R.mipmap.ic_launcher, CheckFragment::class.java
        ),
        HomeInfo(
            "文本变化", "EditText文本变化监听", R.mipmap.ic_launcher, TextChangeFragment::class.java
        ),

        "选择操作",
        HomeInfo(
            "选择", "单选、多选", R.mipmap.ic_launcher,
            SelectFragment::class.java
        ),
        "数据操作",
        HomeInfo(
            "数据操作", "数据增删改查", R.mipmap.ic_launcher,

            ),
        HomeInfo(
            "数据操作", "数据增删改查", R.mipmap.ic_launcher,
            SpecialLayoutFragment::class.java
        ),
        HomeInfo(
            "数据操作", "数据增删改查", R.mipmap.ic_launcher,
            SpecialLayoutFragment::class.java
        ),
        HomeInfo(
            "数据操作", "数据增删改查", R.mipmap.ic_launcher,
            SpecialLayoutFragment::class.java
        ),
        HomeInfo(
            "数据操作", "数据增删改查", R.mipmap.ic_launcher,
            SpecialLayoutFragment::class.java
        ),
    )

}
