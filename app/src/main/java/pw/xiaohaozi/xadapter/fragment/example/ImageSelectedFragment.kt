package pw.xiaohaozi.xadapter.fragment.example

import android.Manifest
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.animation.addListener
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.hjq.permissions.Permission
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pw.xiaohaozi.myvideo.utils.permission.RPermissions
import pw.xiaohaozi.xadapter.R
import pw.xiaohaozi.xadapter.databinding.FragmentImageSelectBinding
import pw.xiaohaozi.xadapter.databinding.ItemImageSelectGroupBinding
import pw.xiaohaozi.xadapter.databinding.ItemImageSelectedBinding
import pw.xiaohaozi.xadapter.fragment.VBFragment
import pw.xiaohaozi.xadapter.smart.ext.createAdapter
import pw.xiaohaozi.xadapter.smart.ext.singleSelect
import pw.xiaohaozi.xadapter.utils.LoadLocalMedia
import pw.xiaohaozi.xadapter.utils.LoadMediaFile
import pw.xiaohaozi.xadapter.utils.load


class ImageSelectedFragment : VBFragment<FragmentImageSelectBinding>() {
    private val TAG = "ImageSelectedFragment"
    private var selectedList = mutableListOf<LoadMediaFile>()

    @SuppressLint("SetTextI18n")
    val groupAdapter = createAdapter<ItemImageSelectGroupBinding, MutableList<LoadMediaFile>> { (holder, data, position, payload) ->
        holder.binding.ivSelectState.isVisible = isSelected(data)
        if (payload.contains("select")) return@createAdapter
        val firstOrNull = data.firstOrNull()
        Log.i(TAG, "groupAdapter : ${firstOrNull?.path}")
        holder.binding.tvName.text = firstOrNull.groupName(position)
        holder.binding.ivImage.load(firstOrNull?.path)
        holder.binding.tvCount.text = "(${data.size})"
    }.singleSelect { data, position, index, fromUser ->
        if (fromUser) {
            binding.tvGroupName.postDelayed({ binding.tvGroupName.text = data.firstOrNull().groupName(position) }, 300)
            imageAdapter.refresh(data)
            binding.viewCover.isVisible = false
            binding.cvGroup.hint()
            binding.ivArrow.rotation(180f, 360f)
        }
    }

    @SuppressLint("SetTextI18n")
    private val imageAdapter = createAdapter<ItemImageSelectedBinding, LoadMediaFile> { (holder, data, _, payload,scope) ->
        val index = selectedList.indexOf(data)
        if (index < 0) {
            holder.binding.tvSelectedIndex.text = ""
            holder.binding.tvSelectedIndex.setBackgroundResource(R.drawable.bg_not_selected)
        } else {
            holder.binding.tvSelectedIndex.text = "${index + 1}"
            holder.binding.tvSelectedIndex.setBackgroundResource(R.drawable.bg_selected_position)
        }
        if (payload.contains("select")) return@createAdapter
        holder.binding.ivImage.load(data.path)
        //进一步验证协成，注意有些gif或视频类型的文件，这里是加载不了的，所以有空白是正常的。
//        scope.launch(IO) {
//            try {
//                val bitmap = BitmapFactory.decodeFile(data.path)
//                // 缩到500×500
//                val scaled = Bitmap.createScaledBitmap(bitmap, 500, 500, true)
//                bitmap.recycle() // 回收原Bitmap
//                withContext(Main) {
//                    holder.binding.ivImage.setImageBitmap(scaled)
//                }
//            } catch (e: Exception) {
//                Log.e(TAG, "图片加载失败:${data.name}", e)
//            }
//        }
    }.setOnClickListener { holder, data, position, view ->
        if (!selectedList.contains(data)) {
            selectedList.add(data)
            notifyItemChanged(position)
        } else {
            selectedList.remove(data)
            notifyAllItemChanged("select")
        }
        val size = selectedList.size
        binding.tvPreview.text = if (size == 0) "预览" else "预览（${size}）"
    }

    private fun LoadMediaFile?.groupName(position: Int): String {
        return if (position == 0) "图片与视频"
        else if (this?.path?.contains("/storage/emulated/0/Pictures/DCIM/Camera") == true) "相机"
        else if (this?.path?.contains("/storage/emulated/0/Pictures/WeiXin") == true) "微信"
        else if (this?.path?.contains("/storage/emulated/0/Pictures/Screenshots") == true) "截屏"
        else if (this?.path?.contains("/storage/emulated/0/Pictures/Download") == true) "下载"
        else this?.bucketDisplayName ?: ""
    }

    private fun View.show() {
        this.isVisible = true
        val animator = ObjectAnimator.ofFloat(this, "translationY", -this.height.toFloat(), 0f) // 水平移动100像素
        animator.setDuration(300)
        animator.start()
    }

    private fun View.hint() {
        val animator = ObjectAnimator.ofFloat(this, "translationY", 0f, -this.height.toFloat()) // 水平移动100像素
        animator.setDuration(300)
        animator.addListener(onEnd = { this.isVisible = false })
        animator.start()
    }

    private fun View.rotation(start: Float, end: Float) {
        val animator = ObjectAnimator.ofFloat(this, "rotation", start, end) // 水平移动100像素
        animator.setDuration(300)
        animator.start()
    }

    override fun FragmentImageSelectBinding.initView() {
        binding.rvGroup.adapter = groupAdapter
        binding.rvImage.adapter = imageAdapter
        binding.viewCover.setOnClickListener {
            it.isVisible = false
            binding.cvGroup.hint()
            binding.ivArrow.rotation(180f, 360f)
        }
        binding.btnGroup.setOnClickListener {
            if (binding.viewCover.isVisible) {
                binding.viewCover.isVisible = false
                binding.cvGroup.hint()
                binding.ivArrow.rotation(180f, 360f)
            } else {
                binding.viewCover.isVisible = true
                binding.cvGroup.show()
                binding.ivArrow.rotation(0f, 180f)
            }
        }
        RPermissions(requireContext())
            .use("选择文件")
            .permissions(if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) Manifest.permission.MANAGE_EXTERNAL_STORAGE else Permission.READ_EXTERNAL_STORAGE)
            .request { initData() }
    }

    private fun initData() {
        lifecycleScope.launch {
            val loadFiles = withContext(IO) { LoadLocalMedia().getLoadFiles(requireContext()) }
            groupAdapter.refresh(loadFiles)
            imageAdapter.refresh(loadFiles.firstOrNull() ?: mutableListOf())
            binding.cvGroup.post { binding.cvGroup.translationY = -binding.cvGroup.height.toFloat() }

        }
    }
}