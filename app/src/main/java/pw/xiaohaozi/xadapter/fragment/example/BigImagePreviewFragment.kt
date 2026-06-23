package pw.xiaohaozi.xadapter.fragment.example

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.transition.ChangeBounds
import androidx.transition.Fade
import androidx.transition.TransitionInflater
import androidx.transition.TransitionSet
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import kotlinx.coroutines.launch
import pw.xiaohaozi.xadapter.R
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

