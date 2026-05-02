package pw.xiaohaozi.xadapter.activity.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import androidx.fragment.app.Fragment
import pw.xiaohaozi.xadapter.databinding.FragmentDocumentBinding
import pw.xiaohaozi.xadapter.loadMarkDownByAsses


class DocumentFragment : Fragment() {
    lateinit var binding: FragmentDocumentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDocumentBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //binding.webView.loadUrl("file:///android_asset/Document.html")

        binding.webView.loadMarkDownByAsses(requireContext(), "Document.md")
    }

}