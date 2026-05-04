package pw.xiaohaozi.xadapter.fragment.node

import android.graphics.Typeface
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.ViewBinding
import pw.xiaohaozi.xadapter.R
import pw.xiaohaozi.xadapter.databinding.FragmentNodeEditBinding
import pw.xiaohaozi.xadapter.databinding.ItemNodeEditBinding
import pw.xiaohaozi.xadapter.fragment.VBFragment
import pw.xiaohaozi.xadapter.node.NodeAdapter
import pw.xiaohaozi.xadapter.node.NodeProvider
import pw.xiaohaozi.xadapter.node.entity.ExpandedNodeEntity
import pw.xiaohaozi.xadapter.node.entity.NodeEntity
import pw.xiaohaozi.xadapter.node.ext.nodeAdapter
import pw.xiaohaozi.xadapter.smart.holder.XHolder


/**
 * 单布局
 */
class AddNodesFragment : VBFragment<FragmentNodeEditBinding>() {

    private val adapter = function()
    var index = 1
    override fun FragmentNodeEditBinding.initView() {
        binding.recycleView.layoutManager = LinearLayoutManager(requireContext())
        binding.recycleView.adapter = adapter
        adapter.refresh(dataList)

        binding.btnAddData.setOnClickListener {
            val list = mutableListOf(
                NodeInfo1("${++index}", "Node1", mutableListOf()),
                NodeInfo1("${++index}", "Node1", mutableListOf()),
            )
            adapter.addNode(list)
//            adapter.addNode(list, adapter.source?.size)
        }
    }


    fun function(): NodeAdapter<ViewBinding, NodeEntity<*, *>> {
        val adapter = nodeAdapter()
            .withType<ItemNodeEditBinding, NodeInfo1> { (holder, data) ->
                bindNode1(holder, data)
            }.setOnClickListener(R.id.btn_add) { holder, data, position, view ->
                addNode2(data, position) //点击一级菜单，添加二级菜单数据
            }.withType<ItemNodeEditBinding, NodeInfo2> { (holder, data) ->
                bindNode2(holder, data)
            }.toAdapter()
        return adapter
    }

    private fun bindNode1(holder: XHolder<ItemNodeEditBinding>, data: NodeInfo1) {
        holder.binding.apply {
            tvContent.text = data.no + "、" + data.text
            tvContent.setTypeface(Typeface.DEFAULT_BOLD)
            btnAdd.text = "添加子节点"
            btnAdd.isVisible = true
            btnEdit.isVisible = false
            btnDelete.isVisible = false
            ivArrow.isVisible = false
            ivArrow.rotation = if (data.isExpanded()) 0f else -90f
        }
    }

    private fun bindNode2(holder: XHolder<ItemNodeEditBinding>, data: NodeInfo2) {
        holder.binding.apply {
            tvContent.text = data.no + "、" + data.text
            tvContent.setTypeface(Typeface.DEFAULT)
            btnAdd.isVisible = false
            btnEdit.isVisible = false
            btnDelete.isVisible = false
            ivArrow.isVisible = false
        }
    }


    private fun NodeProvider<ViewBinding, NodeEntity<*, *>, ItemNodeEditBinding, NodeInfo1>.addNode2(
        data: NodeInfo1,
        position: Int
    ) {
        val size = data.getChildNodeEntityList().size
        val list = mutableListOf(
            NodeInfo2(" ${data.no}$size", "Node2"),
            NodeInfo2(" ${data.no}${size + 1}", "Node2"),
        )
        adapter.addChildNode(data, list)
//        adapter.addChildNode(data, list, 0)
        if (!data.isExpanded()) adapter.expand(position)
    }


    //提前预制两个根Node
    private val dataList = mutableListOf(
        NodeInfo1("0", "Node1", mutableListOf(NodeInfo2(" 00", "Node2"))),
        NodeInfo1("1", "Node1", mutableListOf(NodeInfo2(" 10", "Node2"))),
    )

    class NodeInfo1(val no: String, var text: String, private val childList: MutableList<NodeInfo2>) :
        NodeEntity<Unit, NodeInfo2>, ExpandedNodeEntity {
        override var xParentNodeEntity: Unit? = null
        override var xNodeGrade: Int? = null
        override var xIsExpanded: Boolean? = true

        override fun getChildNodeEntityList(): MutableList<NodeInfo2> {
            return childList
        }

        override fun toString(): String {
            return no
        }
    }

    class NodeInfo2(val no: String, val text: String) :
        NodeEntity<NodeInfo1, Unit>, ExpandedNodeEntity {
        override var xParentNodeEntity: NodeInfo1? = null
        override var xNodeGrade: Int? = null
        override var xIsExpanded: Boolean? = true

        override fun getChildNodeEntityList(): MutableList<Unit>? {
            return null
        }

        override fun toString(): String {
            return no
        }
    }


}


