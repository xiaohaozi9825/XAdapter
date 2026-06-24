# 大图预览&视频播放Adapter
> 视频播放逻辑较为复杂，可以使用继承的方式创建adapter和provider
## 用法

### 创建Adapter
```kotlin
class BigImagePreviewAdapter : SmartAdapter<ViewBinding, LoadMediaFile>() {
    companion object {
        const val TAG = "BigImagePreviewAdapter"
        const val ITEM_TYPE_IMAGE = 1//图片
        const val ITEM_TYPE_VIDEO = 2//视频
    }

    fun LoadMediaFile.isImage() = mimeType?.contains("image") == true
    fun LoadMediaFile.isVideo() = mimeType?.contains("video") == true

    init {
        addProvider(ImageProvider(this), ITEM_TYPE_IMAGE)
        addProvider(VideoProvider(this), ITEM_TYPE_VIDEO)

        //动态计算itemType
        customItemType { data, _ ->
            return@customItemType if (data.isVideo()) ITEM_TYPE_VIDEO else ITEM_TYPE_IMAGE
        }
    }

}
```

### 创建图片预览Provider
```kotlin
class ImageProvider(adapter: BigImagePreviewAdapter) :
    SmartProvider<ViewBinding, LoadMediaFile, ItemImagePreviewBinding, LoadMediaFile>(adapter) {
    override fun onCreated(holder: XHolder<ItemImagePreviewBinding>) {

    }

    override fun onBind(scope: CoroutineScope, holder: XHolder<ItemImagePreviewBinding>, data: LoadMediaFile, position: Int) {
        val uri = data.uri ?: return
        holder.binding.imageView.setImage(ImageSource.uri(uri))
    }

}
```

### 创建视频播放Provider
```kotlin
class VideoProvider(adapter: BigImagePreviewAdapter) :
    SmartProvider<ViewBinding, LoadMediaFile, ItemVideoPreviewBinding, LoadMediaFile>(adapter) {
    companion object {
        const val TAG = "VideoProvider"
    }

    //为每个holder 记录一个 player
    private var players = hashMapOf<XHolder<ItemVideoPreviewBinding>, Player>()

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
                            prepare()
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
        val uri = data.uri ?: return
        //设置预览图片
        holder.binding.ivPreview.load(uri)
        holder.binding.btnStart.isVisible = true
        holder.binding.ivPreview.isVisible = true

        //设置视频资源
        holder.binding.playerView.player = players[holder]
            ?.apply { setMediaItem(MediaItem.fromUri(uri)) }
    }

    override fun onHolderAttachedToWindow(holder: XHolder<ItemVideoPreviewBinding>) {
        super.onHolderAttachedToWindow(holder)
        //viewpager2 item可见时，重置视频为初始状态
        holder.binding.btnStart.isVisible = true
        holder.binding.ivPreview.isVisible = true
        players[holder]
            ?.apply { playWhenReady = false }
            ?.apply { seekTo(0) }
            ?.apply { prepare() }
    }

    override fun onHolderDetachedFromWindow(holder: XHolder<ItemVideoPreviewBinding>) {
        super.onHolderDetachedFromWindow(holder)
        //viewpager2 item不可见时，停止播放
        players[holder]?.apply { stop() }
    }

    override fun onViewRecyclerDetachedFromWindow(recyclerView: RecyclerView) {
        super.onViewRecyclerDetachedFromWindow(recyclerView)
        //销毁资源
        players.forEach { (_, player) -> player.release() }
        players.clear()
    }
}
```
