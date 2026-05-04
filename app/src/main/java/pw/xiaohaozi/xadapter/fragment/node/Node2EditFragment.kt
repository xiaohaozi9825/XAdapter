package pw.xiaohaozi.xadapter.fragment.node

import android.graphics.Typeface
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.ViewBinding
import pw.xiaohaozi.xadapter.R
import pw.xiaohaozi.xadapter.databinding.FragmentNodeEditBinding
import pw.xiaohaozi.xadapter.databinding.ItemHomeHeaderBinding
import pw.xiaohaozi.xadapter.databinding.ItemNodeEditBinding
import pw.xiaohaozi.xadapter.dialog.InputDialog
import pw.xiaohaozi.xadapter.fragment.VBFragment
import pw.xiaohaozi.xadapter.node.NodeAdapter
import pw.xiaohaozi.xadapter.node.NodeProvider
import pw.xiaohaozi.xadapter.node.entity.ExpandedNodeEntity
import pw.xiaohaozi.xadapter.node.entity.NodeEntity
import pw.xiaohaozi.xadapter.node.ext.nodeAdapter
import pw.xiaohaozi.xadapter.node.ext.swipeDelete
import pw.xiaohaozi.xadapter.smart.holder.XHolder


/**
 * 单布局
 */
class Node2EditFragment : VBFragment<FragmentNodeEditBinding>() {

    private val adapter = function()
    var index = 1

    override fun FragmentNodeEditBinding.initView() {
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
    }


    fun function(): NodeAdapter<ViewBinding, NodeEntity<*, *>> {
        val adapter = nodeAdapter()
            .withType<ItemNodeEditBinding, NodeInfo1> { (holder, data) ->
                bindNode1(holder, data)
            }.setOnClickListener(R.id.iv_arrow) { holder, data, position, view ->
                expandOrCollapseNode1(data, position)
            }.setOnClickListener(R.id.btn_add) { holder, data, position, view ->
                addNode2(data, position) //点击一级菜单，添加二级菜单数据
            }.setOnClickListener(R.id.btn_edit) { holder, data, position, view ->
                editNode1(data)
            }.withType<ItemNodeEditBinding, NodeInfo2> { (holder, data) ->
                bindNode2(holder, data)
            }.setOnClickListener(R.id.iv_arrow) { holder, data, position, view ->
                expandOrCollapseNode2(data, position)
            }.setOnClickListener(R.id.btn_add) { holder, data, position, view ->
                addNode3(data, position)
            }.setOnClickListener(R.id.btn_edit) { holder, data, position, view ->
                editNode2(data)
            }.withType<ItemNodeEditBinding, NodeInfo3> { (holder, data) ->
                bindNode3(holder, data)
            }.setOnClickListener(R.id.btn_edit) { holder, data, position, view ->
                editNode3(data)
            }
            .toAdapter()
            .setOnClickListener(R.id.btn_delete) { holder, data, position, view ->
                removeNodeAtPosition(position)//设置全局点击事件
            }
            .addHeader<ItemHomeHeaderBinding>()
            .swipeDelete()
        return adapter
    }

    private fun removeNodeAtPosition(position: Int) {
        if (position == -1) return
        //移除指定位置节点
        adapter.removeNodePosition(position)
    }

    private fun NodeProvider<ViewBinding, NodeEntity<*, *>, ItemNodeEditBinding, NodeInfo3>.editNode3(
        data: NodeInfo3
    ) {
        InputDialog(requireActivity())
            .setTitle("编辑内容")
            .setMsg(data.text)
            .onConfirm { _, content: String ->
                //①数据属性变化
                //                        data.text = content
                //                        getNodeAdapter().updateNode(data)
                //②数据引用变化
                val newNode = NodeInfo3(data.no, content)
                adapter.updateNode(data, newNode)
            }
            .onCancel {}
            .show()
    }

    private fun bindNode3(
        holder: XHolder<ItemNodeEditBinding>,
        data: NodeInfo3
    ) {
        holder.binding
            .apply { tvContent.text = data.no + "、" + data.text }
            .apply { tvContent.setTypeface(null, Typeface.ITALIC) }
            .apply { btnAdd.isVisible = false }
            //                    .apply { btnEdit.isVisible = true }
            .apply { ivArrow.isInvisible = true }
    }

    private fun NodeProvider<ViewBinding, NodeEntity<*, *>, ItemNodeEditBinding, NodeInfo2>.editNode2(
        data: NodeInfo2
    ) {
        InputDialog(requireActivity())
            .setTitle("编辑内容")
            .setMsg(data.text)
            .onConfirm { _, content: String ->
                //①数据属性变化
                //                        data.text = content
                //                        getNodeAdapter().updateNode(data)
                //②数据引用变化
                val newNode = NodeInfo2(data.no, content, mutableListOf(NodeInfo3("33", "三级菜单")))
                adapter.replaceNode(data, newNode)
            }
            .onCancel {}
            .show()
    }

    private fun NodeProvider<ViewBinding, NodeEntity<*, *>, ItemNodeEditBinding, NodeInfo2>.addNode3(
        data: NodeInfo2,
        position: Int
    ) {
        val size = data.getChildNodeEntityList().size
        adapter.addChildNode(
            data,
            NodeInfo3("  ${data.no}${size}", "Node3"),
            //size//在末尾添加数据，该参数可不传
        )
        if (!data.isExpanded()) adapter.expand(position)
    }

    private fun NodeProvider<ViewBinding, NodeEntity<*, *>, ItemNodeEditBinding, NodeInfo2>.expandOrCollapseNode2(
        data: NodeInfo2,
        position: Int
    ) {
        if (data.isExpanded()) adapter.collapse(position, true)
        else adapter.expand(position)
    }

    private fun bindNode2(
        holder: XHolder<ItemNodeEditBinding>,
        data: NodeInfo2
    ) {
        holder.binding
            .apply { tvContent.text = data.no + "、" + data.text }
            .apply { tvContent.setTypeface(Typeface.DEFAULT) }
            .apply { btnAdd.isVisible = true }
            //                    .apply { btnEdit.isVisible = false }
            .apply { ivArrow.isInvisible = false }
            .apply { ivArrow.rotation = if (data.isExpanded()) 0f else -90f }
    }

    private fun NodeProvider<ViewBinding, NodeEntity<*, *>, ItemNodeEditBinding, NodeInfo1>.editNode1(
        data: NodeInfo1
    ) {
        InputDialog(requireActivity())
            .setTitle("编辑内容")
            .setMsg(data.text)
            .onConfirm { _, content: String ->
                //①数据属性变化
                data.text = content
                adapter.updateNode(data)
                //②数据引用变化
                //                        val newNode = NodeInfo1(data.no, content, mutableListOf(NodeInfo2("22","二级菜单", mutableListOf())))
                //                        adapter.replaceNode(data, newNode)
            }
            .onCancel {}
            .show()
    }

    private fun NodeProvider<ViewBinding, NodeEntity<*, *>, ItemNodeEditBinding, NodeInfo1>.addNode2(
        data: NodeInfo1,
        position: Int
    ) {
        val size = data.getChildNodeEntityList().size
        adapter.addChildNode(data, NodeInfo2(" ${data.no}$size", "Node2", mutableListOf()))
        if (!data.isExpanded()) adapter.expand(position)
    }

    private fun bindNode1(
        holder: XHolder<ItemNodeEditBinding>,
        data: NodeInfo1
    ) {
        holder.binding.apply {
            tvContent.text = data.no + "、" + data.text
            tvContent.setTypeface(Typeface.DEFAULT_BOLD)
            btnAdd.isVisible = true
            ivArrow.isInvisible = false
            ivArrow.rotation = if (data.isExpanded()) 0f else -90f
        }
    }

    private fun NodeProvider<ViewBinding, NodeEntity<*, *>, ItemNodeEditBinding, NodeInfo1>.expandOrCollapseNode1(
        data: NodeInfo1,
        position: Int
    ) {
        if (data.isExpanded()) adapter.collapse(position)
        else adapter.expand(position, true)
    }

    val dataList = mutableListOf(
        NodeInfo1("0", "Node1", mutableListOf(NodeInfo2(" 00", "Node2", mutableListOf(NodeInfo3("  000", "Node3"))))),
        NodeInfo1("1", "Node1", mutableListOf(NodeInfo2(" 10", "Node2", mutableListOf(NodeInfo3("  100", "Node3"))))),
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

    class NodeInfo2(val no: String, val text: String, private val childList: MutableList<NodeInfo3>) :
        NodeEntity<NodeInfo1, NodeInfo3>, ExpandedNodeEntity {
        override var xParentNodeEntity: NodeInfo1? = null
        override var xNodeGrade: Int? = null
        override var xIsExpanded: Boolean? = true

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


