package pw.xiaohaozi.xadapter.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.ViewBinding
import coil.load
import pw.xiaohaozi.xadapter.R
import pw.xiaohaozi.xadapter.databinding.FragmentRecyclerBinding
import pw.xiaohaozi.xadapter.databinding.ItemHomeFooterBinding
import pw.xiaohaozi.xadapter.databinding.ItemHomeHeaderBinding
import pw.xiaohaozi.xadapter.databinding.ItemImageCardBinding
import pw.xiaohaozi.xadapter.databinding.ItemVerseBinding
import pw.xiaohaozi.xadapter.info.VerseInfo
import pw.xiaohaozi.xadapter.smart.adapter.SmartAdapter
import pw.xiaohaozi.xadapter.smart.ext.createAdapter
import pw.xiaohaozi.xadapter.smart.ext.dragSort
import pw.xiaohaozi.xadapter.smart.ext.swipeDelete
import pw.xiaohaozi.xadapter.smart.ext.toAdapter
import pw.xiaohaozi.xadapter.smart.ext.withType

/**
 * 单布局
 */
class SwipeDeleteFragment : Fragment() {
    private lateinit var binding: FragmentRecyclerBinding

    private val adapter = function1()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRecyclerBinding.inflate(inflater)
        binding.recycleView.layoutManager = LinearLayoutManager(requireContext())
        binding.recycleView.adapter = adapter
        adapter.reset(list)
        return binding.root
    }

    /**
     * 方法1
     * 使用XAdapter拓展方法创建
     */
    private fun function1(): SmartAdapter<ViewBinding, Any?> {
        return createAdapter()
            .withType<ItemVerseBinding, VerseInfo> { (holder, data) ->
                holder.binding.tvContent.text = data.content
                holder.binding.tvAuthor.text = data.author
            }
            .swipeDelete()//只对当前类型执行侧滑操作
            .withType<ItemImageCardBinding, Int> {
                it.holder.binding.image.load(it.data)
            }
            .toAdapter()
            .addHeader<ItemHomeHeaderBinding> { }
            .addFooter<ItemHomeFooterBinding> { }
//            .swipeDelete()//对整个adapter执行侧滑操作

    }

    private val list = arrayListOf(
        VerseInfo("1、何时杖尔看南雪，我与梅花两白头。", "——查辛香《清稗类钞·咏罗浮藤杖所作》"),
        VerseInfo("2、晚来天欲雪，能饮一杯无？", "——白居易《问刘十九》"),
        VerseInfo("3、昔去雪如花，今来花似雪。", "——范云《别诗》"),
        R.mipmap.t2,
        VerseInfo("4、柴门闻犬吠，风雪夜归人。", "——刘长卿《逢雪宿芙蓉山主人》"),
        VerseInfo("5、忽如一夜春风来，千树万树梨花开。", "——岑参《白雪歌送武判官归京》"),
        VerseInfo("6、浮生只合尊前老。雪满长安道。", "——舒亶《虞美人·寄公度》"),
        VerseInfo("7、孤舟蓑笠翁，独钓寒江雪。", "——柳宗元《江雪》"),
        VerseInfo("8、乱山残雪夜，孤烛异乡人。", "——崔涂《除夜 / 巴山道中除夜书怀 / 除夜有怀》"),
        VerseInfo("9、渺万里层云，千山暮雪，只影向谁去？", "——元好问《摸鱼儿·雁丘词 /迈陂塘》"),
        VerseInfo("10、有梅无雪不精神，有雪无诗俗了人。", "——卢梅坡《雪梅·其二》"),
        VerseInfo("11、欲将轻骑逐，大雪满弓刀。", "——卢纶《和张仆射塞下曲·其三》"),
        VerseInfo("12、千里黄云白日曛，北风吹雁雪纷纷。", "——高适《别董大二首》"),
        VerseInfo("13、白雪却嫌春色晚，故穿庭树作飞花。", "——韩愈《春雪》"),
        VerseInfo("14、云横秦岭家何在？雪拥蓝关马不前。", "——韩愈《左迁至蓝关示侄孙湘》"),
        VerseInfo("15、窗含西岭千秋雪，门泊东吴万里船。", "——杜甫《绝句》"),
        VerseInfo("16、不知近水花先发，疑是经冬雪未销。", "——张谓《早梅》"),
        VerseInfo("17、五月天山雪，无花只有寒。", "——李白《塞下曲六首·其一》"),
        VerseInfo("18、惨惨柴门风雪夜，此时有子不如无。", "——黄景仁《别老母》"),
        VerseInfo("19、北风卷地白草折，胡天八月即飞雪。", "——岑参《白雪歌送武判官归京》"),
        VerseInfo("20、欲渡黄河冰塞川，将登太行雪满山。", "——李白《行路难·其一》"),
        VerseInfo("21、今我来思，雨雪霏霏。", "——佚名《采薇》"),
        VerseInfo("22、雪消门外千山绿，花发江边二月晴。", "——欧阳修《春日西湖寄谢法曹歌》"),
        VerseInfo("23、梅雪争春未肯降，骚人阁笔费评章。", "——卢梅坡《雪梅·其一》"),
        VerseInfo("24、燕山雪花大如席，片片吹落轩辕台。", "——李白《北风行》"),
        VerseInfo("25、大雪压青松，青松挺且直。", "——陈毅《青松》"),
        VerseInfo("26、终南阴岭秀，积雪浮云端。", "——祖咏《终南望余雪》"),
        VerseInfo("27、夜深知雪重，时闻折竹声。", "——白居易《夜雪》"),
        VerseInfo("28、雪纷纷，掩重门，不由人不断魂，瘦损江梅韵。", "——关汉卿《大德歌·冬》"),
        VerseInfo("29、草枯鹰眼疾，雪尽马蹄轻。", "——王维《观猎》"),
        VerseInfo("30、梅须逊雪三分白，雪却输梅一段香。", "——卢梅坡《雪梅·其一》"),
        VerseInfo("31、六出飞花入户时，坐看青竹变琼枝。", "——高骈《对雪》"),
        VerseInfo("32、烟霏霏。雪霏霏。雪向梅花枝上堆。", "——吴淑姬《长相思令·烟霏霏》"),
        VerseInfo("33、天将暮，雪乱舞，半梅花半飘柳絮。", "——马致远《寿阳曲·江天暮雪》"),
        VerseInfo("34、遥知独听灯前雨，转忆同看雪后山。", "——纳兰性德《于中好·送梁汾南还为题小影》"),
        VerseInfo("35、云和积雪苍山晚，烟伴残阳绿树昏。", "——周朴《春日秦国怀古》"),
        VerseInfo("36、三春白雪归青冢，万里黄河绕黑山。", "——柳中庸《征人怨 / 征怨》"),
        VerseInfo("37、晨起开门雪满山，雪晴云淡日光寒。", "——郑燮《山中雪后》"),
        VerseInfo("38、春雪满空来，触处似花开。", "——赵嘏《喜张沨及第》"),
        VerseInfo("39、天山雪后海风寒，横笛偏吹行路难。", "——李益《从军北征》"),
        VerseInfo("40、溪深难受雪，山冻不流云。", "——洪升《雪望》"),
        VerseInfo("41、前村深雪里，昨夜一枝开。", "——齐己《早梅》"),
        VerseInfo("42、水晶帘外娟娟月，梨花枝上层层雪。", "——杨基《菩萨蛮·水晶帘外娟娟月》"),
        VerseInfo("43、岁暮阴阳催短景，天涯霜雪霁寒宵。", "——杜甫《阁夜》"),
        VerseInfo("44、残雪压枝犹有桔，冻雷惊笋欲抽芽。", "——欧阳修《戏答元珍》"),
        VerseInfo("45、雪月最相宜，梅雪都清绝。", "——张孝祥《卜算子·雪月最相宜》"),
        VerseInfo("46、溪深古雪在，石断寒泉流。", "——李白《寻高凤石门山中元丹丘》"),
        VerseInfo("47、风起。雪飞炎海变清凉。", "——苏轼《定风波·南海归赠王定国侍人寓娘》"),
        VerseInfo("48、吹灯窗更明，月照一天雪。", "——袁枚《十二月十五夜》"),
        VerseInfo("49、雪里已知春信至。寒梅点缀琼枝腻。", "——李清照《渔家傲·雪里已知春信至》"),
        VerseInfo("50、江涵雁影梅花瘦，四无尘、雪飞云起，夜窗如昼。", "——卢祖皋《贺新郎·挽住风前柳》"),
        VerseInfo("51、数萼初含雪，孤标画本难。", "——崔道融《梅花》"),
        VerseInfo("52、朔风吹散三更雪，倩魂犹恋桃花月。", "——纳兰性德《菩萨蛮·朔风吹散三更雪》"),
        VerseInfo("53、去年相送，馀杭门外，飞雪似杨花。", "——苏轼《少年游·润州作》"),
        VerseInfo("54、一声画角谯门，丰庭新月黄昏，雪里山前水滨。", "——白朴《天净沙·冬》"),
        VerseInfo("55、夜雪初霁，荠麦弥望。", "——姜夔《扬州慢·淮左名都》"),
        VerseInfo("56、隔牖风惊竹，开门雪满山。", "——王维《冬晚对雪忆胡居士家》"),
        VerseInfo("57、纷纷暮雪下辕门，风掣红旗冻不翻。", "——岑参《白雪歌送武判官归京》"),
        VerseInfo("58、冬宜密雪，有碎玉声。", "——王禹偁《黄冈竹楼记》"),
        VerseInfo("59、林下春晴风渐和，高崖残雪已无多。", "——王守仁《春晴》"),
        VerseInfo("60、横笛闻声不见人，红旗直上天山雪。", "——陈羽《从军行》"),
        VerseInfo("61、剑河风急雪片阔，沙口石冻马蹄脱。", "——岑参《轮台歌奉送封大夫出师西征》"),
        VerseInfo("62、雪暗凋旗画，风多杂鼓声。", "——杨炯《从军行》"),
        VerseInfo("63、乱石穿空，惊涛拍岸，卷起千堆雪。", "——苏轼《念奴娇·赤壁怀古》"),
        VerseInfo("64、余拏一小舟，拥毳衣炉火，独往湖心亭看雪。", "——张岱《湖心亭看雪》"),
        VerseInfo("65、年年雪里。常插梅花醉。", "——李清照《清平乐·年年雪里》"),
        VerseInfo("66、燕支长寒雪作花，蛾眉憔悴没胡沙。", "——李白《王昭君二首》"),
        VerseInfo("67、日暮诗成天又雪，与梅并作十分春。", "——卢梅坡《雪梅·其二》"),
        VerseInfo("68、野云万里无城郭，雨雪纷纷连大漠。", "——李颀《古从军行》"),
        VerseInfo("69、云和积雪苍山晚，烟伴残阳绿树昏。", "——周朴《春日秦国怀古》"),
        VerseInfo("70、西山白雪三城戍，南浦清江万里桥。", "——杜甫《野望》"),
        VerseInfo("71、最爱东山晴后雪，软红光里涌银山。", "——杨万里《最爱东山晴后雪》"),
        VerseInfo("72、北风吹雪四更初，嘉瑞天教及岁除。", "——陆游《除夜雪》"),
        VerseInfo("73、雪粉华，舞梨花，再不见烟村四五家。", "——关汉卿《大德歌·冬景》"),
        VerseInfo("74、乱云低薄暮，急雪舞回风。", "——杜甫《对雪》"),
        VerseInfo("75、路出寒云外，人归暮雪时。", "——卢纶《李端公 / 送李端》"),
        VerseInfo("76、城里夕阳城外雪，相将十里异阴晴。", "——王守仁《次韵陆佥宪元日春晴》"),
        VerseInfo("77、春还草阁梅先动，月满虚庭雪未消。", "——王守仁《元夕二首》"),
        VerseInfo("78、轮台东门送君去，去时雪满天山路。", "——岑参《白雪歌送武判官归京》"),
        VerseInfo("79、飞雪带春风，裴回乱绕空。", "——刘方平《春雪》"),
        VerseInfo("80、地白风色寒，雪花大如手。", "——李白《嘲王历阳不肯饮酒》"),
        VerseInfo("81、三日柴门拥不开，阶平庭满白皑皑。", "——韩愈《酬王二十舍人雪中见寄》"),
        VerseInfo("82、昨夜醉眠西浦月。今宵独钓南溪雪。", "——洪适《渔家傲引·子月水寒风又烈》"),
        VerseInfo("83、瀚海百重波，阴山千里雪。", "——李世民《饮马长城窟行》"),
        VerseInfo("84、夜来城外一尺雪，晓驾炭车辗冰辙。", "——白居易《卖炭翁》"),
        VerseInfo("85、垂钓板桥东，雪压蓑衣冷。", "——释敬安《题寒江钓雪图》"),
        VerseInfo("86、梅花大庾岭头发，柳絮章台街里飞。", "——李商隐《对雪二首》"),
        VerseInfo("87、梅花散彩向空山，雪花随意穿帘幕。", "——王旭《踏莎行·雪中看梅花》"),
        VerseInfo("88、天山三丈雪，岂是远行时。", "——李白《独不见》"),
        VerseInfo("89、昆仑之高有积雪，蓬莱之远常遗寒。", "——王令《暑旱苦热》"),
        VerseInfo("90、闻道梅花坼晓风，雪堆遍满四山中。", "——陆游《梅花绝句》"),
        VerseInfo("91、林表明霁色，城中增暮寒。", "——祖咏《终南望余雪》"),
        VerseInfo("92、烟尘犯雪岭，鼓角动江城。", "——杜甫《岁暮》"),
        VerseInfo("93、天仙碧玉琼瑶，点点扬花，片片鹅毛。", "——薛昂夫《蟾宫曲·雪》"),
        VerseInfo("94、罗襟湿未干，又是凄凉雪。", "——张淑芳《满路花·冬》"),
        VerseInfo("95、看雪飞、苹底芦梢，未如鬓白。", "——吴文英《瑞鹤仙·秋感》"),
        VerseInfo("96、今年春浅腊侵年。冰雪破春妍。", "——苏轼《一丛花·今年春浅侵年》"),
        VerseInfo("97、不知庭霰今朝落，疑是林花昨夜开。", "——宋之问《苑中遇雪应制》"),
        VerseInfo("98、凄凄岁暮风，翳翳经日雪。", "——陶渊明《癸卯岁十二月中作与从弟敬远》"),
        VerseInfo("99、侵陵雪色还萱草，漏泄春光有柳条。", "——杜甫《腊日》"),
        VerseInfo("100、将军玉帐貂鼠衣，手持酒杯看雪飞。", "——刘基《北风行》"),
    )
}

