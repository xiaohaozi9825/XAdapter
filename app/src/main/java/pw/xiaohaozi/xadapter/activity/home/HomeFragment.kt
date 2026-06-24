package pw.xiaohaozi.xadapter.activity.home

import android.content.Intent
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import pw.xiaohaozi.xadapter.activity.SearchActivity
import pw.xiaohaozi.xadapter.databinding.FragmentHomeBinding
import pw.xiaohaozi.xadapter.fragment.VBFragment
import pw.xiaohaozi.xadapter.loadMarkDownByAsses


class HomeFragment : VBFragment<FragmentHomeBinding>() {

    override fun FragmentHomeBinding.initView() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        tvSearch.setOnClickListener {
            startActivity(Intent(requireContext(), SearchActivity::class.java))
        }
        //binding.webView.loadUrl("file:///android_asset/index.html")

        webView.loadMarkDownByAsses(requireContext(), "index.md",null)
    }

}