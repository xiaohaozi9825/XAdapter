package pw.xiaohaozi.xadapter.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import pw.xiaohaozi.xadapter.databinding.FragmentNodeEditBinding
import pw.xiaohaozi.xadapter.databinding.ItemNodeBinding
import pw.xiaohaozi.xadapter.databinding.ItemNodeEditBinding
import pw.xiaohaozi.xadapter.node.NodeAdapter
import pw.xiaohaozi.xadapter.node.NodeEntity
import pw.xiaohaozi.xadapter.smart.holder.XHolder
import pw.xiaohaozi.xadapter.smart.provider.XProvider


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


    fun function(): NodeAdapter<ItemNodeEditBinding> {
        val adapter = NodeAdapter<ItemNodeEditBinding>()
        val provider = object : XProvider<ItemNodeEditBinding, NodeInfo>(adapter) {
            override fun onCreated(holder: XHolder<ItemNodeEditBinding>) {
                holder.binding.root.setOnClickListener {
                    val position = holder.bindingAdapterPosition
                    val data = adapter.getData()[position] as NodeInfo
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

                holder.binding.btnDelete.setOnClickListener {
                    val position = holder.bindingAdapterPosition
                    val node = adapter.getData()[position] as NodeInfo
                    adapter.removeNode(node)
//                    adapter.removeNode(0,1)
//                    adapter.removeNodeAt(0)
//                    adapter.source?.take(2)?.let { it1 -> adapter.removeNode(it1) }//移除前两个
                }
            }

            override fun onBind(holder: XHolder<ItemNodeEditBinding>, data: NodeInfo, position: Int) {
                holder.binding.tvContent.text = data.no + "、" + data.text
            }

            override fun isFixedViewType(): Boolean {
                return false
            }

        }
        adapter + provider
        return adapter
    }

    data class NodeInfo(val no: String, val text: String) : NodeEntity<NodeInfo, NodeInfo> {
        private val childList: MutableList<NodeInfo> = mutableListOf()
        override fun getChildNodeEntityList(): MutableList<NodeInfo> {
            return childList
        }
    }
}


