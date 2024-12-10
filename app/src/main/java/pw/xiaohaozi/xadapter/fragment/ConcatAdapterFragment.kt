package pw.xiaohaozi.xadapter.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import pw.xiaohaozi.xadapter.R
import pw.xiaohaozi.xadapter.databinding.FragmentRecyclerBinding
import pw.xiaohaozi.xadapter.databinding.ItemHomeFooterBinding
import pw.xiaohaozi.xadapter.databinding.ItemHomeHeaderBinding
import pw.xiaohaozi.xadapter.databinding.ItemImageCardBinding
import pw.xiaohaozi.xadapter.databinding.ItemVerseBinding
import pw.xiaohaozi.xadapter.info.VerseInfo
import pw.xiaohaozi.xadapter.smart.ext.createAdapter
import pw.xiaohaozi.xadapter.smart.ext.toAdapter
import pw.xiaohaozi.xadapter.smart.ext.withType

/**
 * 与ConcatAdapter结合使用
 * 注意：与ConcatAdapter结合使用时，不支持特殊布局，如：头布局、脚布局、空布局、缺省页布局、分组布局等
 */
class ConcatAdapterFragment : Fragment() {
    private lateinit var binding: FragmentRecyclerBinding
    private val adapterHeader = createAdapter<ItemHomeHeaderBinding, String>(1, onItemId = { _ -> -1 }) { }
    private val adapterFooter = createAdapter<ItemHomeFooterBinding, String>(2, onItemId = { _ -> -2 }) { }
    private val adapterBody = createAdapter(onItemId = { position: Int -> position.toLong() }) { data, _ ->
        return@createAdapter if (data is VerseInfo) 3 else 4
    }
        .withType<ItemVerseBinding, VerseInfo>(itemType = 3) { (holder, data) ->
            holder.binding.tvContent.text = data.content
            holder.binding.tvAuthor.text = data.author
        }
        .withType<ItemImageCardBinding, Int>(itemType = 4) { (holder, data) ->
            holder.binding.image.setImageResource(data)
        }
        .toAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRecyclerBinding.inflate(inflater)
        binding.recycleView.layoutManager = LinearLayoutManager(requireContext())

        val config = ConcatAdapter.Config.Builder()
        //isolateViewTypes：如果使用false，需要明确指定各itemType值,且保证在所有adapt。默认true
        config.setIsolateViewTypes(false)


        //NO_STABLE_IDS
        //ISOLATED_STABLE_IDS
        //SHARED_STABLE_IDS
        //默认NO_STABLE_IDS，其他模式需要子adapter设置HasStableIds，注意各adapter中getItemId方法返回值
        adapterHeader.setHasStableIds(true)
        adapterBody.setHasStableIds(true)
        adapterFooter.setHasStableIds(true)
        config.setStableIdMode(ConcatAdapter.Config.StableIdMode.SHARED_STABLE_IDS)


        val build = config.build()
        val concatAdapter = ConcatAdapter(
            build,//缺省时使用默认值isolateViewTypes=true,StableIdMode=NO_STABLE_IDS
            adapterHeader,
            adapterBody,
            adapterFooter,
        )
        binding.recycleView.adapter = concatAdapter
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapterHeader.refresh(arrayListOf("1"))
        adapterBody.refresh(list)
        adapterFooter.refresh(arrayListOf("2"))
    }


    private val list = arrayListOf(
        R.mipmap.snow1,
        VerseInfo("1、何时杖尔看南雪，我与梅花两白头。", "——查辛香《清稗类钞·咏罗浮藤杖所作》"),
        VerseInfo("2、晚来天欲雪，能饮一杯无？", "——白居易《问刘十九》"),
        VerseInfo("3、昔去雪如花，今来花似雪。", "——范云《别诗》"),
        VerseInfo("4、柴门闻犬吠，风雪夜归人。", "——刘长卿《逢雪宿芙蓉山主人》"),
        VerseInfo("5、忽如一夜春风来，千树万树梨花开。", "——岑参《白雪歌送武判官归京》"),
        VerseInfo("6、浮生只合尊前老。雪满长安道。", "——舒亶《虞美人·寄公度》"),
        VerseInfo("7、孤舟蓑笠翁，独钓寒江雪。", "——柳宗元《江雪》"),
        VerseInfo("8、乱山残雪夜，孤烛异乡人。", "——崔涂《除夜 / 巴山道中除夜书怀 / 除夜有怀》"),
        VerseInfo("9、渺万里层云，千山暮雪，只影向谁去？", "——元好问《摸鱼儿·雁丘词 /迈陂塘》"),
        VerseInfo("10、有梅无雪不精神，有雪无诗俗了人。", "——卢梅坡《雪梅·其二》"),
        R.mipmap.snow2,
        VerseInfo("11、欲将轻骑逐，大雪满弓刀。", "——卢纶《和张仆射塞下曲·其三》"),
        VerseInfo("12、千里黄云白日曛，北风吹雁雪纷纷。", "——高适《别董大二首》"),
        VerseInfo("13、白雪却嫌春色晚，故穿庭树作飞花。", "——韩愈《春雪》"),
        VerseInfo("14、云横秦岭家何在？雪拥蓝关马不前。", "——韩愈《左迁至蓝关示侄孙湘》"),
        VerseInfo("15、窗含西岭千秋雪，门泊东吴万里船。", "——杜甫《绝句》"),
        VerseInfo("16、不知近水花先发，疑是经冬雪未销。", "——张谓《早梅》"),
        VerseInfo("17、五月天山雪，无花只有寒。", "——李白《塞下曲六首·其一》"),
        VerseInfo("18、惨惨柴门风雪夜，此时有子不如无。", "——黄景仁《别老母》"),
        VerseInfo("19、北风卷地白草折，胡天八月即飞雪。", "——岑参《白雪歌送武判官归京》"),
        R.mipmap.snow3,
        VerseInfo("20、欲渡黄河冰塞川，将登太行雪满山。", "——李白《行路难·其一》"),
        VerseInfo("21、今我来思，雨雪霏霏。", "——佚名《采薇》"),
        VerseInfo("22、雪消门外千山绿，花发江边二月晴。", "——欧阳修《春日西湖寄谢法曹歌》"),
        VerseInfo("23、梅雪争春未肯降，骚人阁笔费评章。", "——卢梅坡《雪梅·其一》"),
        VerseInfo("24、燕山雪花大如席，片片吹落轩辕台。", "——李白《北风行》"),
        VerseInfo("25、大雪压青松，青松挺且直。", "——陈毅《青松》"),

        )

}

