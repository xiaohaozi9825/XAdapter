package pw.xiaohaozi.xadapter.fragment

import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.ViewBinding
import pw.xiaohaozi.xadapter.R
import pw.xiaohaozi.xadapter.databinding.FragmentNodeEditBinding
import pw.xiaohaozi.xadapter.databinding.ItemNodeEditBinding
import pw.xiaohaozi.xadapter.dialog.InputDialog
import pw.xiaohaozi.xadapter.node.NodeAdapter
import pw.xiaohaozi.xadapter.node.entity.ExpandedNodeEntity
import pw.xiaohaozi.xadapter.node.entity.NodeEntity
import pw.xiaohaozi.xadapter.node.ext.nodeAdapter
import pw.xiaohaozi.xadapter.node.ext.toAdapter
import pw.xiaohaozi.xadapter.node.ext.withType


/**
 * 单布局
 */
class Node2EditFragment : Fragment() {
    private lateinit var binding: FragmentNodeEditBinding

    private val adapter = function()
    var index = 1
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNodeEditBinding.inflate(inflater)
        binding.recycleView.layoutManager = LinearLayoutManager(requireContext())
        binding.recycleView.adapter = adapter
        adapter.refresh(dataList)
        binding.btnAddData.setOnClickListener {
            //添加一个节点
            adapter.addNode(NodeInfo1("${++index}", "Node1", mutableListOf()), adapter.source?.size)
            //添加多个节点
//            val nodes = arrayListOf(NodeInfo("${++index}", "Node1"), NodeInfo("${++index}", "Node2"))
//            adapter.addNode(nodes, adapter.source?.size)


        }
        return binding.root
    }


    fun function(): NodeAdapter<ViewBinding, NodeEntity<*, *>> {
        val adapter = nodeAdapter()
            .withType<ItemNodeEditBinding, NodeInfo1> { (holder, data) ->
                //创建一级菜单
                holder.binding
                    .apply { tvContent.text = data.no + "、" + data.text }
                    .apply { tvContent.setTypeface(Typeface.DEFAULT_BOLD) }
                    .apply { btnAdd.isVisible = true }
                    .apply { btnEdit.isVisible = false }
                    .apply { ivArrow.isInvisible = false }
                    .apply { ivArrow.rotation = if (data.isExpanded()) 0f else -90f  }
            }.setOnClickListener { holder, data, position, view ->
                if (data.isExpanded()) adapter.collapse(position, )
                else adapter.expand(position,true)
            }.setOnClickListener(R.id.btn_add) { holder, data, position, view ->
                //点击一级菜单，添加二级菜单数据
                val size = data.getChildNodeEntityList().size
                getNodeAdapter().addChildNode(data, NodeInfo2(" ${data.no}$size", "Node2", mutableListOf()))
                if (!data.isExpanded())adapter.expand(position)
            }.withType<ItemNodeEditBinding, NodeInfo2> { (holder, data) ->
                //创建二级菜单
                holder.binding
                    .apply { tvContent.text = data.no + "、" + data.text }
                    .apply { tvContent.setTypeface(Typeface.DEFAULT) }
                    .apply { btnAdd.isVisible = true }
                    .apply { btnEdit.isVisible = false }
                    .apply { ivArrow.isInvisible = false }
                    .apply { ivArrow.rotation = if (data.isExpanded()) 0f else -90f  }
            }.setOnClickListener { holder, data, position, view ->
                if (data.isExpanded()) adapter.collapse(position, true)
                else adapter.expand(position)
            }.setOnClickListener(R.id.btn_add) { holder, data, position, view ->
                val size = data.getChildNodeEntityList().size
                getNodeAdapter().addChildNode(
                    data,
                    NodeInfo3("  ${data.no}${size}", "Node3"),
                    //size//在末尾添加数据，该参数可不传
                )
                if (!data.isExpanded())adapter.expand(position)
            }.withType<ItemNodeEditBinding, NodeInfo3> { (holder, data) ->
                //创建三级菜单
                holder.binding
                    .apply { tvContent.text = data.no + "、" + data.text }
                    .apply { tvContent.setTypeface(null, Typeface.ITALIC) }
                    .apply { btnAdd.isVisible = false }
                    .apply { btnEdit.isVisible = true }
                    .apply { ivArrow.isInvisible = true }
            }
            .setOnClickListener(R.id.btn_edit) { holder, data, position, view ->
                InputDialog(requireActivity())
                    .setTitle("编辑内容")
                    .setMsg(data.text)
                    .onConfirm { _, content: String ->
                        data.text = content
//                        adapter.upDate(data)
//                        adapter.upDate(position,data)
//                        adapter.updateAt(getDataList().indexOf(data))
                    }
                    .onCancel {}
                    .show()
            }
            .toAdapter()
            .setOnClickListener(R.id.btn_delete) { holder, data, position, view ->
                //设置全局点击事件
                if (position == -1) return@setOnClickListener
                //移除指定位置节点
                adapter.removeNodePosition(position)
            }
        return adapter
    }

    val dataList = mutableListOf(
        NodeInfo1("0", "Node1", mutableListOf(NodeInfo2(" 00", "Node2", mutableListOf(NodeInfo3("  000", "Node3"))))),
        NodeInfo1("1", "Node1", mutableListOf(NodeInfo2(" 10", "Node2", mutableListOf(NodeInfo3("  100", "Node3"))))),
    )

    class NodeInfo1(val no: String, val text: String, private val childList: MutableList<NodeInfo2>) :
        NodeEntity<Unit, NodeInfo2>, ExpandedNodeEntity {
        override var xParentNodeEntity: Unit? = null
        override var xNodeGrade: Int? = null
        override var xIsExpanded: Boolean = true

        override fun getChildNodeEntityList(): MutableList<NodeInfo2> {
            return childList
        }

        override fun toString(): String {
            return no
        }
    }

    class NodeInfo2(val no: String, val text: String, private val childList: MutableList<NodeInfo3>) :
        NodeEntity<NodeInfo1, NodeInfo3>, ExpandedNodeEntity {
        override var xParentNodeEntity: NodeInfo1? = null
        override var xNodeGrade: Int? = null
        override var xIsExpanded: Boolean = true

        override fun getChildNodeEntityList(): MutableList<NodeInfo3> {
            return childList
        }

        override fun toString(): String {
            return no
        }
    }

    class NodeInfo3(val no: String, var text: String) : NodeEntity<NodeInfo2, String> {
        private val childList: MutableList<String> = mutableListOf()
        override var xParentNodeEntity: NodeInfo2? = null
        override var xNodeGrade: Int? = null

        override fun getChildNodeEntityList(): MutableList<String> {
            return childList
        }

        override fun toString(): String {
            return no
        }
    }
}


