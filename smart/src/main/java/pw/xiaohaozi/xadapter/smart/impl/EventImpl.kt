package pw.xiaohaozi.xadapter.smart.impl

import android.view.View
import android.widget.CompoundButton
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.viewbinding.ViewBinding
import pw.xiaohaozi.xadapter.smart.XAdapterException
import pw.xiaohaozi.xadapter.smart.adapter.SmartAdapter
import pw.xiaohaozi.xadapter.smart.holder.SmartHolder
import pw.xiaohaozi.xadapter.smart.provider.TypeProvider
import pw.xiaohaozi.xadapter.smart.proxy.EventProxy
import pw.xiaohaozi.xadapter.smart.proxy.XEmployer
import pw.xiaohaozi.xadapter.smart.proxy.XProxy


/**
 *
 * 描述：
 * 作者：小耗子
 * 简书地址：https://www.jianshu.com/u/2a2ea7b43087
 * github：https://github.com/xiaohaozi9825
 * 创建时间：2022/10/5 22:47
 */
class EventImpl<Employer : XProxy<Employer>, VB : ViewBinding, D> : EventProxy<Employer, VB, D> {
    override lateinit var employer: Employer
    private val adapter: SmartAdapter<*, *> by lazy {
        when (val e = employer) {
            is XEmployer -> e.getEmployerAdapter()
            else -> throw XAdapterException("找不到对应的Adapter对象")
        }
    }

    //adapter中的datas可以被重新赋值，所以不能用by lazy 的方式获取
    private fun getDatas() = adapter.datas as MutableList<D>

    override val clickListenerMap: HashMap<Int?, Employer.(holder: SmartHolder<VB>, data: D, position: Int, view: View) -> Unit> =
        hashMapOf()
    override val longClickListenerMap: HashMap<Int?, Employer.(holder: SmartHolder<VB>, data: D, position: Int, view: View) -> Boolean> =
        hashMapOf()
    override val checkedChangeListener: HashMap<Int?, Employer.(holder: SmartHolder<VB>, data: D, position: Int, view: CompoundButton, isCheck: Boolean) -> Unit> =
        hashMapOf()
    override val textChangeMap: HashMap<Int?, Employer.(holder: SmartHolder<VB>, data: D, position: Int, view: TextView, text: CharSequence?) -> Unit> =
        hashMapOf()

    override fun initProxy(employer: Employer) {
        super.initProxy(employer)
        adapter.addOnViewHolderChanges(object : SmartAdapter.OnViewHolderChanges {
            override fun onCreated(provide: TypeProvider<*, *>, holder: SmartHolder<*>) {
                if (employer != adapter && provide != employer) return
                initListener(holder)
            }

            override fun onBinding(holder: SmartHolder<*>, position: Int) {

            }

        })
    }

    private fun initListener(holder: SmartHolder<*>) {
        clickListenerMap.forEach {
            val id = it.key
            val value = it.value
            val tagger: View = id?.let { holder.itemView.findViewById(it) } ?: holder.itemView
            tagger.setOnClickListener {
                val position = holder.adapterPosition
                val data = getDatas()[position]
                value.invoke(employer, holder as SmartHolder<VB>, data, position, it)
            }
        }
        longClickListenerMap.forEach {
            val id = it.key
            val value = it.value
            val tagger: View = id?.let { holder.itemView.findViewById(it) } ?: holder.itemView
            tagger.setOnLongClickListener {
                val position = holder.adapterPosition
                val data = getDatas()[position]
                value.invoke(employer, holder as SmartHolder<VB>, data, position, it)
            }
        }
        checkedChangeListener.forEach {
            val id = it.key
            val value = it.value
            val tagger: CompoundButton? =
                (id?.let { holder.itemView.findViewById(it) } ?: holder.itemView) as? CompoundButton
            tagger?.setOnCheckedChangeListener { buttonView, isChecked ->
                val position = holder.adapterPosition
                val data = getDatas()[position]
                value.invoke(
                    employer,
                    holder as SmartHolder<VB>,
                    data,
                    position,
                    buttonView,
                    isChecked
                )
            }
        }
        textChangeMap.forEach {
            val id = it.key
            val value = it.value
            val tagger: TextView? =
                (id?.let { holder.itemView.findViewById(it) } ?: holder.itemView) as? TextView
            tagger?.addTextChangedListener {
                val position = holder.adapterPosition
                val data = getDatas()[position]
                value.invoke(employer, holder as SmartHolder<VB>, data, position, tagger, it)
            }
        }

    }

    override fun setOnClickListener(
        id: Int?,
        listener: Employer.(holder: SmartHolder<VB>, data: D, position: Int, view: View) -> Unit
    ): Employer {
        clickListenerMap[id] = listener
        return employer
    }

    override fun setOnLongClickListener(
        id: Int?,
        listener: Employer.(holder: SmartHolder<VB>, data: D, position: Int, view: View) -> Boolean
    ): Employer {
        longClickListenerMap[id] = listener
        return employer
    }

    override fun setOnCheckedChangeListener(
        id: Int?,
        listener: Employer.(holder: SmartHolder<VB>, data: D, position: Int, view: CompoundButton, isCheck: Boolean) -> Unit
    ): Employer {
        checkedChangeListener[id] = listener
        return employer
    }

    override fun setOnTextChange(
        id: Int?,
        listener: Employer.(holder: SmartHolder<VB>, data: D, position: Int, view: TextView, text: CharSequence?) -> Unit
    ): Employer {
        textChangeMap[id] = listener
        return employer
    }
}