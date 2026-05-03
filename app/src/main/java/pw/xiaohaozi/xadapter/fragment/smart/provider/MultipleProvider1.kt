package pw.xiaohaozi.xadapter.fragment.smart.provider

import androidx.viewbinding.ViewBinding
import pw.xiaohaozi.xadapter.databinding.ItemImageCardBinding
import pw.xiaohaozi.xadapter.smart.adapter.SmartAdapter
import pw.xiaohaozi.xadapter.smart.holder.XHolder
import pw.xiaohaozi.xadapter.smart.provider.SmartProvider

class MultipleProvider1(override val adapter: SmartAdapter<ViewBinding, Any?>) :
    SmartProvider<ViewBinding, Any?, ItemImageCardBinding, Int>(adapter) {

    override fun onCreated(holder: XHolder<ItemImageCardBinding>) {

    }

    override fun onBind(holder: XHolder<ItemImageCardBinding>, data: Int, position: Int) {
        holder.binding.image.setImageResource(data)
    }

}

