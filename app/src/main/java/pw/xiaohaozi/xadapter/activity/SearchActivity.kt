package pw.xiaohaozi.xadapter.activity

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isGone
import androidx.lifecycle.lifecycleScope
import coil.load
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pw.xiaohaozi.xadapter.databinding.ActivitySearchBinding
import pw.xiaohaozi.xadapter.databinding.ItemSearchBinding
import pw.xiaohaozi.xadapter.enableEdgeToEdge
import pw.xiaohaozi.xadapter.exampleMenuList
import pw.xiaohaozi.xadapter.info.HomeInfo
import pw.xiaohaozi.xadapter.nodeMenuList
import pw.xiaohaozi.xadapter.smart.ext.createAdapter
import pw.xiaohaozi.xadapter.smartMenuList
import java.util.regex.Pattern

class SearchActivity : AppCompatActivity() {
    val binding by lazy { ActivitySearchBinding.inflate(layoutInflater) }

    // 原始数据
    private val originalList = (smartMenuList + nodeMenuList+ exampleMenuList).filterIsInstance<HomeInfo>()

    //创建adapter
    val adapter = createAdapter<ItemSearchBinding, HomeInfo> { (holder, data, position) ->
        holder.binding.apply {
            val keyword = binding.tvSearch.text.toString().trim()
            ivIcon.load(data.url)
            tvTitle.highlight(data.label, keyword)
            tvContent.highlight(data.msg, keyword)
            division.isGone = position == getDataList().lastIndex
        }
    }.setOnClickListener { holder, data, position, view ->
        val clazz = data.clazz
        if (clazz == null) {
            Toast.makeText(this@SearchActivity, "敬请期待", Toast.LENGTH_SHORT).show()
        } else
            toEmptyActivity(clazz, data.label, clazz.simpleName)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        //初始化搜索框，主要设置文本变化监听
        initSearchView()
        //左上角返回按钮
        binding.ivGoBack.setOnClickListener { finish() }
        //设置adapter
        binding.rvList.adapter = adapter
        //初始化数据
        adapter.refresh(originalList)
    }

    private fun initSearchView() {
        binding.tvSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                //模糊匹配过滤
                filterData(s.toString().trim())
            }
        })
    }


    // 模糊匹配过滤（不区分大小写）
    private fun filterData(keyword: String) {
        lifecycleScope.launch(Default) {
            val filteredList = originalList
                .filter { it.label.contains(keyword, true) || it.msg.contains(keyword, true) }
                .toMutableList()
            withContext(Main) { adapter.refresh(filteredList) }
        }
    }

    //高亮显示文本
    private fun TextView.highlight(text: String, keyword: String) {
        // 创建可样式化的文本
        val spannable = SpannableStringBuilder(text)

        // 高亮所有匹配位置（不区分大小写）
        val regex = Pattern.compile(keyword, Pattern.CASE_INSENSITIVE).matcher(text)
        while (regex.find()) {
            val start = regex.start()
            val end = regex.end()
            spannable.setSpan(
                ForegroundColorSpan(Color.RED), // 红色高亮
                start, end,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            spannable.setSpan(
                StyleSpan(Typeface.BOLD), // 加粗
                start, end,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            spannable.setSpan(
                BackgroundColorSpan(Color.YELLOW), // 黄色背景
                start, end,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        this.text = spannable

    }
}