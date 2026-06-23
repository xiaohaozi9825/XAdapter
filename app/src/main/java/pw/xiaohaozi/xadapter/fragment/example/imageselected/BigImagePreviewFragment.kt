package pw.xiaohaozi.xadapter.fragment.example.imageselected

import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import kotlinx.coroutines.launch
import pw.xiaohaozi.xadapter.databinding.FragmentBigImagePreviewBinding
import pw.xiaohaozi.xadapter.fragment.VBFragment

class BigImagePreviewFragment : VBFragment<FragmentBigImagePreviewBinding>() {
    private val viewmodel by activityViewModels<ImageSelectedViewModel>()
    val adapter by lazy { BigImagePreviewAdapter() }

    override fun FragmentBigImagePreviewBinding.initView() {
        adapter.bindLifecycle(viewLifecycleOwner)
        viewPager.adapter = adapter
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                viewmodel.curPosition.value = position
            }
        })
        lifecycleScope.launch {
            val mediaFiles = viewmodel.curMediaList.value
            val position = viewmodel.curPosition.value
            adapter.refresh(mediaFiles)
            viewPager.setCurrentItem(position,false)
        }
    }
}

