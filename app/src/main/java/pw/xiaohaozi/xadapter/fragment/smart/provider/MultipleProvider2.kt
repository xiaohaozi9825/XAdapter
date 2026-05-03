package pw.xiaohaozi.xadapter.fragment.smart.provider

import androidx.viewbinding.ViewBinding
import pw.xiaohaozi.xadapter.databinding.ItemImageCardBinding
import pw.xiaohaozi.xadapter.databinding.ItemVerseBinding
import pw.xiaohaozi.xadapter.info.VerseInfo
import pw.xiaohaozi.xadapter.smart.adapter.SmartAdapter
import pw.xiaohaozi.xadapter.smart.holder.XHolder
import pw.xiaohaozi.xadapter.smart.provider.SmartProvider

class MultipleProvider2(override val adapter: SmartAdapter<ViewBinding, Any?>) :
    SmartProvider<ViewBinding, Any?, ItemVerseBinding, VerseInfo?>(adapter) {

    override fun onCreated(holder: XHolder<ItemVerseBinding>) {

    }

    override fun onBind(holder: XHolder<ItemVerseBinding>, data: VerseInfo?, position: Int) {
        holder.binding.tvContent.text = data?.content
        holder.binding.tvAuthor.text = data?.author
    }

}

