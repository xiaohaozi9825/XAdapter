package pw.xiaohaozi.xadapter.activity.home

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import pw.xiaohaozi.xadapter.activity.SearchActivity
import pw.xiaohaozi.xadapter.databinding.FragmentDocumentBinding
import pw.xiaohaozi.xadapter.databinding.FragmentHomeBinding


class DocumentFragment : Fragment() {
    lateinit var binding: FragmentDocumentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDocumentBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.webView.loadUrl("file:///android_asset/Document.html")
    }

}