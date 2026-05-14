package pw.xiaohaozi.xadapter.fragment.smart.provider

import kotlinx.coroutines.CoroutineScope
import pw.xiaohaozi.xadapter.databinding.ItemVerseBinding
import pw.xiaohaozi.xadapter.info.VerseInfo
import pw.xiaohaozi.xadapter.smart.adapter.SmartAdapter
import pw.xiaohaozi.xadapter.smart.holder.XHolder
import pw.xiaohaozi.xadapter.smart.provider.SmartProvider


class SingleProvider : SmartProvider<ItemVerseBinding, VerseInfo, ItemVerseBinding, VerseInfo>(SmartAdapter()) {
    init {
        adapter.addProvider(this)
    }

    override fun onCreated(holder: XHolder<ItemVerseBinding>) {
        //初始化ViewHolder
    }

    override fun onBind(scope: CoroutineScope, holder: XHolder<ItemVerseBinding>, data: VerseInfo, position: Int) {
        holder.binding.tvContent.text = data?.content
        holder.binding.tvAuthor.text = data?.author
    }

}