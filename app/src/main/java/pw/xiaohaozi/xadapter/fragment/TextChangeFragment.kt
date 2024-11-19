package pw.xiaohaozi.xadapter.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import pw.xiaohaozi.xadapter.R
import pw.xiaohaozi.xadapter.databinding.FragmentRecyclerBinding
import pw.xiaohaozi.xadapter.databinding.ItemTextChingeBinding
import pw.xiaohaozi.xadapter.smart.adapter.SmartAdapter
import pw.xiaohaozi.xadapter.smart.ext.createAdapter


class TextChangeFragment : Fragment() {
    private lateinit var binding: FragmentRecyclerBinding

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
        val adapter = function()
        binding.recycleView.adapter = adapter
        adapter.reset(list)
    }


    /**
     * 文本变化监听
     */
    private fun function(): SmartAdapter<ItemTextChingeBinding, Exercises> {
        return createAdapter<ItemTextChingeBinding, Exercises> { (holder, data) ->
            //绑定题目
            holder.binding.tvContent.text = data.content
            //绑定答案
            holder.binding.etAnswer.setText(data.answer)

        }.setOnTextChange(R.id.et_answer) { holder, data, position, view, text ->
            data.answer = text.toString()
        }
    }


    private val list = arrayListOf(
        Exercises("1、河姆渡和半坡原始居民过上定居生活的最主要原因是（）"),

        Exercises("2、我国是世界最早种植蔬菜的国家。下列遗址的考古发现中可为这一论断提供证据的是（）"),

        Exercises("3、相传，造出衣裳、舟车、宫室等，为后世的衣食住行奠定基础的“人文始祖”是（）"),

        Exercises("4、在古希腊神话中，众神经常参加人间战争。传说中国古代也有一场“风伯御风、雨神行雨”的战役。在这场战役中，炎帝、黄帝部落大败蚩尤部落。该战役发生在（）"),

        Exercises("5、我们的祖先在与自然灾害抗争中留下了许多美丽的传说。右图反映的是（）"),

        Exercises("6、黄河流域是中华文明的发祥地之一。下列选项中最能体现该地区原始农耕文化成就是（）"),

        Exercises("7、西周为了巩固统治，实行“封建亲戚，以藩屏周”的政治制度。这种制度是（）"),

        Exercises("8、战国时期有这样一户人家：老大因作战有功获得爵位；老二在家勤于耕作。被免除徭役；老三则被国君派往小县为吏。这户人家最有可能生活在（）"),

        Exercises("9、春秋战国时期。新旧制度更替，社会大变革的根本原因是（）"),

        Exercises("10、某校七年级二班的同学在学习“商鞅变法”一课后表演了一出历史短剧。下列各项中错误的是（）"),

        Exercises("11、经典诵读已成为当今中国人传承历史文化的重要方式。《三字经》中“瀛秦氏，始兼并。传二世。楚汉争。高祖兴，汉业建”所包含的朝代顺序是（）"),

        Exercises("12、“地方推行郡县制。小篆成为规范字，焚书坑儒稿专制。”这一顺口溜反映的是（）实行的统治政策。"),

        Exercises("13、“惜秦皇汉武。略输文采，同宗宋祖，稍逊风骚……”毛泽东在《沁园春.雪》中提及了中国古代多位杰出君王。其中“汉武”最主要的功绩是（）"),

        )

    /**
     * 习题类
     */
    open class Exercises(
        val content: String,//题目内容
        var answer: String? = null//答案
    )

}

