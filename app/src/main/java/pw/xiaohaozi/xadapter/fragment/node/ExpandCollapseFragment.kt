package pw.xiaohaozi.xadapter.fragment.node

import android.annotation.SuppressLint
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.google.gson.reflect.TypeToken
import pw.xiaohaozi.xadapter.databinding.FragmentRecyclerBinding
import pw.xiaohaozi.xadapter.databinding.ItemNodeBinding
import pw.xiaohaozi.xadapter.fragment.VBFragment
import pw.xiaohaozi.xadapter.node.entity.ExpandedNodeEntity
import pw.xiaohaozi.xadapter.node.entity.NodeEntity
import pw.xiaohaozi.xadapter.node.ext.nodeAdapter
import java.lang.reflect.Type

//展开与折叠
class ExpandCollapseFragment : VBFragment<FragmentRecyclerBinding>() {
    @SuppressLint("SetTextI18n")
    val adapter = nodeAdapter()
        .withType<ItemNodeBinding, ProvinceNode> { (holder, data) ->
            holder.binding.tvContent.text = "${if (data.isExpanded()) "-" else "+"}${data.name}"
        }.setOnClickListener { holder, data, position, view ->
            if (data.isExpanded()) adapter.collapse(position, false)
            else adapter.expand(position, false)
        }.withType<ItemNodeBinding, CityNode> { (holder, data) ->
            holder.binding.tvContent.text = "  ${if (data.isExpanded()) "-" else "+"}${data.name}"
        }.setOnClickListener { holder, data, position, view ->
            if (data.isExpanded()) adapter.collapse(position, true)
            else adapter.expand(position)
        }.withType<ItemNodeBinding, AreaNode> { (holder, data) ->
            holder.binding.tvContent.text = "         ${data.name}"
        }.toAdapter()


    override fun FragmentRecyclerBinding.initView() {
        binding.recycleView.layoutManager = LinearLayoutManager(requireContext())
        binding.recycleView.adapter = adapter
        val list = getList()
        Log.i("是否展开", "onViewCreated: ${list[0].xIsExpanded}")
        adapter.refresh(getList())
    }

    private fun getList(): MutableList<ProvinceNode> {
        val json = requireContext().assets.open("省市县.json").readBytes().toString(Charsets.UTF_8)
        val gson = GsonBuilder()
            //由于县/区部分是字符串类型，而我们期望是AreaNode类型，所以这里需要自定义解析器
            .registerTypeAdapter(AreaNode::class.java, AreaNodeDeserializer())
            .registerTypeAdapter(CityNode::class.java, CityNodeDeserializer())
            .registerTypeAdapter(ProvinceNode::class.java, ProvinceNodeDeserializer())
            .create()
        return gson.fromJson(json, object : TypeToken<MutableList<ProvinceNode>>() {}.type)
    }

    internal class ProvinceNodeDeserializer : JsonDeserializer<ProvinceNode> {
        override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext): ProvinceNode {
            val gson = GsonBuilder()
                .registerTypeAdapter(CityNode::class.java, CityNodeDeserializer())
                .registerTypeAdapter(AreaNode::class.java, AreaNodeDeserializer())
                .create()
            val obj = gson.fromJson<ProvinceNode>(json, typeOfT)
//            val obj = context.deserialize<ProvinceNode>(json, typeOfT)
            obj.xIsExpanded = true
            return obj
        }
    }

    internal class CityNodeDeserializer : JsonDeserializer<CityNode> {
        override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext): CityNode {
            val gson = GsonBuilder()
                .registerTypeAdapter(AreaNode::class.java, AreaNodeDeserializer())
                .create()
            val obj = gson.fromJson<CityNode>(json, typeOfT)
            obj.xIsExpanded = true
            return obj
        }
    }

    internal class AreaNodeDeserializer : JsonDeserializer<AreaNode> {
        @Throws(JsonParseException::class)
        override fun deserialize(json: JsonElement, typeOfT: Type?, context: JsonDeserializationContext?): AreaNode {
            val areaName = json.asString
            return AreaNode(areaName)
        }
    }

    data class ProvinceNode(val name: String, val city: MutableList<CityNode>) : NodeEntity<Unit, CityNode>, ExpandedNodeEntity {
        override var xParentNodeEntity: Unit? = null
        override var xNodeGrade: Int? = null

        @Transient
        override var xIsExpanded: Boolean? = null

        override fun isExpanded(): Boolean {
            return xIsExpanded ?: true
        }

        override fun getChildNodeEntityList(): MutableList<CityNode> {
            return city
        }
    }

    data class CityNode(val name: String, val area: ArrayList<AreaNode>) : NodeEntity<ProvinceNode, AreaNode>, ExpandedNodeEntity {
        override var xParentNodeEntity: ProvinceNode? = null
        override var xNodeGrade: Int? = null

        @Transient
        override var xIsExpanded: Boolean? = true

        override fun getChildNodeEntityList(): MutableList<AreaNode> {
            return area
        }
    }

    data class AreaNode(val name: String) : NodeEntity<CityNode, Unit> {
        override var xParentNodeEntity: CityNode? = null
        override var xNodeGrade: Int? = null
        override fun getChildNodeEntityList(): MutableList<Unit>? {
            return null
        }
    }

}