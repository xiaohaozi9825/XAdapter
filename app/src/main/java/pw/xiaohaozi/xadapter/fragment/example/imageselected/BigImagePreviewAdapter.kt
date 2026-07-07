package pw.xiaohaozi.xadapter.fragment.example.imageselected

import android.util.Log
import androidx.annotation.OptIn
import androidx.core.view.isVisible
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import androidx.viewpager2.widget.ViewPager2
import com.davemorrissey.labs.subscaleview.ImageSource
import kotlinx.coroutines.CoroutineScope
import pw.xiaohaozi.xadapter.databinding.ItemImagePreviewBinding
import pw.xiaohaozi.xadapter.databinding.ItemVideoPreviewBinding
import pw.xiaohaozi.xadapter.smart.adapter.SmartAdapter
import pw.xiaohaozi.xadapter.smart.holder.XHolder
import pw.xiaohaozi.xadapter.smart.provider.SmartProvider
import pw.xiaohaozi.xadapter.utils.LoadMediaFile
import pw.xiaohaozi.xadapter.utils.isVideo
import pw.xiaohaozi.xadapter.utils.load
import kotlin.math.absoluteValue

class BigImagePreviewAdapter(viewPager: ViewPager2) : SmartAdapter<ViewBinding, LoadMediaFile>() {
    companion object {
        const val TAG = "BigImagePreviewAdapter"
        const val ITEM_TYPE_IMAGE = 1//图片
        const val ITEM_TYPE_VIDEO = 2//视频
    }



    init {
        addProvider(ImageProvider(this), ITEM_TYPE_IMAGE)
        addProvider(VideoProvider(this, viewPager), ITEM_TYPE_VIDEO)

        //动态计算itemType
        customItemType { data, _ ->
            return@customItemType if (data.isVideo()) ITEM_TYPE_VIDEO else ITEM_TYPE_IMAGE
        }
    }

}

class ImageProvider(adapter: BigImagePreviewAdapter) :
    SmartProvider<ViewBinding, LoadMediaFile, ItemImagePreviewBinding, LoadMediaFile>(adapter) {
    override fun onCreated(holder: XHolder<ItemImagePreviewBinding>) {

    }

    override fun onBind(scope: CoroutineScope, holder: XHolder<ItemImagePreviewBinding>, data: LoadMediaFile, position: Int) {
        val uri = data.uri ?: return
        holder.binding.imageView.setImage(ImageSource.uri(uri))
    }

}

class VideoProvider(adapter: BigImagePreviewAdapter, val viewPager: ViewPager2) :
    SmartProvider<ViewBinding, LoadMediaFile, ItemVideoPreviewBinding, LoadMediaFile>(adapter) {
    companion object {
        const val TAG = "VideoProvider"
    }

    //为每个holder 记录一个 player
    private var players = hashMapOf<XHolder<ItemVideoPreviewBinding>, Player>()
    val callback = object : ViewPager2.OnPageChangeCallback() {
        var tempPosition = -1
        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            Log.i(
                TAG,
                "onPageScrolled: position = $position  positionOffset = $positionOffset positionOffsetPixels = $positionOffsetPixels"
            )
            //等待动画结束，判断是否切换了item，如果切换了item，如果切换了item，则调用onHide和onShow方法
            //positionOffset.absoluteValue < 0.0001 判断动画是否结束
            //tempPosition != position 判断是否切换了item
            if (tempPosition != position && positionOffset.absoluteValue < 0.0001) {
                if (tempPosition != -1) {
                    onHide(tempPosition)
                }
                onShow(position)
                tempPosition = position
            }
        }

        override fun onPageSelected(position: Int) {
            Log.i(TAG, "onPageSelected: $position")
//            该方法在滑动动画开始时就会调用，有可能还没有执行onBind方法
//            if (tempPosition != -1) {
//                onHide(tempPosition)
//            }
//            onShow(position)
//            tempPosition = position
        }

        override fun onPageScrollStateChanged(state: Int) {
            Log.i(TAG, "onPageScrollStateChanged: $state")
        }
    }

    init {
        viewPager.registerOnPageChangeCallback(callback)
    }

    override fun onCreated(holder: XHolder<ItemVideoPreviewBinding>) {
        Log.i(TAG, "onCreated ")
        players[holder] = ExoPlayer.Builder(holder.binding.playerView.context).build()
            .apply {
                //监听视频播放结束
                addListener(object : Player.Listener {
                    override fun onPlaybackStateChanged(playbackState: Int) {
                        super.onPlaybackStateChanged(playbackState)
                        //视频播放完成，重置为初始状态
                        if (playbackState == Player.STATE_ENDED) {
                            holder.binding.btnStart.isVisible = true
                            holder.binding.ivPreview.isVisible = true
                            playWhenReady = false
                            seekTo(0)
                        }
                    }
                })
            }
        //不显示控制器
        holder.binding.playerView.useController = false

        //点击控制播放和暂停
        holder.binding.playerView.setOnClickListener {
            val player = players[holder]
            if (player?.isPlaying == true) {
                player.pause()
                holder.binding.btnStart.isVisible = true
            } else {
                player?.play()
                holder.binding.btnStart.isVisible = false
                holder.binding.ivPreview.isVisible = false
            }
        }
    }

    @OptIn(UnstableApi::class)
    override fun onBind(scope: CoroutineScope, holder: XHolder<ItemVideoPreviewBinding>, data: LoadMediaFile, position: Int) {
        Log.i(TAG, "onBind: $position")
        val uri = data.uri ?: return
        //设置预览图片
        holder.binding.ivPreview.load(uri)
        holder.binding.btnStart.isVisible = true
        holder.binding.ivPreview.isVisible = true

        //设置视频资源
        holder.binding.playerView.player = players[holder]
            ?.apply { setMediaItem(MediaItem.fromUri(uri)) }
    }

    //viewHolder 附着到recyclerView上时，准备好资源
    override fun onHolderAttachedToWindow(holder: XHolder<ItemVideoPreviewBinding>) {
        super.onHolderAttachedToWindow(holder)
        //当viewPager.offscreenPageLimit >= 1 时，
        // item预加载时就会执行该方法，但不一定会显示，所以在onShow方法中准备会比较合理
        //prepare(holder)
    }

    //viewHolder 脱离recyclerView上时，停止播放
    override fun onHolderDetachedFromWindow(holder: XHolder<ItemVideoPreviewBinding>) {
        super.onHolderDetachedFromWindow(holder)
        //当viewPager.offscreenPageLimit >= 1 时，
        // item不可见时并不会调用该方法，所以在onHide方法中停止会比较合理
        //stop(holder)
    }

    override fun onRecyclerViewAttachedToWindow(recyclerView: RecyclerView) {
        super.onRecyclerViewAttachedToWindow(recyclerView)
        //此方法不会被回调，因此在初始化时viewPager.registerOnPageChangeCallback(callback)比较合理
        Log.i(TAG, "onRecyclerViewAttachedToWindow: ")
    }

    override fun onRecyclerViewDetachedFromWindow(recyclerView: RecyclerView) {
        super.onRecyclerViewDetachedFromWindow(recyclerView)
        Log.i(TAG, "onRecyclerViewDetachedFromWindow: ")
        viewPager.unregisterOnPageChangeCallback(callback)
        //销毁资源
        players.forEach { (_, player) -> player.release() }
        players.clear()
    }


    private fun onShow(position: Int) {
        Log.i(TAG, "onShow: $position")
        val holder = players.keys.find { it.bindingAdapterPosition == position }
        if (holder != null) prepare(holder)
    }

    private fun onHide(position: Int) {
        Log.i(TAG, "onHide: $position")
        val holder = players.keys.find { it.bindingAdapterPosition == position }
        if (holder != null) stop(holder)
    }

    /**
     * 视频准备
     * 可以在onHolderAttachedToWindow中调用，提前准备
     * 可以在onShow中调用，显示时准备
     * 可以在click中调用，点击时再准备
     */
    private fun prepare(holder: XHolder<ItemVideoPreviewBinding>) {
        holder.binding.btnStart.isVisible = true
        holder.binding.ivPreview.isVisible = true
        players[holder]
            ?.apply { playWhenReady = false }
            ?.apply { seekTo(0) }
            ?.apply { prepare() }
    }

    private fun stop(holder: XHolder<ItemVideoPreviewBinding>) {
        holder.binding.btnStart.isVisible = true
        holder.binding.ivPreview.isVisible = true
        players[holder]?.apply { stop() }
    }
}