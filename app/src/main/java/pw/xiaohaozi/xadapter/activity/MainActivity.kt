package pw.xiaohaozi.xadapter.activity

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import pw.xiaohaozi.xadapter.R
import pw.xiaohaozi.xadapter.databinding.ActivityMainBinding
import pw.xiaohaozi.xadapter.databinding.ItemHomeBinding
import pw.xiaohaozi.xadapter.databinding.ItemHomeHeaderBinding
import pw.xiaohaozi.xadapter.databinding.ItemHomeTitleBinding
import pw.xiaohaozi.xadapter.enableEdgeToEdge
import pw.xiaohaozi.xadapter.fragment.CheckFragment
import pw.xiaohaozi.xadapter.fragment.ClickFragment
import pw.xiaohaozi.xadapter.fragment.DragSortFragment
import pw.xiaohaozi.xadapter.fragment.ImageSelectFragment
import pw.xiaohaozi.xadapter.fragment.LongClickFragment
import pw.xiaohaozi.xadapter.fragment.MultipleFragment
import pw.xiaohaozi.xadapter.fragment.SingleFragment
import pw.xiaohaozi.xadapter.fragment.SpecialLayoutFragment
import pw.xiaohaozi.xadapter.fragment.SwipeDeleteFragment
import pw.xiaohaozi.xadapter.fragment.SwipeMenuFragment
import pw.xiaohaozi.xadapter.fragment.TextChangeFragment
import pw.xiaohaozi.xadapter.info.HomeInfo
import pw.xiaohaozi.xadapter.info.VerseInfo
import pw.xiaohaozi.xadapter.smart.ext.createAdapter
import pw.xiaohaozi.xadapter.smart.ext.toAdapter
import pw.xiaohaozi.xadapter.smart.ext.withType


class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    private val adapter = createAdapter()
        .addHeader<ItemHomeHeaderBinding> { }
        .withType<ItemHomeTitleBinding, String>(isFixed = true) { (holder, data) ->
            holder.binding.tvTitle.text = data
        }
        .withType<ItemHomeBinding, HomeInfo> { (holder, data) ->
            holder.binding.data = data
        }
        .setOnClickListener { holder, data, position, view ->
            val clazz = data.clazz
            if (clazz == null) {
                Toast.makeText(this@MainActivity, "敬请期待", Toast.LENGTH_SHORT).show()
            } else
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
        "Adapter创建",
        HomeInfo("创建单布局", "单布局创建方式", R.mipmap.ic_launcher, SingleFragment::class.java),
        HomeInfo("创建多布局", "多布局创建方式", R.mipmap.ic_launcher, MultipleFragment::class.java),
        "item事件监听",
        HomeInfo("点击事件", "item点击事件", R.mipmap.ic_launcher, ClickFragment::class.java),
        HomeInfo("长按事件", "item长按事件", R.mipmap.ic_launcher, LongClickFragment::class.java),
        HomeInfo("选中事件", "RadioButton、CheckBox选中状态监听", R.mipmap.ic_launcher, CheckFragment::class.java),
        HomeInfo("文本变化", "EditText文本变化监听", R.mipmap.ic_launcher, TextChangeFragment::class.java),

        "选择操作",
        HomeInfo("选择", "item选择操作", R.mipmap.ic_launcher, ImageSelectFragment::class.java),
        "特殊布局",
        HomeInfo("特殊布局", "如头布局，脚布局，空布局、缺省页", R.mipmap.ic_launcher, SpecialLayoutFragment::class.java),
        "拖拽与侧滑",
        HomeInfo("侧滑删除", "", R.mipmap.ic_launcher, SwipeDeleteFragment::class.java),
        HomeInfo("拖拽排序", "", R.mipmap.ic_launcher, DragSortFragment::class.java),
        HomeInfo("侧滑菜单", "类似QQ侧滑效果", R.mipmap.ic_launcher, SwipeMenuFragment::class.java),
        "数据操作",
        HomeInfo("数据操作", "数据增删改查", R.mipmap.ic_launcher),
        HomeInfo("数据操作", "数据增删改查", R.mipmap.ic_launcher, SpecialLayoutFragment::class.java),
        HomeInfo("数据操作", "数据增删改查", R.mipmap.ic_launcher, SpecialLayoutFragment::class.java),
        HomeInfo("数据操作", "数据增删改查", R.mipmap.ic_launcher, SpecialLayoutFragment::class.java),
        HomeInfo("数据操作", "数据增删改查", R.mipmap.ic_launcher, SpecialLayoutFragment::class.java),
    )

}
