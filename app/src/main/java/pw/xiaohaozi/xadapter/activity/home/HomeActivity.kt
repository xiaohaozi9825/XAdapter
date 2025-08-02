package pw.xiaohaozi.xadapter.activity.home

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import pw.xiaohaozi.xadapter.R
import pw.xiaohaozi.xadapter.databinding.ActivityHomeBinding
import pw.xiaohaozi.xadapter.databinding.ItemHomeBottomButtonBinding
import pw.xiaohaozi.xadapter.enableEdgeToEdge
import pw.xiaohaozi.xadapter.smart.ext.createAdapter


class HomeActivity : AppCompatActivity() {
    val binding by lazy { ActivityHomeBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        initBottomNav()
    }

    private fun initBottomNav() {
        val adapter = createAdapter<ItemHomeBottomButtonBinding, NevButton> {
            it.holder.binding.apply {
                if (!it.payloads.contains("select")) {
                    tvTitle.text = it.data.title
                    ivIcon.setImageResource(it.data.icon)
                }
                if (isSelected(it.data)) {
                    tvTitle.setTextColor(Color.parseColor("#AA0000"))
                } else {
                    tvTitle.setTextColor(Color.parseColor("#AAAAAA"))
                }
            }
        }.setOnItemSelectListener(payload = "select") { data, position, index, fromUser ->
            if (isSelected(data)) {
                //切换页面
                val fragmentManager: FragmentManager = supportFragmentManager
                val transaction: FragmentTransaction = fragmentManager.beginTransaction()
                transaction.replace(R.id.fl_content, data.fragment, null)
//                 transaction.addToBackStack(null)
                transaction.commit()
            }
        }.setMaxSelectCount(1)

        binding.rvBottom.adapter = adapter
        adapter.refresh(bottomList)
        binding.rvBottom.post { adapter.setSelectAt(0, true) }
    }

    private val bottomList = mutableListOf(
        NevButton(R.mipmap.ic_launcher, "首页", HomeFragment::class.java),
        NevButton(R.mipmap.ic_launcher, "Smart", SmartMenuFragment::class.java),
        NevButton(R.mipmap.ic_launcher, "Node", NodeMenuFragment::class.java),
        NevButton(R.mipmap.ic_launcher, "示例", ExampleMenuFragment::class.java),
        NevButton(R.mipmap.ic_launcher, "文档", DocumentFragment::class.java),
    )

    data class NevButton(
        val icon: Int,
        val title: String,
        val fragment: Class<out Fragment>
    )

}