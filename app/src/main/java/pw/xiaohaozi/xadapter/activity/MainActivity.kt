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
import pw.xiaohaozi.xadapter.fragment.smart.CheckFragment
import pw.xiaohaozi.xadapter.fragment.smart.ClickFragment
import pw.xiaohaozi.xadapter.fragment.smart.ConcatAdapterFragment
import pw.xiaohaozi.xadapter.fragment.smart.CoroutineScopeFragment
import pw.xiaohaozi.xadapter.fragment.smart.DataDifferFragment
import pw.xiaohaozi.xadapter.fragment.smart.DataOperationFragment
import pw.xiaohaozi.xadapter.fragment.smart.DragSortFragment
import pw.xiaohaozi.xadapter.fragment.smart.GroupFragment
import pw.xiaohaozi.xadapter.fragment.smart.SelectFragment
import pw.xiaohaozi.xadapter.fragment.smart.LongClickFragment
import pw.xiaohaozi.xadapter.fragment.smart.MultipleFragment
import pw.xiaohaozi.xadapter.fragment.node.Node2EditFragment
import pw.xiaohaozi.xadapter.fragment.node.NodeEditFragment
import pw.xiaohaozi.xadapter.fragment.node.NodeFragment
import pw.xiaohaozi.xadapter.fragment.smart.SingleFragment
import pw.xiaohaozi.xadapter.fragment.smart.SpecialLayoutFragment
import pw.xiaohaozi.xadapter.fragment.smart.SwipeDeleteFragment
import pw.xiaohaozi.xadapter.fragment.smart.SwipeMenuFragment
import pw.xiaohaozi.xadapter.fragment.smart.TextChangeFragment
import pw.xiaohaozi.xadapter.info.HomeInfo
import pw.xiaohaozi.xadapter.info.VerseInfo
import pw.xiaohaozi.xadapter.smart.ext.createAdapter

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    private val adapter = createAdapter()
        .addHeader<ItemHomeHeaderBinding> { holder, data -> }
        .withType<ItemHomeTitleBinding, String>(isFixed = true) {
            it.binding.tvTitle.text = it.data
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
        adapter.refresh(list)

    }

    private val list = arrayListOf(
        "NodeAdapter",
        HomeInfo("node测试", "", R.mipmap.ic_launcher, NodeFragment::class.java),
        HomeInfo("node编辑", "", R.mipmap.ic_launcher, NodeEditFragment::class.java),
        HomeInfo("node编辑2", "", R.mipmap.ic_launcher, Node2EditFragment::class.java),

        "Adapter创建",
        HomeInfo("创建单布局", "单布局创建方式", R.mipmap.ic_launcher, SingleFragment::class.java),
        HomeInfo("创建多布局", "多布局创建方式", R.mipmap.ic_launcher, MultipleFragment::class.java),

        "选择操作",
        HomeInfo("选择操作", "Item选择操作", R.mipmap.ic_launcher, SelectFragment::class.java),

        "Item事件监听",
        HomeInfo("点击事件", "item点击事件", R.mipmap.ic_launcher, ClickFragment::class.java),
        HomeInfo("长按事件", "item长按事件", R.mipmap.ic_launcher, LongClickFragment::class.java),
        HomeInfo("选中事件", "单选、多选等", R.mipmap.ic_launcher, CheckFragment::class.java),
        HomeInfo("文本变化", "EditText文本变化监听", R.mipmap.ic_launcher, TextChangeFragment::class.java),


        "特殊布局",
        HomeInfo("特殊布局", "如头布局，脚布局，空布局、缺省页", R.mipmap.ic_launcher, SpecialLayoutFragment::class.java),
        HomeInfo("分组布局", "允许Item撑满整行", R.mipmap.ic_launcher, GroupFragment::class.java),
        "拖拽与侧滑",
        HomeInfo("侧滑删除", "侧滑删除Item", R.mipmap.ic_launcher, SwipeDeleteFragment::class.java),
        HomeInfo("拖拽排序", "长按拖拽排序", R.mipmap.ic_launcher, DragSortFragment::class.java),
        HomeInfo("侧滑菜单", "类似QQ侧滑效果", R.mipmap.ic_launcher, SwipeMenuFragment::class.java),
        "数据操作",
        HomeInfo("常规操作", "数据增删改查", R.mipmap.ic_launcher, DataOperationFragment::class.java),
        HomeInfo("Differ", "使用Differ更新数据", R.mipmap.ic_launcher, DataDifferFragment::class.java),
        "其他",
        HomeInfo("协程测试", "", R.mipmap.ic_launcher, CoroutineScopeFragment::class.java),
        HomeInfo("ConcatAdapter", "结合ConcatAdapter使用", R.mipmap.ic_launcher, ConcatAdapterFragment::class.java),

        "NodeAdapter",
        HomeInfo("单类型Node创建", "", R.mipmap.ic_launcher),
        HomeInfo("多类型Node创建", "", R.mipmap.ic_launcher),

        HomeInfo("Node展开与折叠", "", R.mipmap.ic_launcher),

        HomeInfo("添加根Node", "", R.mipmap.ic_launcher),
        HomeInfo("添加同级Node", "", R.mipmap.ic_launcher),
        HomeInfo("添加子级Node", "", R.mipmap.ic_launcher),

        HomeInfo("修改同级Node", "", R.mipmap.ic_launcher),
        HomeInfo("修改子级Node", "", R.mipmap.ic_launcher),
        HomeInfo("替换Node", "", R.mipmap.ic_launcher),

        HomeInfo("删除同级Node", "", R.mipmap.ic_launcher),
        HomeInfo("删除子级Node", "", R.mipmap.ic_launcher),


        )

}
