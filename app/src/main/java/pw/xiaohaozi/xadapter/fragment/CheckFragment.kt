package pw.xiaohaozi.xadapter.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import pw.xiaohaozi.xadapter.R
import pw.xiaohaozi.xadapter.databinding.FragmentRecyclerBinding
import pw.xiaohaozi.xadapter.databinding.ItemCheckCheckboxBinding
import pw.xiaohaozi.xadapter.databinding.ItemCheckRadiobuttonBinding
import pw.xiaohaozi.xadapter.smart.adapter.SmartAdapter
import pw.xiaohaozi.xadapter.smart.ext.createAdapter


class CheckFragment : Fragment() {
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
//        val adapter1 = function1()
//        binding.recycleView.adapter = adapter1
//        adapter1.reset(list1)

        val adapter2 = function2()
        binding.recycleView.adapter = adapter2
        adapter2.reset(list2)

    }


    /**
     * 单选
     */
    private fun function1(): SmartAdapter<ItemCheckRadiobuttonBinding, ExercisesRadio> {
        return createAdapter<ItemCheckRadiobuttonBinding, ExercisesRadio> { (holder, data, position) ->
            //绑定题目
            holder.binding.tvContent.text = data.content

            //绑定答案
            holder.binding.rbOptionA.text = data.option[0]
            holder.binding.rbOptionB.text = data.option[1]
            holder.binding.rbOptionC.text = data.option[2]
            holder.binding.rbOptionD.text = data.option[3]
            //绑定答案选择状态
            when (data.answer) {
                0 -> holder.binding.rbOptionA.isChecked = true
                1 -> holder.binding.rbOptionB.isChecked = true
                2 -> holder.binding.rbOptionC.isChecked = true
                3 -> holder.binding.rbOptionD.isChecked = true
                else -> {
                    //由于recyclerview item 存在复用机制，所以在没有radioButton选中的情况下，
                    //需要情况选中状态，否则展示的状态会出现不正常的情况
                    holder.binding.rgAnswer.clearCheck()
                }
            }
        }.setOnCheckedChangeListener(R.id.rb_option_a) { holder, data, position, view, isCheck ->
            if (isCheck) data.answer = 0
        }.setOnCheckedChangeListener(R.id.rb_option_b) { holder, data, position, view, isCheck ->
            if (isCheck) data.answer = 1
        }.setOnCheckedChangeListener(R.id.rb_option_c) { holder, data, position, view, isCheck ->
            if (isCheck) data.answer = 2
        }.setOnCheckedChangeListener(R.id.rb_option_d) { holder, data, position, view, isCheck ->
            if (isCheck) data.answer = 3
        }
    }

    /**
     * 多选
     */
    private fun function2(): SmartAdapter<ItemCheckCheckboxBinding, ExercisesCheck> {
        return createAdapter<ItemCheckCheckboxBinding, ExercisesCheck> { (holder, data, position) ->
            //绑定题目
            holder.binding.tvContent.text = data.content

            //绑定答案
            holder.binding.cbOptionA.text = data.option[0]
            holder.binding.cbOptionB.text = data.option[1]
            holder.binding.cbOptionC.text = data.option[2]
            holder.binding.cbOptionD.text = data.option[3]

            //绑定答案选择状态
            holder.binding.cbOptionA.isChecked = data.answers.contains(0)
            holder.binding.cbOptionB.isChecked = data.answers.contains(1)
            holder.binding.cbOptionC.isChecked = data.answers.contains(2)
            holder.binding.cbOptionD.isChecked = data.answers.contains(3)

        }.setOnCheckedChangeListener(R.id.cb_option_a) { holder, data, position, view, isCheck ->
            if (isCheck) data.answers.add(0)
            else data.answers.remove(0)
        }.setOnCheckedChangeListener(R.id.cb_option_b) { holder, data, position, view, isCheck ->
            if (isCheck) data.answers.add(1)
            else data.answers.remove(1)
        }.setOnCheckedChangeListener(R.id.cb_option_c) { holder, data, position, view, isCheck ->
            if (isCheck) data.answers.add(2)
            else data.answers.remove(2)
        }.setOnCheckedChangeListener(R.id.cb_option_d) { holder, data, position, view, isCheck ->
            if (isCheck) data.answers.add(3)
            else data.answers.remove(3)
        }
    }


    private val list1 = arrayListOf(
        ExercisesRadio(
            "1、河姆渡和半坡原始居民过上定居生活的最主要原因是（）",
            arrayListOf("A、农业生产的出现", " B、火的使用  ", "C、建造房屋 ", "D、使用陶器")
        ),

        ExercisesRadio(
            "2、我国是世界最早种植蔬菜的国家。下列遗址的考古发现中可为这一论断提供证据的是（）",
            arrayListOf("A、元谋人遗址", "B、北京人遗址  ", "C、河姆渡人遗址", "D、半坡人遗址")
        ),

        ExercisesRadio(
            "3、相传，造出衣裳、舟车、宫室等，为后世的衣食住行奠定基础的“人文始祖”是（）",
            arrayListOf("A、黄帝 ", "B、尧", "C、舜", "D、禹")
        ),

        ExercisesRadio(
            "4、在古希腊神话中，众神经常参加人间战争。传说中国古代也有一场“风伯御风、雨神行雨”的战役。在这场战役中，炎帝、黄帝部落大败蚩尤部落。该战役发生在（）",
            arrayListOf("A、牧野 ", "B、逐鹿", "C、长平", "D、城濮")
        ),

        ExercisesRadio(
            "5、我们的祖先在与自然灾害抗争中留下了许多美丽的传说。右图反映的是（）",
            arrayListOf("A、大禹治水", "B、精卫填海", "C、后羿射日", "D、夸父逐日")
        ),

        ExercisesRadio(
            "6、黄河流域是中华文明的发祥地之一。下列选项中最能体现该地区原始农耕文化成就是（）",
            arrayListOf("A、种植粟", "B、种植水稻", "C、人工取火", " D、住干栏式房子")
        ),

        ExercisesRadio(
            "7、西周为了巩固统治，实行“封建亲戚，以藩屏周”的政治制度。这种制度是（）",
            arrayListOf("A、禅让制", "B、世袭制", " C、分封制", "D、郡县制")
        ),

        ExercisesRadio(
            "8、战国时期有这样一户人家：老大因作战有功获得爵位；老二在家勤于耕作。被免除徭役；老三则被国君派往小县为吏。这户人家最有可能生活在（）",
            arrayListOf("A、齐国", "B、楚国", "C、燕国", "D、秦国")
        ),

        ExercisesRadio(
            "9、春秋战国时期。新旧制度更替，社会大变革的根本原因是（）",
            arrayListOf("A、战争频繁", " B、诸侯争霸", "C、百家争鸣 ", "D、社会生产力发展，铁制农具的广泛使用和牛耕的推广")
        ),

        ExercisesRadio(
            "10、某校七年级二班的同学在学习“商鞅变法”一课后表演了一出历史短剧。下列各项中错误的是（）",
            arrayListOf(
                "A、甲同学扮演秦孝公任命商鞅主持变法",
                "B、乙同学扮演生产粮食布帛多的人获得奖励",
                "C、丙同学扮演获得军功的大奖接受爵位",
                "D、扮演秦孝公的甲同学向全国颁旨：废除土地私有制"
            )
        ),

        ExercisesRadio(
            "11、经典诵读已成为当今中国人传承历史文化的重要方式。《三字经》中“瀛秦氏，始兼并。传二世。楚汉争。高祖兴，汉业建”所包含的朝代顺序是（）",
            arrayListOf("A、秦——西汉", "B、西汉——东汉  ", "C、东汉——三国", "D、三国——西晋")
        ),

        ExercisesRadio(
            "12、“地方推行郡县制。小篆成为规范字，焚书坑儒稿专制。”这一顺口溜反映的是（）实行的统治政策。",
            arrayListOf("A、秦始皇", "B、汉武帝", "C、唐太宗", "D、宋太祖")
        ),

        ExercisesRadio(
            "13、“惜秦皇汉武。略输文采，同宗宋祖，稍逊风骚……”毛泽东在《沁园春.雪》中提及了中国古代多位杰出君王。其中“汉武”最主要的功绩是（）",
            arrayListOf("A、创立了中央集权", " B、结束割据，实现国家统一 ", "C、稳固大一统局面", "D、统治期间出现盛世局面")
        ),

        )
    private val list2 = arrayListOf(
        ExercisesCheck(
            "1、河姆渡和半坡原始居民过上定居生活的最主要原因是（）",
            arrayListOf("A、农业生产的出现", " B、火的使用  ", "C、建造房屋 ", "D、使用陶器")
        ),

        ExercisesCheck(
            "2、我国是世界最早种植蔬菜的国家。下列遗址的考古发现中可为这一论断提供证据的是（）",
            arrayListOf("A、元谋人遗址", "B、北京人遗址  ", "C、河姆渡人遗址", "D、半坡人遗址")
        ),

        ExercisesCheck(
            "3、相传，造出衣裳、舟车、宫室等，为后世的衣食住行奠定基础的“人文始祖”是（）",
            arrayListOf("A、黄帝 ", "B、尧", "C、舜", "D、禹")
        ),

        ExercisesCheck(
            "4、在古希腊神话中，众神经常参加人间战争。传说中国古代也有一场“风伯御风、雨神行雨”的战役。在这场战役中，炎帝、黄帝部落大败蚩尤部落。该战役发生在（）",
            arrayListOf("A、牧野 ", "B、逐鹿", "C、长平", "D、城濮")
        ),

        ExercisesCheck(
            "5、我们的祖先在与自然灾害抗争中留下了许多美丽的传说。右图反映的是（）",
            arrayListOf("A、大禹治水", "B、精卫填海", "C、后羿射日", "D、夸父逐日")
        ),

        ExercisesCheck(
            "6、黄河流域是中华文明的发祥地之一。下列选项中最能体现该地区原始农耕文化成就是（）",
            arrayListOf("A、种植粟", "B、种植水稻", "C、人工取火", " D、住干栏式房子")
        ),

        ExercisesCheck(
            "7、西周为了巩固统治，实行“封建亲戚，以藩屏周”的政治制度。这种制度是（）",
            arrayListOf("A、禅让制", "B、世袭制", " C、分封制", "D、郡县制")
        ),

        ExercisesCheck(
            "8、战国时期有这样一户人家：老大因作战有功获得爵位；老二在家勤于耕作。被免除徭役；老三则被国君派往小县为吏。这户人家最有可能生活在（）",
            arrayListOf("A、齐国", "B、楚国", "C、燕国", "D、秦国")
        ),

        ExercisesCheck(
            "9、春秋战国时期。新旧制度更替，社会大变革的根本原因是（）",
            arrayListOf("A、战争频繁", " B、诸侯争霸", "C、百家争鸣 ", "D、社会生产力发展，铁制农具的广泛使用和牛耕的推广")
        ),

        ExercisesCheck(
            "10、某校七年级二班的同学在学习“商鞅变法”一课后表演了一出历史短剧。下列各项中错误的是（）",
            arrayListOf(
                "A、甲同学扮演秦孝公任命商鞅主持变法",
                "B、乙同学扮演生产粮食布帛多的人获得奖励",
                "C、丙同学扮演获得军功的大奖接受爵位",
                "D、扮演秦孝公的甲同学向全国颁旨：废除土地私有制"
            )
        ),

        ExercisesCheck(
            "11、经典诵读已成为当今中国人传承历史文化的重要方式。《三字经》中“瀛秦氏，始兼并。传二世。楚汉争。高祖兴，汉业建”所包含的朝代顺序是（）",
            arrayListOf("A、秦——西汉", "B、西汉——东汉  ", "C、东汉——三国", "D、三国——西晋")
        ),

        ExercisesCheck(
            "12、“地方推行郡县制。小篆成为规范字，焚书坑儒稿专制。”这一顺口溜反映的是（）实行的统治政策。",
            arrayListOf("A、秦始皇", "B、汉武帝", "C、唐太宗", "D、宋太祖")
        ),

        ExercisesCheck(
            "13、“惜秦皇汉武。略输文采，同宗宋祖，稍逊风骚……”毛泽东在《沁园春.雪》中提及了中国古代多位杰出君王。其中“汉武”最主要的功绩是（）",
            arrayListOf("A、创立了中央集权", " B、结束割据，实现国家统一 ", "C、稳固大一统局面", "D、统治期间出现盛世局面")
        ),

        )

    /**
     * 习题类
     */
    open class Exercises(
        val content: String,//题目内容
        val option: ArrayList<String>//选择项 A B C D
    )

    /**
     * 单选题
     */
    class ExercisesRadio(
        content: String,
        option: ArrayList<String>,
        var answer: Int? = null//已选项，值对应option中index
    ) : Exercises(content, option)

    /**
     * 多选题
     */
    class ExercisesCheck(
        content: String,
        option: ArrayList<String>,
        var answers: HashSet<Int> = hashSetOf()//已选项，值对应option中index
    ) : Exercises(content, option)

}

