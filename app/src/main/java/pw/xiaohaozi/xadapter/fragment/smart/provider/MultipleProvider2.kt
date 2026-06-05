package pw.xiaohaozi.xadapter.fragment.smart.provider

import android.util.Log
import androidx.viewbinding.ViewBinding
import kotlinx.coroutines.CoroutineScope
import pw.xiaohaozi.xadapter.databinding.ItemVerseBinding
import pw.xiaohaozi.xadapter.info.VerseInfo
import pw.xiaohaozi.xadapter.smart.adapter.SmartAdapter
import pw.xiaohaozi.xadapter.smart.holder.XHolder
import pw.xiaohaozi.xadapter.smart.provider.SmartProvider

class MultipleProvider2(adapter: SmartAdapter<ViewBinding, Any?>) :
    SmartProvider<ViewBinding, Any?, ItemVerseBinding, VerseInfo?>(adapter) {
    val TAG = "MultipleProvider2"
    override fun onCreated(holder: XHolder<ItemVerseBinding>) {

    }

    override fun onBind(scope: CoroutineScope, holder: XHolder<ItemVerseBinding>, data: VerseInfo?, position: Int) {
        holder.binding.tvContent.text = data?.content
        holder.binding.tvAuthor.text = data?.author
        Log.i(TAG, "== onBind: $position")
    }

    override fun onHolderAttachedToWindow(holder: XHolder<ItemVerseBinding>) {
        super.onHolderAttachedToWindow(holder)
        Log.i(TAG, "++ onHolderAttachedToWindow: ${holder.bindingAdapterPosition}")
    }

    override fun onHolderDetachedFromWindow(holder: XHolder<ItemVerseBinding>) {
        super.onHolderDetachedFromWindow(holder)
        Log.i(TAG, "-- onHolderDetachedFromWindow: ${holder.bindingAdapterPosition}")
    }
}

