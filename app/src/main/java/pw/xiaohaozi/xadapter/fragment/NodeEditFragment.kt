package pw.xiaohaozi.xadapter.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import pw.xiaohaozi.xadapter.R
import pw.xiaohaozi.xadapter.databinding.FragmentNodeEditBinding
import pw.xiaohaozi.xadapter.databinding.ItemNodeEditBinding
import pw.xiaohaozi.xadapter.node.NodeAdapter
import pw.xiaohaozi.xadapter.node.entity.NodeEntity
import pw.xiaohaozi.xadapter.node.ext.nodeAdapter


/**
 * 单布局
 */
class NodeEditFragment : Fragment() {
    private lateinit var binding: FragmentNodeEditBinding

    private val adapter = function()
    var index = 0
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNodeEditBinding.inflate(inflater)
        binding.recycleView.layoutManager = LinearLayoutManager(requireContext())
        binding.recycleView.adapter = adapter
        adapter.refresh(mutableListOf())
        binding.btnAddData.setOnClickListener {
            //添加一个节点
            adapter.addNode(NodeInfo("${++index}", "Node"), adapter.source?.size)
            //添加多个节点
//            val nodes = arrayListOf(NodeInfo("${++index}", "Node1"), NodeInfo("${++index}", "Node2"))
//            adapter.addNode(nodes, adapter.source?.size)


        }
        return binding.root
    }


    fun function(): NodeAdapter<ItemNodeEditBinding, NodeInfo> {
        val adapter = nodeAdapter<ItemNodeEditBinding, NodeInfo> { (holder, data) ->
            holder.binding.tvContent.text = data.no + "、" + data.text
        }
            .setOnClickListener { holder, data, position, view ->
                //添加单个节点
                adapter.addChildNode(
                    data,
                    NodeInfo("    ${data.no}.${data.getChildNodeEntityList().size + 1}", data.text),
                    data.getChildNodeEntityList().size
                )

//                    // 添加多个节点
//                    val nodes = arrayListOf(
//                        NodeInfo("    ${data.no}.${data.getChildNodeEntityList().size + 1}", "Child1"),
//                        NodeInfo("    ${data.no}.${data.getChildNodeEntityList().size + 2}", "Child2")
//                    )
//                    adapter.addChildNode(data, nodes, data.getChildNodeEntityList().size)
            }
            .setOnClickListener(R.id.btn_delete) { holder, data, position, view ->
                if (position == -1) return@setOnClickListener
                //移除指定位置节点
                adapter.removeNodePosition(position)

//                    val node = adapter.getData()[position] as NodeInfo
//移除指定节点测试，可以是子节点或非子节点
//                    adapter.removeNode(node)
//                    adapter.removeNode(0,1)
//                    adapter.removeNodeAt(0)
//                    adapter.source?.take(2)?.let { it1 -> adapter.removeNodeList(it1) }//移除前两个

                //移除子节点测试
//                    val parent = node.getParentNodeEntity() ?: return@setOnClickListener
//                    adapter.removeChildNode( parent as  NodeEntity<*, NodeEntity<*, *>>,node)
//                    adapter.removeChildNodeAt(parent as NodeEntity<*, NodeEntity<*, *>>, parent.getChildNodeEntityList().indexOf(node))
//                    adapter.removeChildNode( parent as  NodeEntity<*, NodeEntity<*, *>>,0,2)
//                    parent.getChildNodeEntityList().take(2).let { it1 ->   adapter.removeChildNodeList( parent as  NodeEntity<*, NodeEntity<*, *>>,it1) }//移除前两个


            }
        return adapter
    }

    data class NodeInfo(val no: String, val text: String) : NodeEntity<NodeInfo, NodeInfo> {
        private val childList: MutableList<NodeInfo> = mutableListOf()
        override var xParentNodeEntity: NodeInfo? = null
        override var xNodeGrade: Int? = null

        override fun getChildNodeEntityList(): MutableList<NodeInfo> {
            return childList
        }
    }
}


