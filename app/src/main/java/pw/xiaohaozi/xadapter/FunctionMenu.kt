package pw.xiaohaozi.xadapter

import pw.xiaohaozi.xadapter.fragment.node.AddNodeFragment
import pw.xiaohaozi.xadapter.fragment.node.AddNodesFragment
import pw.xiaohaozi.xadapter.fragment.smart.CheckFragment
import pw.xiaohaozi.xadapter.fragment.smart.ClickFragment
import pw.xiaohaozi.xadapter.fragment.smart.ConcatAdapterFragment
import pw.xiaohaozi.xadapter.fragment.smart.CoroutineScopeFragment
import pw.xiaohaozi.xadapter.fragment.node.CreateMultipleNodeFragment
import pw.xiaohaozi.xadapter.fragment.node.CreateNodeFragment
import pw.xiaohaozi.xadapter.fragment.node.EditNodeFragment
import pw.xiaohaozi.xadapter.fragment.example.FriendFragment
import pw.xiaohaozi.xadapter.fragment.example.GoodsFragment
import pw.xiaohaozi.xadapter.fragment.example.ImageSelectedFragment
import pw.xiaohaozi.xadapter.fragment.example.MultipleCategoryFragment
import pw.xiaohaozi.xadapter.fragment.smart.DataDifferFragment
import pw.xiaohaozi.xadapter.fragment.smart.DataOperationFragment
import pw.xiaohaozi.xadapter.fragment.smart.DragSortFragment
import pw.xiaohaozi.xadapter.fragment.smart.GroupFragment
import pw.xiaohaozi.xadapter.fragment.smart.SelectFragment
import pw.xiaohaozi.xadapter.fragment.smart.LongClickFragment
import pw.xiaohaozi.xadapter.fragment.smart.MultipleFragment
import pw.xiaohaozi.xadapter.fragment.node.Node2EditFragment
import pw.xiaohaozi.xadapter.fragment.node.NodeEditFragment
import pw.xiaohaozi.xadapter.fragment.node.NodeFragment
import pw.xiaohaozi.xadapter.fragment.node.RemoveAtNodeFragment
import pw.xiaohaozi.xadapter.fragment.node.RemoveListNodeFragment
import pw.xiaohaozi.xadapter.fragment.node.RemoveMultipleNodeFragment
import pw.xiaohaozi.xadapter.fragment.node.RemoveNodeFragment
import pw.xiaohaozi.xadapter.fragment.node.RemovePositionNodeFragment
import pw.xiaohaozi.xadapter.fragment.node.ReplaceNodeFragment
import pw.xiaohaozi.xadapter.fragment.smart.SingleFragment
import pw.xiaohaozi.xadapter.fragment.smart.SpecialLayoutFragment
import pw.xiaohaozi.xadapter.fragment.smart.SwipeDeleteFragment
import pw.xiaohaozi.xadapter.fragment.smart.SwipeMenuFragment
import pw.xiaohaozi.xadapter.fragment.smart.TextChangeFragment
import pw.xiaohaozi.xadapter.info.HomeInfo

//smart模块
val smartMenuList = arrayListOf(
    "Adapter创建",
    HomeInfo("创建单布局", "创建只有一种布局方式的Adapter", R.mipmap.icon_smart_single, SingleFragment::class.java),
    HomeInfo("创建多布局", "创建多种布局方式的Adapter", R.mipmap.icon_node_multiple, MultipleFragment::class.java),
    "Item事件监听",
    HomeInfo("点击事件", "item点击事件", R.mipmap.icon_smart_click, ClickFragment::class.java),
    HomeInfo("长按事件", "item长按事件", R.mipmap.icon_smart_long_click, LongClickFragment::class.java),
    HomeInfo("选中事件", "监听单选框、复选框、开关等组件的选中状态", R.mipmap.icon_smart_check, CheckFragment::class.java),
    HomeInfo("文本变化", "监听文本输入框内容变化", R.mipmap.icon_smart_text_change, TextChangeFragment::class.java),
    "选择操作",
    HomeInfo("选择操作", "单选、多选、全选等操作", R.mipmap.icon_smart_select, SelectFragment::class.java),
    "特殊布局",
    HomeInfo("特殊布局", "头布局，脚布局，空布局、缺省页布局", R.mipmap.icon_smart_special, SpecialLayoutFragment::class.java),
    HomeInfo("分组布局", "使布局填充整行", R.mipmap.icon_smart_group, GroupFragment::class.java),
    "拖拽与侧滑",
    HomeInfo("侧滑删除", "侧滑删除Item", R.mipmap.icon_smart_swipe_delete, SwipeDeleteFragment::class.java),
    HomeInfo("拖拽排序", "长按拖拽排序", R.mipmap.icon_smart_draw_sort, DragSortFragment::class.java),
    HomeInfo("侧滑菜单", "类似QQ好友列表侧滑效果", R.mipmap.icon_smart_swipe_menu, SwipeMenuFragment::class.java),
    "数据操作",
    HomeInfo("常规操作", "列表数据添加、删除、修改操作", R.mipmap.icon_smart_data_operation, DataOperationFragment::class.java),
    HomeInfo("Differ", "使用Differ更新数据", R.mipmap.icon_smart_data_differ, DataDifferFragment::class.java),
    "其他",
    HomeInfo("协程作用域", "XAdapter对协成的支持", R.mipmap.icon_smart_coroutine, CoroutineScopeFragment::class.java),
    HomeInfo("ConcatAdapter", "结合ConcatAdapter使用", R.mipmap.icon_smart_concat, ConcatAdapterFragment::class.java),
)

//node模块
val nodeMenuList = arrayListOf(
    "Node测试",
    HomeInfo("node测试", "", R.mipmap.ic_launcher, NodeFragment::class.java),
    HomeInfo("node编辑", "", R.mipmap.ic_launcher, NodeEditFragment::class.java),
    HomeInfo("node编辑2", "", R.mipmap.ic_launcher, Node2EditFragment::class.java),
    "Node创建",
    HomeInfo("单类型创建", "单类型Node创建", R.mipmap.icon_smart_single, CreateNodeFragment::class.java),
    HomeInfo("多类型创建", "多类型Node创建", R.mipmap.icon_node_multiple, CreateMultipleNodeFragment::class.java),
    "Node展开与折叠",
    HomeInfo("展开与折叠", "Node展开与折叠", R.mipmap.icon_smart_multiple),

    "Node数据操作",
    HomeInfo("添加一个节点", "", R.mipmap.icon_node_add, AddNodeFragment::class.java),
    HomeInfo("添加多个节点", "", R.mipmap.icon_node_add, AddNodesFragment::class.java),

    //updateNode(node: D, payload: Any? = null)、updateNode(oldNode: D, newNode: D, payload: Any? = null)、updateChildNode(parent: D, oldNode: D, newNode: D, payload: Any? = null)
    HomeInfo("修改节点", "修改指定节点内容", R.mipmap.icon_node_edit, EditNodeFragment::class.java),

    //replaceNode(oldNode: D, newNode: D)
    HomeInfo("替换Node", "替换节点以及子节点", R.mipmap.icon_node_replace, ReplaceNodeFragment::class.java),

    //removeNode(node: D) 、removeChildNode(parent: D, node: D)
    HomeInfo("删除一个节点", "删除一个根节点或子节点", R.mipmap.icon_node_delete, RemoveNodeFragment::class.java),

    //removeNodeAt(index: Int)、removeChildNodeAt(parent: D, index: Int)
    HomeInfo("按索引删除节点", "", R.mipmap.icon_node_delete, RemoveAtNodeFragment::class.java),

    //removeNode(start: Int, count: Int)、removeChildNode(parent: D, start: Int, count: Int)
    HomeInfo("删除多个连续节点", "", R.mipmap.icon_node_delete, RemoveMultipleNodeFragment::class.java),

    //removeNodeList(nodes: List<D>)、 removeChildNodeList(parent: D, nodes: List<D>)
    HomeInfo("删除多个不连续节点", "", R.mipmap.icon_node_delete, RemoveListNodeFragment::class.java),

    //removeNodePosition(adapterPosition: Int)、
    HomeInfo("按列表所在位置删除", "", R.mipmap.icon_node_delete, RemovePositionNodeFragment::class.java),
)

//示例模块
val exampleMenuList = arrayListOf<Any>(
    HomeInfo("好友列表", "仿QQ好友列表，分组可吸顶、折叠、展开", R.mipmap.icon_example_friend, FriendFragment::class.java),
    HomeInfo("多级分类列表", "商品分类多级菜单", R.mipmap.icon_example_goods_category, MultipleCategoryFragment::class.java),
    HomeInfo("图片选择器", "仿微信图片选择器", R.mipmap.icon_example_image_selected, ImageSelectedFragment::class.java),
    HomeInfo("分类列表联动", "商品分类与商品列表联动,分类可吸顶", R.mipmap.icon_example_goods_category, GoodsFragment::class.java),
    HomeInfo("标签选择器", "流式布局", R.mipmap.icon_example_tag),
//    HomeInfo("底部导航栏", "", R.mipmap.ic_launcher),
//    HomeInfo("TabBar", "", R.mipmap.ic_launcher),

)