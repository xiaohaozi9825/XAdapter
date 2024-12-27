package pw.xiaohaozi.xadapter.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import pw.xiaohaozi.xadapter.databinding.FragmentRecyclerBinding
import pw.xiaohaozi.xadapter.databinding.ItemNodeBinding
import pw.xiaohaozi.xadapter.node.NodeAdapter
import pw.xiaohaozi.xadapter.node.NodeEntity
import pw.xiaohaozi.xadapter.node.Sheng
import pw.xiaohaozi.xadapter.node.Shi
import pw.xiaohaozi.xadapter.node.Xian
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
        adapter.refresh(list)
        return binding.root
    }

    fun function(): NodeAdapter<ItemNodeBinding> {
        val adapter = NodeAdapter<ItemNodeBinding>()
        val shiProvider = object : XProvider<ItemNodeBinding, Shi>(adapter) {
            override fun onCreated(holder: XHolder<ItemNodeBinding>) {

            }

            override fun onBind(holder: XHolder<ItemNodeBinding>, data: Shi, position: Int) {
                holder.binding.tvContent.text = data.name
            }

            override fun isFixedViewType(): Boolean {
                return false
            }

        }
        val xianProvider = object : XProvider<ItemNodeBinding, Xian>(adapter) {
            override fun onCreated(holder: XHolder<ItemNodeBinding>) {

            }

            override fun onBind(holder: XHolder<ItemNodeBinding>, data: Xian, position: Int) {
                holder.binding.tvContent.text = data.name
            }

            override fun isFixedViewType(): Boolean {
                return false
            }

        }
        adapter + shiProvider + xianProvider
        return adapter
    }

    val guiLingChild = listOf(
        Xian("灵川县"),
        Xian("灌阳县"),
        Xian("恭城县"),
    )
    val nanNingChild = listOf(
        Xian("朝阳区"),
        Xian("武鸣县"),
        Xian("宾阳县"),
    )
    val guiLingShi = Shi("桂林市", guiLingChild)
    val nanNingShi = Shi("南宁市", nanNingChild)

    val guangXiSheng = Sheng("广西", listOf(nanNingShi, guiLingShi))

    val list = listOf(guiLingShi, nanNingShi)

}

