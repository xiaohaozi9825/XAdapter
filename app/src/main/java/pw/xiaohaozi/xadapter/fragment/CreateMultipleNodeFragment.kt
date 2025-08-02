package pw.xiaohaozi.xadapter.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.ViewBinding
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import pw.xiaohaozi.xadapter.R
import pw.xiaohaozi.xadapter.databinding.FragmentRecyclerBinding
import pw.xiaohaozi.xadapter.databinding.ItemCheckCheckboxBinding
import pw.xiaohaozi.xadapter.databinding.ItemCheckRadiobuttonBinding
import pw.xiaohaozi.xadapter.databinding.ItemNodeBinding
import pw.xiaohaozi.xadapter.node.NodeAdapter
import pw.xiaohaozi.xadapter.node.entity.ExpandedNodeEntity
import pw.xiaohaozi.xadapter.node.entity.NodeEntity
import pw.xiaohaozi.xadapter.node.ext.nodeAdapter
import pw.xiaohaozi.xadapter.node.ext.toAdapter
import pw.xiaohaozi.xadapter.node.ext.withType
import pw.xiaohaozi.xadapter.smart.adapter.SmartAdapter
import pw.xiaohaozi.xadapter.smart.ext.createAdapter
import pw.xiaohaozi.xadapterdemo.utils.GsonUtil.toJson
import java.lang.reflect.Type


class CreateMultipleNodeFragment : Fragment() {
    private lateinit var binding: FragmentRecyclerBinding

    @SuppressLint("SetTextI18n")
    val adapter = nodeAdapter()
        .withType<ItemNodeBinding, ProvinceNode> { (holder, data) ->
            holder.binding.tvContent.text = data.name
        }
        .withType<ItemNodeBinding, CityNode> { (holder, data) ->
            holder.binding.tvContent.text = "   ${data.name}"
        }
        .withType<ItemNodeBinding, AreaNode> { (holder, data) ->
            holder.binding.tvContent.text = "         ${data.name}"
        }.toAdapter()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRecyclerBinding.inflate(inflater)
        binding.recycleView.layoutManager = LinearLayoutManager(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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

    data class AreaNode(val name: String) : NodeEntity<CityNode, Unit> {
        override var xParentNodeEntity: CityNode? = null
        override var xNodeGrade: Int? = null
        override fun getChildNodeEntityList(): MutableList<Unit>? {
            return null
        }
    }

    data class ProvinceNode(val name: String, val city: MutableList<CityNode>) : NodeEntity<Unit, CityNode>, ExpandedNodeEntity {
        override var xParentNodeEntity: Unit? = null
        override var xNodeGrade: Int? = null

        @Transient
        override var xIsExpanded: Boolean = true

        init {
            xIsExpanded = true
            Log.i("是否展开", "init: ${xIsExpanded}")
        }

        override fun getChildNodeEntityList(): MutableList<CityNode> {
            return city
        }
    }

    data class CityNode(val name: String, val area: ArrayList<AreaNode>) : NodeEntity<ProvinceNode, AreaNode>, ExpandedNodeEntity {
        override var xParentNodeEntity: ProvinceNode? = null
        override var xNodeGrade: Int? = null

        @Transient
        override var xIsExpanded: Boolean = true

        override fun getChildNodeEntityList(): MutableList<AreaNode> {
            return area
        }
    }
}

