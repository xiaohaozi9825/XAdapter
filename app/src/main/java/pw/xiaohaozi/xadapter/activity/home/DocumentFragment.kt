package pw.xiaohaozi.xadapter.activity.home

import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import pw.xiaohaozi.xadapter.databinding.FragmentDocumentBinding
import pw.xiaohaozi.xadapter.fragment.VBFragment
import pw.xiaohaozi.xadapter.loadMarkDownByAsses


class DocumentFragment : VBFragment<FragmentDocumentBinding>() {

    override fun FragmentDocumentBinding.initView() {
        //binding.webView.loadUrl("file:///android_asset/Document.html")
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.webView.loadMarkDownByAsses(requireContext(), "Document.md", null)
    }

}