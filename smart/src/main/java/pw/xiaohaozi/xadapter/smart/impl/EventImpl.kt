package pw.xiaohaozi.xadapter.smart.impl

import android.view.View
import android.widget.CompoundButton
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.viewbinding.ViewBinding
import pw.xiaohaozi.xadapter.smart.XAdapterException
import pw.xiaohaozi.xadapter.smart.adapter.XAdapter
import pw.xiaohaozi.xadapter.smart.holder.XHolder
import pw.xiaohaozi.xadapter.smart.provider.TypeProvider
import pw.xiaohaozi.xadapter.smart.proxy.EventProxy
import pw.xiaohaozi.xadapter.smart.proxy.OnItemCheckedChangeListener
import pw.xiaohaozi.xadapter.smart.proxy.OnItemClickListener
import pw.xiaohaozi.xadapter.smart.proxy.OnItemLongClickListener
import pw.xiaohaozi.xadapter.smart.proxy.OnItemTextChange
import pw.xiaohaozi.xadapter.smart.proxy.XEmployer
import pw.xiaohaozi.xadapter.smart.proxy.XProxy
import kotlin.math.absoluteValue


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
    private val adapter: XAdapter<*, *> by lazy {
        when (val e = employer) {
            is XEmployer -> e.getEmployerAdapter()
            else -> throw XAdapterException("找不到对应的Adapter对象")
        }
    }

    //adapter中的datas可以被重新赋值，所以不能用by lazy 的方式获取
    private fun getDatas() = adapter.datas as MutableList<*>
    override val clickListenerMap: HashMap<Int?, OnItemClickListener<Employer, VB, D>> = hashMapOf()
    override val longClickListenerMap: HashMap<Int?, OnItemLongClickListener<Employer, VB, D>> = hashMapOf()
    override val checkedChangeListener: HashMap<Int?, OnItemCheckedChangeListener<Employer, VB, D>> = hashMapOf()
    override val textChangeMap: HashMap<Int?, OnItemTextChange<Employer, VB, D>> = hashMapOf()


    override fun initProxy(employer: Employer) {
        super.initProxy(employer)
        adapter.addOnViewHolderChanges(object : XAdapter.OnViewHolderChanges {
            override fun onCreated(provide: TypeProvider<*, *>, holder: XHolder<*>) {
                if (employer != adapter && provide != employer) return
                initListener(holder)
            }

            override fun onBinding(holder: XHolder<*>, position: Int) {

            }

            override fun onBinding(holder: XHolder<*>, position: Int, payloads: List<Any?>) {

            }

        })
    }

    private fun initListener(holder: XHolder<*>) {
        clickListenerMap.forEach {
            val id = it.key
            val value = it.value
            val tagger: View = id?.let { holder.itemView.findViewById(it) } ?: holder.itemView
            tagger.setOnClickListener {
                val position = holder.adapterPosition - adapter.getHeaderProviderCount()
                val data = if (position < 0) {
                    adapter.headers[holder.adapterPosition].third
                } else if (position >= getDatas().size) {
                    adapter.footers[holder.adapterPosition - (adapter.itemCount - adapter.footers.size)].third
                } else {
                    getDatas()[position]
                }
                value.invoke(employer, holder as XHolder<VB>, data as D, position, it)
            }
        }
        longClickListenerMap.forEach {
            val id = it.key
            val value = it.value
            val tagger: View = id?.let { holder.itemView.findViewById(it) } ?: holder.itemView
            tagger.setOnLongClickListener {
                val position = holder.adapterPosition - adapter.getHeaderProviderCount()
                val data = if (position < 0) {
                    adapter.headers[holder.adapterPosition].third
                } else if (position >= getDatas().size) {
                    adapter.footers[position - getDatas().size].third
                } else {
                    getDatas()[position]
                }
                value.invoke(employer, holder as XHolder<VB>, data as D, position, it)
            }
        }
        checkedChangeListener.forEach {
            val id = it.key
            val value = it.value
            val tagger: CompoundButton? = (id?.let { holder.itemView.findViewById(it) } ?: holder.itemView) as? CompoundButton
            tagger?.setOnCheckedChangeListener { buttonView, isChecked ->
                val position = holder.adapterPosition - adapter.getHeaderProviderCount()
                val data = if (position < 0) {
                    adapter.headers[holder.adapterPosition].third
                } else if (position >= getDatas().size) {
                    adapter.footers[position - getDatas().size].third
                } else {
                    getDatas()[position]
                }
                value.invoke(employer, holder as XHolder<VB>, data as D, position, buttonView, isChecked)
            }
        }
        textChangeMap.forEach {
            val id = it.key
            val value = it.value
            val tagger: TextView? = (id?.let { holder.itemView.findViewById(it) } ?: holder.itemView) as? TextView
            tagger?.addTextChangedListener {
                val position = holder.adapterPosition - adapter.getHeaderProviderCount()
                val data = if (position < 0) {
                    adapter.headers[holder.adapterPosition].third
                } else if (position >= getDatas().size) {
                    adapter.footers[position - getDatas().size].third
                } else {
                    getDatas()[position]
                }
                value.invoke(employer, holder as XHolder<VB>, data as D, position, tagger, it)
            }
        }

    }

    override fun setOnClickListener(id: Int?, listener: OnItemClickListener<Employer, VB, D>): Employer {
        clickListenerMap[id] = listener
        return employer
    }

    override fun setOnLongClickListener(id: Int?, listener: OnItemLongClickListener<Employer, VB, D>): Employer {
        longClickListenerMap[id] = listener
        return employer
    }

    override fun setOnCheckedChangeListener(id: Int?, listener: OnItemCheckedChangeListener<Employer, VB, D>): Employer {
        checkedChangeListener[id] = listener
        return employer
    }

    override fun setOnTextChange(id: Int?, listener: OnItemTextChange<Employer, VB, D>): Employer {
        textChangeMap[id] = listener
        return employer
    }
}