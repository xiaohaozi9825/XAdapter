package pw.xiaohaozi.xadapter.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.google.gson.reflect.TypeToken
import pw.xiaohaozi.xadapter.databinding.FragmentRecyclerBinding
import pw.xiaohaozi.xadapter.databinding.ItemNodeBinding
import pw.xiaohaozi.xadapter.node.ExpandedNodeEntity
import pw.xiaohaozi.xadapter.node.NodeAdapter
import pw.xiaohaozi.xadapter.node.NodeEntity
import pw.xiaohaozi.xadapter.smart.holder.XHolder
import pw.xiaohaozi.xadapter.smart.provider.XProvider


/**
 * 单布局
 */
class NodeFragment : Fragment() {
    private lateinit var binding: FragmentRecyclerBinding

    private val adapter = function()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRecyclerBinding.inflate(inflater)
        binding.recycleView.layoutManager = LinearLayoutManager(requireContext())
        binding.recycleView.adapter = adapter
        adapter.refresh(getList())
        return binding.root
    }

    fun function(): NodeAdapter<ItemNodeBinding> {
        val adapter = NodeAdapter<ItemNodeBinding>()
        val province = object : XProvider<ItemNodeBinding, ProvinceNode>(adapter) {
            override fun onCreated(holder: XHolder<ItemNodeBinding>) {
                holder.binding.tvContent.setOnClickListener {
                    val adapterPosition = holder.bindingAdapterPosition
                    val nodeEntity = adapter.getData()[adapterPosition] as ProvinceNode
                    if (nodeEntity.isExpanded()) adapter.collapse(adapterPosition, false)
                    else adapter.expand(adapterPosition, false)
                }
            }

            override fun onBind(holder: XHolder<ItemNodeBinding>, data: ProvinceNode, position: Int) {
                holder.binding.tvContent.text = "+ ${data.name}"
            }

            override fun isFixedViewType(): Boolean {
                return false
            }

        }
        val city = object : XProvider<ItemNodeBinding, CityNode>(adapter) {
            override fun onCreated(holder: XHolder<ItemNodeBinding>) {
                holder.binding.tvContent.setOnClickListener {
                    val adapterPosition = holder.bindingAdapterPosition
                    val nodeEntity = adapter.getData()[adapterPosition] as CityNode
                    if (nodeEntity.isExpanded()) adapter.collapse(adapterPosition, true)
                    else adapter.expand(adapterPosition)
                }
            }

            override fun onBind(holder: XHolder<ItemNodeBinding>, data: CityNode, position: Int) {
                holder.binding.tvContent.text = "  + ${data.name}"
            }

            override fun isFixedViewType(): Boolean {
                return false
            }

        }
        val area = object : XProvider<ItemNodeBinding, AreaNode>(adapter) {
            override fun onCreated(holder: XHolder<ItemNodeBinding>) {

            }

            override fun onBind(holder: XHolder<ItemNodeBinding>, data: AreaNode, position: Int) {
                holder.binding.tvContent.text = "         ${data.name}"
            }

            override fun isFixedViewType(): Boolean {
                return false
            }

        }
        adapter + province + city + area
        return adapter
    }

    private fun getList(): MutableList<ProvinceNode> {
        val json = requireContext().assets.open("省市县.json").readBytes().toString(Charsets.UTF_8)
        val gson = GsonBuilder()
            //由于县/区部分是字符串类型，而我们期望是AreaNode类型，所以这里需要自定义解析器
            .registerTypeAdapter(AreaNode::class.java, AreaNodeDeserializer())
            .create()
        return gson.fromJson(json, object : TypeToken<MutableList<ProvinceNode>>() {}.type)
    }
}


internal class AreaNodeDeserializer : JsonDeserializer<AreaNode> {
    @Throws(JsonParseException::class)
    override fun deserialize(
        json: JsonElement,
        typeOfT: java.lang.reflect.Type?,
        context: JsonDeserializationContext?
    ): AreaNode {
        val areaName = json.asString
        return AreaNode(areaName)
    }
}

data class ProvinceNode(val name: String, val city: MutableList<CityNode>) : NodeEntity<Unit, CityNode>, ExpandedNodeEntity {
    override var _parentNodeEntity: Unit? = null
    override var _nodeGrade: Int? = null
    override var _isExpanded: Boolean = true

    override fun getChildNodeEntityList(): MutableList<CityNode> {
        return city
    }

}

data class CityNode(val name: String, val area: ArrayList<AreaNode>) : NodeEntity<ProvinceNode, AreaNode>, ExpandedNodeEntity {
    override var _parentNodeEntity: ProvinceNode? = null
    override var _nodeGrade: Int? = null
    override var _isExpanded: Boolean = true

    override fun getChildNodeEntityList(): MutableList<AreaNode> {
        return area
    }


}

data class AreaNode(val name: String) : NodeEntity<CityNode, Unit> {
    override var _parentNodeEntity: CityNode? = null
    override var _nodeGrade: Int? = null
    override fun getChildNodeEntityList(): MutableList<Unit>? {
        return null
    }


}


