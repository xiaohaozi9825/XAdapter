package pw.xiaohaozi.xadapter.smart.impl

import android.view.View
import android.widget.CompoundButton
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.viewbinding.ViewBinding
import pw.xiaohaozi.xadapter.smart.XAdapterException
import pw.xiaohaozi.xadapter.smart.adapter.XAdapter
import pw.xiaohaozi.xadapter.smart.entity.DEFAULT_PAGE
import pw.xiaohaozi.xadapter.smart.entity.EMPTY
import pw.xiaohaozi.xadapter.smart.entity.FOOTER
import pw.xiaohaozi.xadapter.smart.entity.HEADER
import pw.xiaohaozi.xadapter.smart.holder.XHolder
import pw.xiaohaozi.xadapter.smart.provider.TypeProvider
import pw.xiaohaozi.xadapter.smart.proxy.EventProxy
import pw.xiaohaozi.xadapter.smart.proxy.OnItemCheckedChangeListener
import pw.xiaohaozi.xadapter.smart.proxy.OnItemClickListener
import pw.xiaohaozi.xadapter.smart.proxy.OnItemLongClickListener
import pw.xiaohaozi.xadapter.smart.proxy.OnItemTextChange
import pw.xiaohaozi.xadapter.smart.proxy.XEmployer
import pw.xiaohaozi.xadapter.smart.proxy.XProxy
import java.lang.reflect.ParameterizedType


/**
 * [EventProxy] 的默认实现：在 ViewHolder 创建后按 map 注册点击、长按、CompoundButton 与文本监听。
 *
 * 描述：通过 [XEmployer] 取宿主 [XAdapter]；头/脚/空/缺省页等不参与 Adapter 级自动注册。
 * 作者：小耗子
 * github：https://github.com/xiaohaozi9825
 * 创建时间：2022/10/5 22:47
 */
class EventImpl<Employer : XProxy<Employer>, VB : ViewBinding, D> : EventProxy<Employer, VB, D> {
    override lateinit var employer: Employer
    private val adapter: XAdapter<*, *,*> by lazy {
        when (val e = employer) {
            is XEmployer -> e.getEmployerAdapter()
            else -> throw XAdapterException("找不到对应的Adapter对象")
        }
    }

    //adapter中的datas可以被重新赋值，所以不能用by lazy 的方式获取
    private fun getData() = adapter.getDataList()
    override val clickListenerMap: HashMap<Int?, OnItemClickListener<Employer, VB, D>> = hashMapOf()
    override val longClickListenerMap: HashMap<Int?, OnItemLongClickListener<Employer, VB, D>> = hashMapOf()
    override val checkedChangeListener: HashMap<Int?, OnItemCheckedChangeListener<Employer, VB, D>> = hashMapOf()
    override val textChangeMap: HashMap<Int?, OnItemTextChange<Employer, VB, D>> = hashMapOf()


    /**
     * 向 [XAdapter] 注册 ViewHolder 监听，在创建完成后根据已配置的 map 绑定事件。
     */
    override fun initProxy(employer: Employer) {
        super.initProxy(employer)
        adapter.addOnViewHolderChanges(object : XAdapter.OnViewHolderChanges {
            override fun onCreated(provide: TypeProvider<*, *>, holder: XHolder<*>) {
                if (employer == adapter) {//adapter
                    //adapter不对特殊布局设置事件监听
                    val genericSuperclass = provide.javaClass.genericSuperclass as? ParameterizedType
                    val arguments = genericSuperclass?.actualTypeArguments
                    if (arguments?.contains(HEADER::class.java) == true) return
                    if (arguments?.contains(FOOTER::class.java) == true) return
                    if (arguments?.contains(EMPTY::class.java) == true) return
                    if (arguments?.contains(DEFAULT_PAGE::class.java) == true) return
                    initListener(holder)
                } else {//provide
                    //provide 只为当前provide设置事件监听
                    if (provide == employer) {
                        initListener(holder)
                    }
                }
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
                val position = holder.getXPosition()
                value.invoke(employer, holder as XHolder<VB>,  holder.data as D, position, it)
            }
        }
        longClickListenerMap.forEach {
            val id = it.key
            val value = it.value
            val tagger: View = id?.let { holder.itemView.findViewById(it) } ?: holder.itemView
            tagger.setOnLongClickListener {
                val position = holder.getXPosition()
                value.invoke(employer, holder as XHolder<VB>, holder.data as D, position, it)
            }
        }
        checkedChangeListener.forEach {
            val id = it.key
            val value = it.value
            val tagger: CompoundButton? = (id?.let { holder.itemView.findViewById(it) } ?: holder.itemView) as? CompoundButton
            tagger?.setOnCheckedChangeListener { buttonView, isChecked ->
                val position = holder.getXPosition()
                value.invoke(employer, holder as XHolder<VB>,  holder.data as D, position, buttonView, isChecked)
            }
        }
        textChangeMap.forEach {
            val id = it.key
            val value = it.value
            val tagger: TextView? = (id?.let { holder.itemView.findViewById(it) } ?: holder.itemView) as? TextView
            tagger?.addTextChangedListener {
                val position = holder.getXPosition()
                value.invoke(employer, holder as XHolder<VB>,  holder.data as D, position, tagger, it)
            }
        }

    }

    /** 注册或覆盖指定 [id] 的 item 点击监听；[id] 为 null 时表示整项根布局。 */
    override fun setOnClickListener(id: Int?, listener: OnItemClickListener<Employer, VB, D>): Employer {
        clickListenerMap[id] = listener
        return employer
    }

    /** 注册或覆盖长按监听。 */
    override fun setOnLongClickListener(id: Int?, listener: OnItemLongClickListener<Employer, VB, D>): Employer {
        longClickListenerMap[id] = listener
        return employer
    }

    /** 注册 [CompoundButton]（如 CheckBox）选中状态变化监听。 */
    override fun setOnCheckedChangeListener(id: Int?, listener: OnItemCheckedChangeListener<Employer, VB, D>): Employer {
        checkedChangeListener[id] = listener
        return employer
    }

    /** 注册 [TextView] / [android.widget.EditText] 文本变化监听。 */
    override fun setOnTextChange(id: Int?, listener: OnItemTextChange<Employer, VB, D>): Employer {
        textChangeMap[id] = listener
        return employer
    }
}
