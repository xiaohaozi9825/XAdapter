package pw.xiaohaozi.xadapter.activity.home

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import pw.xiaohaozi.xadapter.activity.SearchActivity
import pw.xiaohaozi.xadapter.databinding.FragmentHomeBinding
import pw.xiaohaozi.xadapter.loadMarkDownByAsses


class HomeFragment : Fragment() {
    lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvSearch.setOnClickListener {
            startActivity(Intent(requireContext(), SearchActivity::class.java))
        }
        //binding.webView.loadUrl("file:///android_asset/index.html")

        binding.webView.loadMarkDownByAsses(requireContext(), "index.md")

    }

}